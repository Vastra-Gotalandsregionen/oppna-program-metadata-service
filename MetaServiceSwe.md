# Introduction #

Metadatatjänsten är en samling av tjänster som hanterar metadata baserade på en taxonomi. I dagsläget tillhandahåller Metadatatjänsten två tjänster: Nyckelordstjänsten och Vokabulärtjänsten.

Nyckelordstjänsten används för att föreslå nyckelord till medicinska artiklar. Publiceringssystem kan anropa tjänsten för att få nyckelordsförslag till artiklar baserat på deras innehåll. Nyckelorden kommer från en medicinsk taxonomi (MeSH), samt en svensk version av denna med namn SweMeSH.

Vokabulärtjänsten används för att hämta information om noder i en existerande namnrymd. Den är främst tänkt att användas för taxonomier som är skapade av VGR själva.

# Nyckelordstjänst #

Nyckelordstjänsten (Keyword Service) är uppbyggd av tre moduler, vilka kan ses i figur 1. En artikel som skickas till tjänsten behandlas först av Format Stripper som tar bort formateringsinformation från artikeln. Stödda format är text, html, pdf och office.

#### Figure 1 ####
![http://oppna-program-metadata-service.googlecode.com/svn/wiki/images/overview.jpg](http://oppna-program-metadata-service.googlecode.com/svn/wiki/images/overview.jpg)

Den rena texten skickas sedan vidare i en kedja; Keyword Matcher extraherar ord från texten som matcher en given ordlista (just nu SweMeSH) med hjälp av ett antal inre moduler. Keyword Matcher beskrivs i ett eget avsnitt nedan. Resultatet blir en lista av medicinska termer som är kopplade till den inmatade texten. User Profile Service jämför de funna medicinska termerna mot användarens tidigare aktivitet.

Om en medicinsk term använts som bokmärke, märks termen som “bokmärkt”. Om användaren tidigare har använt termen för att beskriva en artikel märks termen som “taggad”. Slutresultatet, en lista med märkta medicinska termer skickas tillbaka till användargränssnittet.


## Keyword Matcher ##

#### Figure 2 ####
![http://oppna-program-metadata-service.googlecode.com/svn/wiki/images/KeywordMatcher.png](http://oppna-program-metadata-service.googlecode.com/svn/wiki/images/KeywordMatcher.png)

**Tokenize** : Splittrar upp dokumentet i ord genom att dela vid speciella tecken så som mellanslag, parenteser etc.

**Build N-grams** : Skapar termer genom att kombinera flera ord till större begrepp.

**Dictionary Intersection** : Tar fram snittet mellan mängden med termer i dokumentet och mängden med termer i ordlistan (SweMeSH) och resultatet är alla termer i dokumentet som även finns i ordlistan.

**Normalize and Count** : Normaliserar så att man i fortsättningen använder den föredragna termen från ordlistan, räknar även förekomsten av varje term.

**Filter** : Filtrerar bort resultat från vissa givna kategorier i ordlistan

**Sort by frequency** : Sorterar så att termer som förekommer oftast ligger högst upp

**Sort by category** : Sorterar på en bestämd prioritering av kategorier i ordlistan.

Resultatet är en lista med nyckelord som finns i dokumentet sorterad efter en relevans som bygger på frekvens och kategori.

## Analysis Service ##

#### Figure 2 ####
![http://oppna-program-metadata-service.googlecode.com/svn/wiki/images/modules.jpg](http://oppna-program-metadata-service.googlecode.com/svn/wiki/images/modules.jpg)

### Normalize ###

Normaliserar texten genom att ta bort accenter, apostrofer, punkter, kommatecken osv.

### Remove stop words ###

Tar bort stoppord, dvs små ord som inte betyder någonting. Denna modul baseras på en lista. Alla ord som finns med i listan tas bort från texten. Kan konfigureras för engelska och svenska stoppord.

### Stemming ###

Reducerar orden i texten till dess grundform. Stöd för stemming av engelska och svenska ord finns.

### Remove short words ###

Korta ord är sällan beskrivande. Detta steg tar bort korta ord som inte är definierade som stoppord. Gränsen för längden på ett kort ord kan ställas in.

### Count word frequency ###

Räknar vikten på varje ordstam i texten. Vikten baseras på antalet förekomster av ordstammen i texten och antalet dokument som innehåller denna ordstam, d.sk. tfidf (term frequency, inverse document frequency). Vikten ligger som grund till vilka ord som ska returneras.

### Remove blacklisted words ###

När indata ord resulterar i noll träffar i den medicinska taxonomin, sparar Taxonomy Service detta ord som “blacklisted”. Detta steg hämtar de svartlistade orden och tar bort dem från texten.

### Remove low frequency words ###

Ord som förekommer få gånger i texten tas bort. Gränsen för antalet förekomster kan ställas in.

### Sort words by frequency ###

Orden sorteras i fallande ordning baserat på dess frekvens i dokumentet.


# Vokabulärtjänst #

Vokabulärtjänsten (Vocabulary Service) används för att hämta information om en namnrymd lagrad i den underliggande taxonominhanteraren. Tjänsten tar in en sökväg till en nod i taxonomin och returnerar alla direkt underliggande noder. Användningsområdet är att t.ex kunna lista alla dokumenttyper i VGR:s vokabulär. Varje dokumenttyp är då en egen nod med den gemensamma föräldranoden ”Dokumenttyper”.