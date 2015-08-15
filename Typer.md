# Typer #

Under detta avsnitt presenteras de typer som tjänsten använder sig av.

<br><br>
<h2>Node</h2>

Node typen innehåller information om ett nyckelord. Denna typ har fält enligt nedanstående.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> name	       </td><td> String     </td><td> Namnet på noden. Presenteras i GUI:t </td></tr>
<tr><td> namespaceId </td><td> String     </td><td> Den unika identifieraren för namnrymden som noden tillhör i taxonomihanteraren </td></tr>
<tr><td> internalId  </td><td> String	    </td><td> Den unika identifieraren i taxonomihanteraren för noden.</td></tr>
<tr><td> sourceId    </td><td> String     </td><td> Den unika identifieraren i ursprungstaxonomin som noden representerar </td></tr>
<tr><td> parents	    </td><td> List<br>
<br>
<Node><br>
<br>
 </td><td> En lista av denna nods föräldrar ett steg upp i hierarkin </td></tr>
<tr><td> properties	 </td><td> List<br>
<br>
<NodeProperties><br>
<br>
 </td><td> En lista av denna nods egenskaper </td></tr>
<tr><td> userStatus	 </td><td> List<br>
<br>
<UserStatus><br>
<br>
 </td><td>	En lista av statusinformation. Läs mer om statusinformation nedan </td></tr>
<tr><td> synonyms    </td><td>	List<br>
<br>
<String><br>
<br>
 </td><td>	En lista med synonymer för denna nod </td></tr>
<tr><td> hasChildren	</td><td> Bool       </td><td> Flagga som indikerar om noden har barn eller inte. Obs, alltid false i Nyckelordstjänsten </td></tr></tbody></table>


<br><br>
<h2>NodeProperty</h2>

En egenskap på en nod, används främst i reviewListan för lookupWord för att spara vem som lagt in noden och vilken url noden skall associeras med.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> name	       </td><td> String     </td><td> Namnet på egenskapen, tex profileId </td></tr>
<tr><td> value       </td><td> String     </td><td> Värdet på egenskapen </td></tr></tbody></table>

<br><br>
<h2>UserStatus</h2>

UserStatus typen är en enumeration och används för att markera hur användaren har använt ett nyckelord. I dagsläget kan en användare välja att bokmärka ett nyckelord, och systemet kan märka ett nyckelord som taggat. Ett taggat nyckelord innebär att det har använts för att beskriva ett dokument av den aktuella användaren tidigare.<br>
<br>
Tillåtna värden på UserStatus är: bookmarked, taggged.<br>
<br>
<br>
<br><br>
<h2>Identification</h2>

Identification är ett object som identifierar användaren och en profil.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> userId      </td><td> String     </td><td> Identiferar användaren </td></tr>
<tr><td> profileId   </td><td> String     </td><td> Identifierar en profil </td></tr></tbody></table>



<br><br>
<h2>Document</h2>

Document är ett interface som används av tre klasser:<br>
<ul><li>DocumentImpl<br>
</li><li>FileDocument<br>
</li><li>TextDocument</li></ul>

<code>DocumentImpl</code> innehåller fullständig information om ett document och är inte tänkt att användas för sig. Istället är det tänkt att <code>FileDocument</code> och <code>TextDocument</code> ska användas, vilka implementerar valda delar ur <code>DocumentImpl</code> klassen.<br>
<br>
<br>
<br><br>
<h3>TextDocument</h3>

TextDocument representerar ett dokument innehållande text eller HTML text.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> title       </td><td> String     </td><td> Titel på dokumentet </td></tr>
<tr><td> textContent </td><td> String     </td><td> Dokumentets textinnehåll (utf-8 kodat) </td></tr></tbody></table>


<br><br>
<h3>FileDocument</h3>

FileDocument representerar en fil som t.ex. file.html, file.pdf, file.odt etc.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> filename    </td><td> String     </td><td> Fullständigt filnamn  </td></tr>
<tr><td> inputStream </td><td> InputStream </td><td> En ström till filinnehållet  </td></tr>
<tr><td> encoding    </td><td> String     </td><td> Kodning av innehållet i strömmen, t.ex. utf-8 </td></tr></tbody></table>


<br><br>

<h2>Options</h2>

Options är ett object som anger valbara parametrar för en request.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> inputWords  </td><td> int        </td><td> Antal ord som getKeywords baserar sin nyckelordsgenerering på. Default värde är 10 </td></tr>
<tr><td> wordsToReturn </td><td> int        </td><td> Antal ord som ska returneras från getKeywords. Default värde är 10 </td></tr>
<tr><td> includeSourceIds </td><td> Map<Integer,List<br>
<br>
<String><br>
<br>
> </td><td> Används för att begränsa sökresultatet till en delmängd av ett namespace.  En map med namespaceId som nyckel och en lista av sourceIds som sökningen ska begränsas till för det angivna namespaceId:t </td></tr>
<tr><td> url         </td><td> String     </td><td> Url som requesten kommer ifrån. Kan användas med VocabularyService.LookupWord; om ordet läggs till i taxonomin, sparas url:en som en egenskap för ordet </td></tr>
<tr><td> filterByProperties </td><td> Map<String, List<br>
<br>
<String><br>
<br>
> </td><td> En map med en lista av filter. Kan användas med VocabularyService.getVocabulary för att endast returnera de noder som har en property som matchar en av strängarna i listan.  </td></tr>
<tr><td> matchSynonyms </td><td> boolean    </td><td> Om synonymer ska matcha när sökning görs i Apelon. Används i VocabularyService.lookupWord. </td></tr>
<tr><td> synomize    </td><td> boolean    </td><td> Satt till true kommer lemmatiseringstjänsten användas för att berika ett ord med synonymer. Används i VocabularyService.addVocabularyNode , VocabularyService.moveVocabularyNode, och VocabularyService.updateVocabularyNode. </td></tr></tbody></table>

<br><br>

<h3>ResponseObject</h3>

ResponseObject används som standard svar till funktioner som vanligtvis inte har något resultat att ge. Objektet har en status kod, ett eventuellt felmeddelande och requestId för den inkommande frågan om något sådant angavs.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> requestId   </td><td> String     </td><td> Identifieraren på den inkommande requesten som det här svaret tillhör </td></tr>
<tr><td> statusCode  </td><td> Enum       </td><td> Enumerationstyp som markerar om anropet lyckades eller ej </td></tr>
<tr><td> errorMessage </td><td> String     </td><td> Ett eventuellt felmeddelande om anropet misslyckades </td></tr></tbody></table>


<br><br>
<h3>NodeListResponseObject</h3>

NodeListResponseObject är en utökning av ResponseObject. Utöver den information som ResponseObject ger så returneras även en lista av Noder<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> requestId   </td><td> String     </td><td> Identifieraren på den inkommande requesten som det här svaret tillhör </td></tr>
<tr><td> statusCode  </td><td> Enum       </td><td> Enumerationstyp som markerar om anropet lyckades eller ej </td></tr>
<tr><td> errorMessage </td><td> String     </td><td> Ett eventuellt felmeddelande om anropet misslyckades </td></tr>
<tr><td> nodeList    </td><td> List<br>
<br>
<Node><br>
<br>
 </td><td> En list av Node objekt </td></tr></tbody></table>

<br><br>
<h3>StatusCode</h3>
En enumerationstyp för statuskoder.<br>
<table><thead><th> <b>Felkod</b> </th><th> <b>Felmeddelande</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> 1001          </td><td> Unsupported text format </td><td> Textformatet som angetts i format-parametern stöds inte av metadatatjänsten </td></tr>
<tr><td> 1002          </td><td> Error formatting content </td><td> Ett fel uppstod när inkommande texten konverterades </td></tr>
<tr><td> 2001          </td><td> Error processing content </td><td> Ett fel uppstod när indatan processerades </td></tr>
<tr><td> 3001          </td><td> Error getting keywords from taxonomy </td><td> Ett fel uppstod när nyckelorden skulle hämtas från taxonomihanteraren </td></tr>
<tr><td> 3002          </td><td> Error editing taxonomy: <detailed message> </td><td> Ett fel uppstod när användaren försökte ändra i taxonomin. Det specialiserade meddelandet ger mer info. Ex. Flytta nod, skapa nod. </td></tr>
<tr><td> 3003          </td><td> insufficient_namespace_privileges </td><td> Markerar att användaren inte har läs eller skrivrättigheter till namnrymden, alternativet att namnrymden inte är konfigurerad för export vid funktionsanropet getNamespaceXML. </td></tr>
<tr><td> 3004          </td><td> invalid_parameter    </td><td> En felaktig inparameter har angivits, t.ex. en ogiltig namnrymd eller sökväg. </td></tr>
<tr><td> 3005          </td><td> error_locating_namespace </td><td> Ett fel uppstod när namnrymden för ett namn eller id skulle hämtas. Uppstår om t.ex. fel namnrymd angivits. </td></tr>
<tr><td> 3006          </td><td> error_resolving_property </td><td> Ett fel uppstod när en property skulle hämtas. Kan uppstå t.ex. då en property inte finns. </td></tr>
<tr><td> 3007          </td><td> invalid_node_property </td><td> Ett fältvärde i en nod är felaktigt </td></tr>
<tr><td> 3008          </td><td> error_storing_property </td><td> Ett fel uppstod när en property skulle lagras </td></tr>
<tr><td> 3009          </td><td> error_locating_node  </td><td> Det gick inte att hitta begärd nod </td></tr>
<tr><td> 3010          </td><td> error_locating_profile </td><td> Ett fel uppstod när profilen motsvarande ett visst profilId skulle hämtas. </td></tr>
<tr><td> 6001          </td><td> Unknown error occured  </td><td> Ett oväntat fel uppstod, kolla loggen för detaljer </td></tr></tbody></table>


<br><br>

<h3>XMLResponseObject</h3>

XMLResponseObject används för att returnera XML information. Typen följer samma principer som ResponseObject med den skillnaden att ett fält för XML information är tillagt.<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> xml         </td><td> String     </td><td> XML information som genererats som svar på anropet </td></tr>
<tr><td> requestId   </td><td> String     </td><td> Identifieraren på den inkommande requesten som det här svaret tillhör </td></tr>
<tr><td> time        </td><td> Long       </td><td> Tiden då detta anropet gjordes </td></tr>
<tr><td> statusCode  </td><td> Enum       </td><td> Enumerationstyp som markerar om anropet lyckades eller ej </td></tr>
<tr><td> errorMessage </td><td> String     </td><td> Ett eventuellt felmeddelande om anropet misslyckades </td></tr></tbody></table>


<br><br>

<h3>LastChangeResponseObject</h3>

LastChangeResponseObject används för att returnera tiden för senaste ändringen i apelon<br>
<br>
<table><thead><th> <b>Namn</b> </th><th> <b>Typ</b> </th><th> <b>Förklaring</b> </th></thead><tbody>
<tr><td> requestId   </td><td> String     </td><td> Identifieraren på den inkommande requesten som det här svaret tillhör </td></tr>
<tr><td> time        </td><td> Long       </td><td> Tiden då något senast ändrades </td></tr>
<tr><td> statusCode  </td><td> Enum       </td><td> Enumerationstyp som markerar om anropet lyckades eller ej </td></tr>
<tr><td> errorMessage </td><td> String     </td><td> Ett eventuellt felmeddelande om anropet misslyckades </td></tr>