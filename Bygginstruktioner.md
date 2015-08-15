#Instruktioner för att bygga

# Bygginstruktioner #
Checka ut källkoden

I projektet KeywordService-svc, skapa filen keywordservice-svc.properties i src/main/resources
Den ska innehålla fem properties för metadatatjänsten:
```
# Apelon connection details
taxonomyservice.host=
taxonomyservice.port=
taxonomyservice.username=
taxonomyservice.password=
# A comma separated list of namespaces we wish to expose to the routine getNamespaceXML(..). 
taxonomyservice.allowedNamespaces=
```
Fyll den med dina login-uppgifter till Apelon.
Skapa en likadan fil i VocabularyService-svc men döp den till vocabularyservice-svc.properties.

Skapa en databas för UserProfileService. I det här exemplet har vi använt en Derby-databas som vi döpt till metaservice. Den kör på port 1527. Sätt upp den som en dataSource i Tomcat, genom att öppna conf/context.xml och lägg till följande:
```
<Resource name="jdbc/MetaServiceDB" auth="Container" type="javax.sql.DataSource"
               maxActive="100" maxIdle="30" maxWait="10000"
               username="username" password="password" driverClassName="org.apache.derby.jdbc.ClientDriver"
               url="jdbc:derby://localhost:1527/metaservice"/>
```
Ändra username,password,driverClassName och url för att passa din installation.
Lägg till jdbc-drivrutinen för databasen i tomcats lib-katalog. För Derby används derbyclient.jar


## Sökprofiler ##
Om sökprofiler används ska dessa definieras i både keywordservice och vocabularyservice i respective svc projekt. Exempelfiler enligt nedan:

searchprofiles-config.xml
```
<bean id="searchProfile.vgr" class="se.vgregion.metaservice.keywordservice.domain.SearchProfile">
        <!-- profileId defined in searchprofiles.properties -->
        <property name="profileId" value="${vgr.profileid}" />
        <property name="searchableNamespaces">
            <set>
                <value>VGR</value>
            </set>
        </property>
        <property name="writeableNamespaces">
            <set>
                <value>VGR</value>
            </set>
        </property>
        <property name="blackList">
            <bean class="se.vgregion.metaservice.keywordservice.domain.NodePath">
                <property name="path" value="VGR/Blacklist" />
            </bean>
        </property>
        <property name="whiteList">
            <bean class="se.vgregion.metaservice.keywordservice.domain.NodePath">
                <property name="path" value="VGR/Whitelist" />
            </bean>
        </property>
        <property name="reviewList">
            <bean class="se.vgregion.metaservice.keywordservice.domain.NodePath">
                <property name="path" value="VGR/Reviewlist" />
            </bean>
        </property>
    </bean>
```

searchprofiles.config
```
vgr.profileid=hard_to_guess_profileId
```


# Lemmatisering #
VocabularyService använder sig av en tjänst för att lemmatisera ord. Denna kräver en att en propertes-fil finns tillgänglig i resources-mappen för svc-projektet; lemmatisation.properties:

```

# The URL to where the lemmatisation service is located.
#
lemmatisation.baseurl=http://localhost:8080/MetaService-LemmatisationService-module-intsvc

```

När lemmatiseringstjänsten körs på en linux server så måste text-filen "saldo.txt" manuellt kopieras till "classes"-mappen. Filen finns tillgänglig som default som en dependency till svc-projektet.


# Instruktioner för SitemapGenerator #
SitemapGenerator är en standalone applikation i metaservice projektet. Applikationen genererar en sitemap för varje angiven namnrymd. Rekomenderad körning är att applikationen startas via en enkel .bat fil, schemalagd enligt operativsystemet inbyggda schemaläggninigresurser.


När projektet byggs ska _sitemapgenerator.properties_ från resource mappen uppdateras:

```
# Directory to store the sitemaps in, e.g., C:\\sitemaps\ for windows or /var/sitemaps/ for linux
# Must be slash ('/') terminated
sitemapgenerator.sitemapLocation=C:\\my_sitemaps\

# The static requestId to use when sitemapgenerator invokes metaservice.vocabularyservice.getVocabulary.
sitemapgenerator.requestId=sitemapGenerator

# The base location used as prefix to each node when generating the url property of the sitemap. 
# Must be slash ('/') terminated
sitemapgenerator.sitePrefix=http://localhost:8081/dtstreebrowser/
```

Därefter behör _pom.xml_-filen i applications mappen uppdateras:
```
  [...]

  <wsdlUrl>
   <!-- Update the location host to where the VocabularyService is actually deployed -->
   http://localhost:8080/MetaService-VocabularyService-module-intsvc-1.0-SNAPSHOT/VocabularyService?wsdl
  </wsdlUrl>

  [...]
```

När projektet byggs skapas en zip-fil. Packa upp zipfilen där den ska köras och anropa batscriptet som följer med för att skapa en sitemap. För att välja vilka namnrymder som ska exponeras så görs detta via att redigera bat-skriptet i en texteditor.