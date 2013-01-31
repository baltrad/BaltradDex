/*******************************************************************************
*
* Copyright (C) 2009-2013 Institute of Meteorology and Water Management, IMGW
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

package eu.baltrad.dex.config.manager;

import eu.baltrad.dex.config.model.AppConfiguration;
import eu.baltrad.dex.config.model.LogConfiguration;
import eu.baltrad.dex.config.model.RegistryConfiguration;

/**
 * Configuration manager interface.
 *
 * @author Maciej Szewczykowski | maciej@baltrad.eu
 * @version 1.7.1
 * @since 1.2.2
 */
public interface IConfigurationManager {
    
    public AppConfiguration getAppConf();
   
    public LogConfiguration getLogConf();
     
    public RegistryConfiguration getRegistryConf();
    
    public void saveAppConf(AppConfiguration conf);
    
    public void saveLogConf(LogConfiguration conf);
    
    public void saveRegistryConf(RegistryConfiguration conf);
    
}
