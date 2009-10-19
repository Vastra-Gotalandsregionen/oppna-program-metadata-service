@echo off


REM -------------------------------------------------------------
REM   Space-separated list of namespaces to generate sitemap for
REM -------------------------------------------------------------

SET NAMESPACES="VGR"



REM -------------------------------------------------------------
REM   The jar file to execute
REM -------------------------------------------------------------

SET JARFILE="MetaService-SitemapGenerator-app-1.0-SNAPSHOT.jar"



REM -------------------------------------------------------------
REM   The entry point of the java application (the main class)
REM -------------------------------------------------------------

SET MAINCLASS="se.vgregion.metaservice.sitemapgenerator.SitemapGenerator"



REM -------------------------------------------------------------
REM   The command to invoke the sitemapGenerator once
REM -------------------------------------------------------------

java -cp %JARFILE% %MAINCLASS% %NAMESPACES%