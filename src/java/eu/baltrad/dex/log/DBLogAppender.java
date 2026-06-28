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
 ******************************************************************************/

package eu.baltrad.dex.log;

import java.io.Serializable;

import eu.baltrad.dex.log.model.impl.LogEntry;
import eu.baltrad.dex.log.manager.impl.LogManager;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "DBLogAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class DBLogAppender extends AbstractAppender {

    private static volatile LogManager logManager = null;

    public DBLogAppender() {
        super("DBLogAppender", null, null, true, Property.EMPTY_ARRAY);
    }

    protected DBLogAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
    }

    @PluginFactory
    public static DBLogAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for DBLogAppender");
            return null;
        }
        return new DBLogAppender(name, filter, layout);
    }

    public synchronized void setLogManager(LogManager manager) {
        if (logManager == null) {
            logManager = manager;
        }
    }

    @Override
    public void append(LogEvent event) {
        if (logManager != null) {
            logManager.store(new LogEntry(
                event.getTimeMillis(),
                event.getLoggerName(),
                event.getLevel().name(),
                event.getMessage().getFormattedMessage()
            ));
        }
    }
}
