/*
 @licstart  The following is the entire license notice for the JavaScript code in this file.

 The MIT License (MIT)

 Copyright (C) 1997-2020 by Dimitri van Heesch

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 @licend  The above is the entire license notice for the JavaScript code in this file
*/
var NAVTREE =
[
  [ "BaltradDex", "index.html", [
    [ "BaltradDex documentation", "index.html", "index" ],
    [ "Data exchange format", "format.html", [
      [ "General information", "format.html#exchange_general", null ],
      [ "Authentication", "format.html#exchange_auth", [
        [ "Authentication header", "format.html#exchange_hdr", null ],
        [ "Signing a request", "format.html#exchange_sign", null ]
      ] ],
      [ "Data source listing request", "format.html#exchange_datasource_listing", null ],
      [ "Start subscription request", "format.html#exchange_start_subscription", null ],
      [ "Update subscription request", "format.html#exchange_update_subscription", null ],
      [ "Post key request", "format.html#exchange_post_key", null ],
      [ "Post file request", "format.html#exchange_post_file", null ],
      [ "Post message request", "format.html#exchange_post_message", null ]
    ] ],
    [ "Installation", "install.html", [
      [ "Prerequisites", "install.html#prereq", null ],
      [ "Preparation", "install.html#prep", [
        [ "Create database", "install.html#prep_db", null ],
        [ "Configure Tomcat", "install.html#prep_tomcat", [
          [ "Prepare certificate keystore", "install.html#prep_tomcat_keystore", null ],
          [ "Configure connectors", "install.html#prep_tomcat_connectors", null ],
          [ "Configure administrative account", "install.html#prep_tomcat_admin_account", null ]
        ] ],
        [ "Add logrotate on the tomcat logs", "install.html#prep_tomcat_logrotate", null ],
        [ "Configure application context", "install.html#prep_app_context", null ]
      ] ],
      [ "Build & install BaltradDex", "install.html#build_dex", null ],
      [ "Testing", "install.html#test_dex", null ]
    ] ],
    [ "Introduction", "intro.html", [
      [ "About", "intro.html#about", null ],
      [ "Project structure", "intro.html#structure", null ],
      [ "Generating documentation", "intro.html#gendocs", null ]
    ] ],
    [ "Running BaltradDex", "running.html", [
      [ "Starting BaltradDex", "running.html#start", null ],
      [ "Login/Logout", "running.html#login", null ],
      [ "Change user's password", "running.html#change_password", null ],
      [ "Quick setup", "running.html#quick_setup", [
        [ "Node settings", "running.html#quick_node_settings", null ],
        [ "Radars", "running.html#quick_radars", null ],
        [ "Data sources", "running.html#quick_data_sources", null ],
        [ "Import injector's key", "running.html#quick_injector", null ],
        [ "Exchange keys between nodes", "running.html#quick_keys", null ]
      ] ],
      [ "Connecting to peer node", "running.html#conn", null ],
      [ "Managing subscriptions", "running.html#subscribe", null ],
      [ "Checking node status", "running.html#node_status", null ],
      [ "Data delivery registry", "running.html#registry", null ],
      [ "Message logging", "running.html#log", null ],
      [ "Sticky messages", "running.html#sticky_messages", null ],
      [ "More on data sources", "running.html#more_data_sources", null ],
      [ "Data browsing and access", "running.html#data_browse", null ],
      [ "Data processing", "running.html#data_processing", null ],
      [ "Administrative tasks", "running.html#admin", [
        [ "Radars", "running.html#radars", null ],
        [ "Data sources", "running.html#data_sources", null ],
        [ "Subscription settings", "running.html#subscription_settings", null ],
        [ "Delivery registry settings", "running.html#registry_settings", null ],
        [ "Messages settings", "running.html#messages_settings", null ],
        [ "User account management", "running.html#accounts", null ],
        [ "Node settings", "running.html#node_settings", null ]
      ] ]
    ] ],
    [ "Supervisor", "supervisor.html", [
      [ "Supervisor", "supervisor.html#supervisor", [
        [ "Overview", "supervisor.html#supervisor_overview", null ],
        [ "Administration", "supervisor.html#supervisor_admin", null ],
        [ "Query interface", "supervisor.html#supervisor_qinterface", null ],
        [ "Return codes", "supervisor.html#supervisor_returncodes", null ],
        [ "Formatting of responses", "supervisor.html#supervisor_formatting", null ],
        [ "Reporters API", "supervisor.html#supervisor_reporters", null ],
        [ "Reporter name: db.status", "supervisor.html#supervisor_reporters_db_status", [
          [ "Arguments", "supervisor.html#supervisor_reporters_db_args", null ],
          [ "Example usage", "supervisor.html#supervisor_reporters_db_example", null ]
        ] ],
        [ "Reporter name: bdb.status", "supervisor.html#supervisor_reporters_bdb_status", null ],
        [ "Arguments", "supervisor.html#supervisor_reporters_bdb_args", [
          [ "Example usage", "supervisor.html#supervisor_reporters_bdb_example", null ]
        ] ],
        [ "Reporter name: radar.connection.status", "supervisor.html#supervisor_reporters_radar_connection_status", [
          [ "Arguments", "supervisor.html#supervisor_reporters_radar_connection_args", null ],
          [ "Example usage", "supervisor.html#supervisor_reporters_radar_connection_example", null ]
        ] ],
        [ "Reporter name: bdb.object.status", "supervisor.html#supervisor_reporters_bdb_object_status", [
          [ "Arguments", "supervisor.html#supervisor_reporters_bdb_object_args", null ],
          [ "Example usage", "supervisor.html#supervisor_reporters_bdb_object_example", null ]
        ] ]
      ] ]
    ] ],
    [ "Support and feedback", "support.html", null ],
    [ "Troubleshooting", "trouble.html", null ],
    [ "Classes", "annotated.html", [
      [ "Class List", "annotated.html", "annotated_dup" ],
      [ "Class Index", "classes.html", null ],
      [ "Class Hierarchy", "hierarchy.html", "hierarchy" ],
      [ "Class Members", "functions.html", [
        [ "All", "functions.html", "functions_dup" ],
        [ "Functions", "functions_func.html", "functions_func" ],
        [ "Variables", "functions_vars.html", null ]
      ] ]
    ] ]
  ] ]
];

var NAVTREEINDEX =
[
"annotated.html",
"classeu_1_1baltrad_1_1dex_1_1config_1_1model_1_1AppConfiguration.html#a2e09a1f58f51320d33722941bc4960c1",
"classeu_1_1baltrad_1_1dex_1_1db_1_1controller_1_1BltImagePreviewController.html#a427bb0494a91acaa34283a16604e0b7d",
"classeu_1_1baltrad_1_1dex_1_1log_1_1model_1_1impl_1_1LogParameter.html#ae04ce4b6b853c4ebd93c484303df2e72",
"classeu_1_1baltrad_1_1dex_1_1net_1_1servlet_1_1AdministratorServlet.html#aa4e37a6110bcc630bf4468733cc86229",
"classeu_1_1baltrad_1_1dex_1_1registry_1_1model_1_1impl_1_1RegistryEntry.html#ad13eade8eff601b574fa3f50a84c1093",
"functions_func_z.html"
];

var SYNCONMSG = 'click to disable panel synchronization';
var SYNCOFFMSG = 'click to enable panel synchronization';