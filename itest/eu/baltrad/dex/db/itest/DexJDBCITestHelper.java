/*******************************************************************************
*
* Copyright (C) 2009-2012 Institute of Meteorology and Water Management, IMGW
*
* This file is part of the BaltradDex software.
*
* BaltradDex is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* BaltradDex is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with the BaltradDex software.  If not, see http://www.gnu.org/licenses.
*
*******************************************************************************/

package eu.baltrad.dex.db.itest;

import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

/**
 * Baltrad-db and Beast integration test helper.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.0.7
 * @since 1.0.7
 */
public class DexJDBCITestHelper {
    
    private JdbcTemplate jdbcTemplate = null;
    private SimpleJdbcInsert insertDs = null;
    private SimpleJdbcInsert insertFilter = null;
    
    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.insertDs = new SimpleJdbcInsert(dataSource)
              .withTableName("dex_data_sources").usingGeneratedKeyColumns("id");
        this.insertFilter = new SimpleJdbcInsert(dataSource)
              .withTableName("dex_data_source_filters")
              .usingGeneratedKeyColumns("id");
    }
    
    public int saveDataSource(eu.baltrad.dex.datasource.model.DataSource ds) 
    {
        Map<String, Object> parms = new HashMap<String, Object>(2);
        parms.put("name", ds.getName());
        parms.put("type", ds.getType());
        parms.put("description", ds.getDescription());
        return insertDs.executeAndReturnKey(parms).intValue();
    }
    
    public void deleteDataSources() {
        this.jdbcTemplate.update("delete from dex_data_sources");
    }
    
    public int saveCombinedFilter(int dataSourceId, int filterId) {
        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("data_source_id", dataSourceId);
        parms.put("filter_id", filterId);
        return insertFilter.executeAndReturnKey(parms).intValue();
    }
    
    public int getCombinedFilterId(int dataSourceId) {
        String sql = "SELECT filter_id FROM dex_data_source_filters WHERE " +
                    " data_source_id = ?";
        int filterId = this.jdbcTemplate.queryForInt(sql, 
                                                   new Integer[]{dataSourceId});
        return filterId;
    }
    
    public void deleteCombinedFilters() {
        this.jdbcTemplate.update("delete from beast_combined_filter_children");
        this.jdbcTemplate.update("delete from beast_combined_filters");
    }
}
