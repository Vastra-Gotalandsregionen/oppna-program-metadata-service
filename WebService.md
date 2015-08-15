# Web Service Nyckelordtjänst #
WSDL-url: http://metadataservice.vgregion.se/keywordservice/KeywordService?wsdl


&lt;hr&gt;


## getKeywords ##

getKeywords är huvudoperationen, där man skickar in en artikel och får tillbaka en lista av nyckelord som passar in på artikeln.

**Inparametrar**

| **Namn**     | **Typ**          | **Förklaring** |
|:-------------|:-----------------|:---------------|
| id           | Identification   | Användaridentifikation |
| requestId    | String	          | Unik anropsidentifierare (loggsyfte) |
| document     | Document         | Text- eller fildokument (TextDocument eller FileDocument) |
| options      | Options          | Valfria extra-parametrar |


**Förväntade options-värden**
  * inputWords - Max antal ord ur texten som skall användas för att generera nyckelord
  * wordsToReturn - Max antal ord som skall returneras
  * includeSourceId - De källor som ska användas


**Returvärde**
_NodeListResponseObject med requestId, statuskod, felmeddelande och en lista av Node objekt_

<br><br>
<h2>getNodeByInternalId</h2>

getNodeByInternalId används för att returnera en nod via dess interna id nummer.<br>
<br>
<b>Inparametrar</b>

<table><thead><th> <b>Namn</b>	</th><th> <b>Typ</b>	</th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id           </td><td> Identification </td><td> Användaridentifikation </td></tr>
<tr><td> requestId    </td><td> String	        </td><td> Unik anropsidentifierare (loggsyfte) </td></tr>
<tr><td> internalId	 </td><td> String	    </td><td> Det interna id numret på en nod</td></tr></tbody></table>


<b>Returvärde</b>
<i>NodeListResponseObject med statuskod, felmeddelande och en lista av Node objekt</i>


<br><br>
<h2>bookmarkKeywords</h2>

bookmarkKeywords används för att bokmärka nyckelord för en användare.<br>
<br>
<b>Inparametrar</b>

<table><thead><th> <b>Namn</b>	</th><th> <b>Typ</b>	</th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id           </td><td> Identification </td><td> Användaridentifikation </td></tr>
<tr><td> requestId   </td><td> String	        </td><td> Unik anropsidentifierare (loggsyfte) </td></tr>
<tr><td>keywordIds	  </td><td>List<br>
<br>
<String><br>
<br>
	</td><td>En lista av ids för varje nyckelord som ska läggas till som bokmärke.</td></tr></tbody></table>

<b>Returvärde</b>
<i>ResponseObject med statuskod och ev. felmeddelande</i>


<br><br>
<h2>tagKeywords</h2>

tagKeywords används för att markera ett nyckelord som taggat. Det innebär att nyckelordet har används för att beskriva ett dokument. Bör kallas automatiskt av klienten när användaren valt nyckelord för en artikel.<br>
<br>
<b>Inparametrar</b>

<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b>	</th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id           </td><td> Identification </td><td> Användaridentifikation </td></tr>
<tr><td> requestId   </td><td> String	        </td><td> Unik anropsidentifierare (loggsyfte) </td></tr>
<tr><td> keywordIds	 </td><td> List<br>
<br>
<String><br>
<br>
	</td><td> En lista av ids för varje nyckelord som ska markeras som taggat.</td></tr></tbody></table>

<b>Returvärde</b>
<i>ResponseObject med statuskod och ev. felmeddelande</i>


<br><br>

<h1>Web Service Vokabulärtjänst</h1>
WSDL-url: <a href='http://metadataservice.vgregion.se/vocabularyservice/VocabularyService?wsdl'>http://metadataservice.vgregion.se/vocabularyservice/VocabularyService?wsdl</a>
<br>
<br>
<hr><br>
<br>
<br>
Vokabulärtjänsten exponeras i dagsläget som en Web Service. Nedan beskrivs den operation som är exponerad. De egenutvecklade typerna som används är definierade i avsnittet Typer.<br>
<br>
<br>
<br>
<h2>GetVocabulary</h2>

getVocabulary returnerar alla direkt underliggande noder till en nod.<br>
<br>
<b>Inparametrar</b>

<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> requestId   </td><td> String     </td><td> Unik anrops-identifierare </td></tr>
<tr><td> path	       </td><td> String     </td><td> Sökvägen till noden </td></tr>
<tr><td> options     </td><td> Options    || Alternativa parametrar att skicka med requesten.</td></tr></tbody></table>

<b>Förväntade options-värden</b>
<ul><li>filterByProperties - Endast returnera noder som har de properties som specificeras.</li></ul>

<b>Returvärde</b>
<i>NodeListResponseObject med statuskod, felmeddelande och en lista av Node objekt</i>


<br><br>
<h2>lookupWord</h2>

lookupWord används för att se om ett ord existerar i någon av de fördefinierade listorna <code>whitelist</code> eller <code>blacklist</code>. Om det inte existerar så läggs ordet till i en review lista. Ett options-objekt kan skickas med för att associera vissa egenskaper till ordet.<br>
<br>
<b>Inparametrar</b>

<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b>	</th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id           </td><td> Identification </td><td> Användaridentifikation </td></tr>
<tr><td> requestId   </td><td> String	        </td><td> Unik anropsidentifierare (loggsyfte) </td></tr>
<tr><td> word        </td><td> String	        </td><td> Ordet att leta efter i listorna </td></tr>
<tr><td> options     </td><td> Options	        </td><td> Alternativa parametrar att skicka med requesten </td></tr></tbody></table>

<b>Förväntade options-värden</b>
<ul><li>url - Om ordet läggs till i taxonomin, sparas url:en som en egenskap för ordet.</li></ul>

<b>Returvärde</b>
<i>ListType vilken är en enumerationstyp som kan innehålla värdena <code>whitelist</code>, <code>blacklist</code>, eller <code>none</code>.</i>


<br><br>

<br><br>
<h2>addVocabularyNode</h2>

Lägger till en ny vokabulärnod som undernod till en existerande nod.<br>
<br>
<b>Inparametrar</b>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id          </td><td> Identification </td><td> Objekt som identifierar användare och profil </td></tr>
<tr><td> requestId   </td><td> String     </td><td> Unik anrops-identifierare </td></tr>
<tr><td> node        </td><td> Node       </td><td> Ett nytt nod-objekt som innehåller en förälder relation </td></tr>
<tr><td> options     </td><td> Options    </td><td> Om värdet synonymize är satt till true kommer lemmatiseringstjänsten anropas för att försöka berika den lagrade noden med synonymer. </td></tr></tbody></table>

<b>Returvärde</b>
<i>ResponseObject statuskod och ev. felmeddelande</i>


<br><br>
<h2>moveVocabularyNode</h2>

Flyttar en vokabulärnod från en nod till en annan.<br>
<br>
<b>Inparametrar</b>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id          </td><td> Identification </td><td> Objekt som identifierar användare och profil </td></tr>
<tr><td> requestId   </td><td> String     </td><td> Unik anrops-identifierare </td></tr>
<tr><td> nodeId      </td><td> String     </td><td> Id för den nod som ska flyttas </td></tr>
<tr><td> destParentNodeId </td><td> String     </td><td> Id till den nod som ska bli ny föräldernod, prefixat av namnutrymmet (namespace) </td></tr>
<tr><td> options     </td><td> Options    </td><td> Om värdet synonymize är satt till true kommer lemmatiseringstjänsten anropas för att försöka berika den lagrade noden med synonymer. </td></tr></tbody></table>

<b>Returvärde</b>
<i>ResponseObject statuskod och ev. felmeddelande</i>


<br><br>
<h2>updateVocabularyNode</h2>

Uppdaterar innehållet i en vokabulärnod.<br>
<br>
<b>Inparametrar</b>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id          </td><td> Identification </td><td> Objekt som identifierar användare och profil </td></tr>
<tr><td> requestId   </td><td> String     </td><td> Unik anrops-identifierare </td></tr>
<tr><td> node        </td><td> Node       </td><td> Den uppdaterade noden (med oförändrat nodeId) </td></tr>
<tr><td> options     </td><td> Options    </td><td> Om värdet synonymize är satt till true kommer lemmatiseringstjänsten anropas för att försöka berika den lagrade noden med synonymer. </td></tr></tbody></table>

<b>Returvärde</b>
<i>ResponseObject statuskod och ev. felmeddelande</i>


<br><br>
<h2>getNamespaceXML</h2>

Hämtar den fulla strukturen för en namnrymd och presenterar detta som XML. <br>
Godkända namnrymder konfigureras i en properties-fil. Se <a href='Bygginstruktioner.md'>Bygginstruktioner</a> för vidare konfiguration.<br>
<br>
<br>
<b>Inparametrar</b>

<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id          </td><td> Identification </td><td> Objekt som identifierar användare och profil </td></tr>
<tr><td> requestId   </td><td> String     </td><td> Unik anrops-identifierare </td></tr>
<tr><td> namespace   </td><td> String     </td><td> Identifierare för vilken namnrymd sitemap:en ska genereras för </td></tr></tbody></table>


<b>Returvärde</b>
<i>XMLResponseObject innehållande svars-XML, statuskod och ev. felmeddelande</i>

<br><br>
<h2>getLastChange</h2>

Returnerar när något senast ändrades i apelon<br>
<br>
<br>
<b>Inparametrar</b>

<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> id          </td><td> Identification </td><td> Objekt som identifierar användare och profil </td></tr>
<tr><td> requestId   </td><td> String     </td><td> Unik anrops-identifierare </td></tr>
<tr><td> namespace   </td><td> String     </td><td> Anger vilken namnrymd som tidsändringen gäller </td></tr></tbody></table>


<b>Returvärde</b>
<i>LastChangeResponseObject innehållande tid, statuskod och ev. felmeddelande</i>