/*******************************************************************************
 *
 * Copyright (C) 2026- Swedish Meteorological and Hydrological Institute, SMHI
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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Priority;
import org.apache.logging.log4j.core.util.NetUtils;

/**
 * Classic syslog messages with a fixed appName/procId tag (e.g. "java[baltrad-dex]:").
 */
@Plugin(name = "BltSyslogLayout", category = Core.CATEGORY_NAME, elementType = Layout.ELEMENT_TYPE,
        printObject = true)
public class BltSyslogLayout extends AbstractStringLayout {
    private static final String DEFAULT_APP_NAME = "java";
    private static final String DEFAULT_PROC_ID = "-";
    private static final String DEFAULT_PATTERN = "%m";

    private final Facility facility;
    private final String appName;
    private final String procId;
    private final String localHostname;
    private final PatternLayout messageLayout;

    protected BltSyslogLayout(Facility facility, String appName, String procId,
         PatternLayout messageLayout, Charset charset) {
        super(charset);
        this.facility = facility;
        this.appName = appName;
        this.procId = procId;
        this.messageLayout = messageLayout;
        this.localHostname = NetUtils.getLocalHostname();
    }

    @PluginFactory
    public static BltSyslogLayout createLayout(
            @PluginAttribute(value = "facility", defaultString = "LOCAL0") Facility facility,
            @PluginAttribute("appName") String appName,
            @PluginAttribute("procId") String procId,
            @PluginAttribute("pattern") String pattern,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset,
            @PluginConfiguration Configuration config) {
        PatternLayout messageLayout = PatternLayout.newBuilder()
                .setPattern(pattern == null ? DEFAULT_PATTERN : pattern)
                .setConfiguration(config)
                .build();
        return new BltSyslogLayout(
                facility,
                appName == null ? DEFAULT_APP_NAME : appName,
                procId == null ? DEFAULT_PROC_ID : procId,
                messageLayout,
                charset == null ? StandardCharsets.UTF_8 : charset);
    }

    @Override
    public String toSerializable(LogEvent event) {
        String timestamp = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.ENGLISH)
                .format(new Date(event.getTimeMillis()));
        StringBuilder sb = new StringBuilder();
        sb.append('<').append(Priority.getPriority(facility, event.getLevel())).append('>');
        sb.append(timestamp).append(' ');
        sb.append(localHostname).append(' ');
        sb.append(appName).append('[').append(procId).append("]: ");
        sb.append(messageLayout.toSerializable(event));
        sb.append('\n');
        return sb.toString();
    }
}
