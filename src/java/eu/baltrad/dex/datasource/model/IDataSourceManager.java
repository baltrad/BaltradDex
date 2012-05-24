/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.baltrad.dex.datasource.model;

import java.util.List;

/**
 *
 * @author szewczenko
 */
public interface IDataSourceManager {
    
    public List<DataSource> load(int id);
    
}
