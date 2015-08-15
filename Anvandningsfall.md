# Användardefinerade etiketter #

## Beskrivning ##
Besökare på en sida kan föreslå nyckelord (etiketter) för att förbättra sökbarheten. Varje ord som föreslås lagras centralt i metadatatjänsten och godkänns av en administratör. Administratören är administratör i källsystemet där sidan ligger (ex EpiServer). När ordet godkänts kan det läggas till på sidan i källsystemet.

## Del 1: Användaren föreslår etikett ##

### Förutsättning ###
En användare har besökt en sida och vill lägga till en etikett. I det grafiska gränssnittet finns en textruta där användaren  kan skriva in sitt ord, samt en knapp för att skicka ordet.

### Tekniskt flöde ###
  1. Systemet anropar metoden LookupWord() i Metadatatjänsten
    1. Options-objektet som parameter kan innehålla en url till aktuella sidan för att para ihop ordet med en url
  1. Metadatatjänsten svarar vilken ordlista ordet finns i (whitelist,blacklist,none)
    1. Svaret whitelist innebär att ordet redan är godkänt och kan läggas till sidan direkt
    1. Svaret blacklist innebär att ordet är förbjudet och användaren ska få ett felmeddelande som informerar om detta
    1. Svaret none innebär att ordet är nytt och måste granskas. Det har därför placerats i reviewlist i Metadatatjänsten

## Del 2: Administratören granskar föreslagna etiketter ##

### Förutsättning ###
Administratören har loggat in i systemet och navigerat till sidan för administration av etiketter

### Tekniskt flöde ###
  1. Systemet anropar metoden GetVocabulary() med sökvägen till reviewlistan (ex "/VGR/reviewlist"). TODO filtrera på profilId
  1. Metadatatjänsten svarar med en lista av alla ord som är redo för granskning. Systemet presenterar dessa.
  1. Administratören väljer att rättstava ett felstavat ord
    1. Systemet anropar updateVocabularyNode med den nya informationen
  1. Administratören markerar vilka ord som ska godkännas och vilka som förbjuds
  1. För varje ord, kan systemet kontrollera att ordet ligger kvar i reviewlistan. Denna kontroll görs med metoden getVocabularyNodesByName(). Genom att kontrollera vilken förälder den returnerade noden har, kan systemet försäkra sig om att ingen annan hunnit godkänna/förbjuda den.
  1. För varje ord som ska läggas till anropar systemet MoveVocabularyNode med noden och nodens nya förälder (whitelist/blacklist) som parameter.
  1. Metadatatjänsten returnerar en status på om det gick bra eller inte. Systemet visar meddelande beroende på status