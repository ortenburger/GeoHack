---
title: Geoportal API Reference

language_tabs: # must be one of https://git.io/vQNgJ
  - shell

toc_footers:
  - <a href="https://www.transformationsstadt.de">Projektwebsite</a>
  - <a href='https://github.com/lord/slate'>Documentation Powered by Slate</a>


search: true
---

# Übersicht

Das hier soll die API des "Geoportal des Guten Lebens" dokumentieren.

Die Anfragen an und antworten von der API passieren in JSON. Ein Testsystem ist unter [entwicklungssystem.transformationsstadt.de:18080/geoportal/](entwicklungssystem.transformationsstadt.de:18080/geoportal/) zu erreichen.

Es müssen daher die Header `Accept: application/json` und `Content-Type: application/json` gesetzt sein.

<aside class="notice">
Die Deserialisierung referenziert Elemente, die durch die Daten in der Antwort mehrmals vorkommen, lediglich bei weiteren Vorkommnissen.

In einer Liste von Osm-Referenzen beispielsweise, werden die Bli-Dimensionen *jeweils* nur einmal komplett ausgegeben und danach nur noch per ID referenziert.
</aside>

# Authentifizierung (`/accounts/authenticate/`)


Die Authentifizierung gegenüber der API findet momentan per Json Web Tokens (JWT) statt. 

<aside class="notice">
  Die Benutzerauthentifizierung soll demnächst durch oAuth ersetzt werden.
</aside>

Um ein Authentifizierungs-Token zu bekommen, sendet man den Benutzernamen (im Beispiel `user`) und das Password (im Beispiel `pass`) per POST-Request in JSON an `accounts/authenticate/`


Parameter | Typ | Beschreibung
--------- | ------- | -----------
username | string | Der Benutzername oder die Email-Adresse
password | string | Das Passwort

```json
{
  "username": "user",
  "password": "pass"
}
```

> Beipiel

```shell
api=entwicklungssystem.transformationsstadt.de:18080/geoportal
path=accounts/authenticate
username=user
password=password
curl -X POST ${api}/accounts/authenticate/ -H "Accept: application/json" -H "Content-Type: application/json" -d '{"username":"'${username}'","password":"'${password}'"}'


```

> Die API gibt bei erfolgreicher Authentifizierung ein solches JSON zurück (Http: `200`):

```json
{
  "success": "authenticated.",
  "id": "56",
  "Bearer": "eyJhbGciOiJIUzUxMiJ9.eyJzccIiOiJhZG1pbiIsImlzcyI6Imdlb3BvcnRhbDo6aHR0cDovL2VudHdpY2tsdW5nc3N5c3RlbS50cmFuc2Zvcm1hdGlvbnNzdGFkdC5kZToxODA4MC9nZW9wb3J0YWwvYWNjb3VddHMvYXV0aGVudGljYXRlLyIsImlhdCI6MTU2MTcwMjMxNiwiZXhwIjoxNTYxNzg4NzE2LCJyZW1vdGUtYWRkcmVzcyI6IjE3OC4yMDAuNzYuMTA4IiwidXNlci1hZ2VudCI6ImN1cmwvNy42NS4wIn0.LTXrvD8mt8JF4M8cBYEkm4QLY_zyZH67e8DNWoYwIZ-4KiQZdZ5ojEbywqJ-wn5WfIVKx11Vvv3vg1XILJW_OQ",
  "username": "user"
}


```

Der String im Feld `Bearer` ist das JWT-Token, welches per HTTP-Header `Authorization: Bearer xxx` in der folgenden Kommunikation mitgesendet werden muss, um restriktierte Operationen auszuführen.

`Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzccIiOiJhZG1pbiIsImlzcyI6Imdlb3BvcnRhbDo6aHR0cDovL2VudHdpY2tsdW5nc3N5c3RlbS50cmFuc2Zvcm1hdGlvbnNzdGFkdC5kZToxODA4MC9nZW9wb3J0YWwvYWNjb3VddHMvYXV0aGVudGljYXRlLyIsImlhdCI6MTU2MTcwMjMxNiwiZXhwIjoxNTYxNzg4NzE2LCJyZW1vdGUtYWRkcmVzcyI6IjE3OC4yMDAuNzYuMTA4IiwidXNlci1hZ2VudCI6ImN1cmwvNy42NS4wIn0.LTXrvD8mt8JF4M8cBYEkm4QLY_zyZH67e8DNWoYwIZ-4KiQZdZ5ojEbywqJ-wn5WfIVKx11Vvv3vg1XILJW_OQ`

> Bei fehlerhafter Anmeldung kommt ein solches Token zurück (Http: `401`):

```json
{
  "error": "authentication failed."
}
```



# Accounts (`/accounts`)

Informationen zum aktuellen Benutzer fragt man über einen GET-Request `/accounts/currentUser`



```shell
curl -v -X GET ${api}/accounts/currentUser/ -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer ${geoportal_bearer}"
```

> Beispielrückgabe:

```json
{
  "id": 20,
  "tags": [],
  "username": "user",
  "firstname": null,
  "lastname": null,
  "email": "user@example.com",
  "description": null,
  "active": true,
  "validated": true,
  "account_created": 1561575093908,
  "last_login": null,
  "login_count": null,
  "eula_version": null,
  "roles": [
    {
      "id": 1,
      "name": "admin",
      "permissions": []
    }
  ],
  "accountCreated": "26.06.2019 06:51:33",
  "eulaVersion": null,
  "lastLogin": null,
  "loginCount": null
}

```

(Typ `User`)

Parameter | Typ | Beschreibung
--------- | ------- | -----------
id | number |Die Benutzerid
tags | KeyValuePair[] | Liste der mit dem Benutzer assoziierten KeyValue-Paare
username | string | Benutzername
firstname | string | Vorname
lastname | string | Nachname
email | string | Email-Adresse
description | string | Beschreibung
active | boolean | Hält vor, ob der Benutzer-Account aktiv ist. (wird noch nicht benutzt)
validated | boolean | Hält vor, ob der Benutzer-Account validiert ist (etwa per Mail, wird noch nicht benutzt)
account_created | Unix Timestamp | Zeitpunkt der Accounterstellung
last_login | Unix Timestamp | Zeitpunkt des letzten Logins (wird noch nicht benutzt, soll durch ein last_seen ersetzt werden)
login_count | number | 
roles | Role | Liste an Rollen, die der Benutzer hat
eulaVersion | string | Version der Nutzungsbedingungen, der der Benutzer zugestimmt hat. Wird noch nicht Benutzt





# BLI-Dimensionen (`/BliDimensions/all/`)

Unter `/BliDimensions/all/` kann die Liste der aktuellen im System bekannten BLI-Dimensionen per GET-Request abgerufen werden.

```shell
curl -v -X GET ${api}/BliDimensions/all/ -H "Accept: application/json" -H "Content-Type: application/json"
```
>Rückgabe (Typ `BliDimension[]`)

```json
[
  {
    "id": 23,
    "name": "Einkommen",
    "description": "Materielle Ausstattung der Menschen (Einkommen und andere finanzielle Leistungen wie Rente oder Arbeitslosengeld).",
    "slug": null
  },
  {
    "id": 24,
    "name": "Arbeit",
    "description": "Die Verfügbarkeit von guten und sicheren Arbeitsplätzen.",
    "slug": null
  },
  {
    "id": 25,
    "name": "Wohnbedingungen",
    "description": " Wohnqualität in Wuppertal, inklusive Wohnungsgröße und -qualität, Wohnumgebung, Miethöhe und weitere Aspekte wie Leerstand und Aussehen der Straßenzüge.",
    "slug": null
  },
  {
    "id": 26,
    "name": "Gesundheit",
    "description": " Ein langes, gesundes Leben und die Voraussetzungen dazu wie eine gute medizinische Versorgung und gesundheitsfördernde Angebote und Umgebung (Ernährung, Bewegung, Bildungsarbeit).",
    "slug": null
  },
  {
    "id": 27,
    "name": "Work-Life-Balance",
    "description": "",
    "slug": null
  },
  {
    "id": 28,
    "name": "Bildung",
    "description": "Vielfältige hochwertige Bildungsangebote, sowohl schulische Bildung(-sabschlüsse) als auch Weiterbildungsangebote, Workshops und Ausbildungsmöglichkeiten.",
    "slug": null
  },
  {
    "id": 29,
    "name": "Gemeinschaft",
    "description": "Die Einbindung in soziale Beziehungen mit FreundInnen, Familie, NachbarInnen und MitbürgerInnen, sowie auch Unterstützungsnetzwerke, Nachbarschaftshilfe und öffentliche Räume.",
    "slug": null
  },
  {
    "id": 30,
    "name": "Engagement/Beteiligung",
    "description": "Die Möglichkeiten der Menschen ihre Umgebung zu gestalten, ob im Ehrenamt, in Wahlen oder durch Bürgerbeteiligung.",
    "slug": null
  },
  {
    "id": 31,
    "name": "Umwelt",
    "description": "Eine saubere Umwelt mit frischer Luft, sauberen Gewässern und Parks, Umweltschutzprojekte und umweltfreundliche Flächennutzung.",
    "slug": null
  },
  {
    "id": 32,
    "name": "Sicherheit",
    "description": " Statistische und gefühlte Sicherheit vor Verbrechen und Unfällen aber auch Präventionsarbeit.",
    "slug": null
  },
  {
    "id": 33,
    "name": "Zufriedenheit",
    "description": "Die persönliche Zufriedenheit mit dem eigenen Leben und der Umgebung, wie der Nachbarschaft, dem Quartier und der Stadt oder dem Dorf.",
    "slug": null
  },
  {
    "id": 34,
    "name": "Infrastruktur",
    "description": "Städtische Infrastruktur z.B. für Verkehr aber auch lokale Einkaufsmöglichkeiten.",
    "slug": null
  },
  {
    "id": 35,
    "name": "Freizeit und Kultur",
    "description": "Barrierefreier Zugang und gute Angebote zur Freizeitgestaltung wie Kunst und Kultur und auch die Zeit, diese zu nutzen.",
    "slug": null
  }
]

```

(Typ `BliDimension[]`)

Parameter | Typ | Beschreibung
--------- | ------- | -----------
id | number | Id der Kategorie
name | string | Name der Kategorie
description | string | Beschreibung der Kategorie
slug | string | Slug zur Identifizierung im Frontend (wird noch nicht benutzt)




# Kategorien (`/Categories`)

Die Liste der verfügbaren Kategorien lässt sich über einen GET-Request an `/Categories/all` abrufen.

Diese Liste wird ohne die angehangenen Datengruppen oder die darin enthaltenen OSM-Referenzen ausgegeben.

```shell
 curl -v -X GET ${api}/Categories/all/ -H "Accept: application/json" -H "Content-Type: application/json"
```

> Beispielausgabe

```json
[
  {
    "id": 11,
    "name": "Ernährung",
    "displayName": "Dieser Ort und seine Sharing- und Giving-Angebote",
    "dataGroups": [],
    "suggestedKeys": [
      {
        "id": 9,
        "key": "_gpd:sustainable_nutrition",
        "value": "",
        "displayName": "Angebote zum Thema nachhaltige Ernährung",
        "source": null
      },
      {
        "id": 10,
        "key": "_gpd:sustainable_nutrition_assortment",
        "value": "",
        "displayName": "Sortiment",
        "source": null
      }
    ]
  },
  {
    "id": 16,
    "name": "Sharing & Giving",
    "displayName": "Dieser Ort und seine Sharing- & Giving-Angebote",
    "dataGroups": [],
    "suggestedKeys": [
      {
        "id": 12,
        "key": "_gpd:sharing_offers",
        "value": "",
        "displayName": "Angebote des Teilen und Schenkens",
        "source": null
      },
      {
        "id": 13,
        "key": "_gpd:sharing_free_offers",
        "value": "",
        "displayName": "Kostenfreie Angebote",
        "source": null
      },
      {
        "id": 14,
        "key": "_gpd:sharing_organisator",
        "value": "",
        "displayName": "Organisator*in",
        "source": null
      },
      {
        "id": 15,
        "key": "_gpd:sharing_nonfree_offers",
        "value": "",
        "displayName": "Kostenpflichtige Angebote",
        "source": null
      }
    ]
  }
]

```

Die Elemente des Arrays (Category[]) haben folgende Struktur


(Typ `Category`)

Parameter | Typ | Beschreibung
--------- | ------- | -----------
id | number | Id der Kategorie
name | string | Name der Kategorie (zur Anzeige in einer Liste von Kategorien)
displayName | string | Zur Überschrift der unter diese Kategorie angezeigten Werte
suggestedKeys | KeyValuePair[] | Liste von mit der Kategorie verknüpften Key-Value-Paare

## Einzelne Kategorien (`/Categories/<id>/DataGroups/`)

Einer Kategorie hängt gegebenenfalls eine Hierarchie von Datengruppen an. 

Diese können per GET-Request an `/Categories/<id>/DataGroups/` abgefragt werden.


```shell
 curl -v -X GET ${api}/Categories/16/DataGroups/ -H "Accept: application/json" -H "Content-Type: application/json"
```

Die Zurückgegebenen Elemente (Typ: DataGroup[]) haben die folgenden Felder:


(Typ `DataGroup`)

Parameter | Typ | Beschreibung
--------- | ------- | -----------
id | number | Id der Kategorie
name | string | Name der Datengruppe
description | string | Beschreibung der Datengruppe
dataGroups | DataGroup[] | Liste von Unterdatengruppen

# OSM-Referenzen (`/GeoElements/`)

Die OSM-Referenzen im Portal können unter dem Pfad `/GeoElements/` abgerufen und verändert werden.


## Alle Elemente abrufen (`/GeoElements/all`)

Über den Pfad `/GeoElements/all/` können alle im Portal eingetragenen Elemente per GET-Request abgerufen werden.

<aside class="notice">
  Dieser Pfad wird zukünftig wegfallen.
</aside>

```shell
 curl -v -X GET ${api}/GeoElements/all/ -H "Accept: application/json" -H "Content-Type: application/json"
```

> Beispielausgabe

```json
 [
  {
    "id": 193,
    "created": 1561575108101,
    "name": "Ihr Bäcker Schüren",
    "description": "Erstens natürliche Rohstoffe hoher Qualität verwenden, und zweitens mit viel Handarbeit und Know-how daraus Tag für Tag leckere Backwaren herstellen. Durch unsere über 100-jährige Tradition wissen wir, dass diese Top-Qualität auf Dauer nur so zu erhalten ist. Wir sind eine der ersten Bäckereien in Deutschland, die eine Gesamtbetriebs-Zertifizierung nach den Qualitäts-Richtlinien des Vereins die freien Bäcker. Zeit für Verantwortung e.V. besitzen.",
    "createdBy": {
      "id": 17,
      "username": "system"
    },
    "osmId": 1088649042,
    "lastChanged": 1561575415595,
    "changedBy": {
      "id": 17,
      "username": "system"
    },
    "type": "NODE",
    "dataGroups": [],
    "peers": [],
    "bliDimensions": [
      26,
      31
    ],
    "tags": [],
    "lon": 7.0705695152282715,
    "lat": 51.23197937011719
  },
  {
    "id": 194,
    "created": 1561575108108,
    "name": "Hutzel Vollkorn Bäckerei",
    "description": "Bereits seit 1981 backen wir Gesundes und Leckeres aus dem vollen Korn. Der Ursprung des Unternehmens war eine Beschäftigungsinitiative namens \"Meiob\", bevor am 4.März 1983 die Hutzel Vollkorn-Bäckerei GmbH gegründet wurde. Wir sind also jetzt seit über 30 Jahren aktiv! Unser Betrieb besteht von Beginn an aus der Backstube in Bochum-Weitmar und dem dort angeschlossenen Bäckerei- und Naturkostfachgeschäft. Weitere eigene Hutzel-Verkaufsstellen sind die Back-Shop-Filiale im denns' Biomarkt (Hattingerstr. 264), der Naturkostladen in Wuppertal-Wichlinghausen (Am Diek 9) und die Märkte in Bochum und Wuppertal. Wir beliefern etwa 40 Wiederverkäufer (Naturkostläden, Bio-Supermärkte und Reformhäuser) im mittleren Ruhrgebiet und im Raum Wuppertal, von denen sich bestimmt auch einer in Ihrer Nähe befindet.",
    "createdBy": {
      "id": 17,
      "username": "system"
    },
    "osmId": 5328475829,
    "lastChanged": 1561575416295,
    "changedBy": {
      "id": 17,
      "username": "system"
    },
    "type": "NODE",
    "dataGroups": [],
    "peers": [],
    "bliDimensions": [
      26,
      31
    ],
    "tags": [],
    "lon": 7.2198920249938965,
    "lat": 51.28506851196289
  }
]

```

Die Zurückgegebene Liste (OsmReference[]) enthält Elemente des folgenden Formates:

(Typ: OsmReference)

Parameter | Typ | Beschreibung
--------- | ------- | -----------
id | number | ID des Elements
created | Unix Timestamp | Zeitpunkt der Erstellung
name | string | Name des Elementes
description | string | Beschreibung des Elementes
createdBy | User (kurzform) | Benutzer, der das Element angelegt hat
lastChanged | Unix Timestamp | Zeitpunkt der letzten Änderung
changedBy | User (kurzform) | Der Benutzer, der die letzte Änderung gespeichert hat
osmId | number | ID in OSM
type | string | Typ der OSM (osmId und type werden zur Referenzierung bei OSM benutzt)
dataGroups | DataGroup[] | Liste der Datengruppen
peers | OsmReference[] | Liste der mit diesem Element verbundenen Netzwerkpartner (nicht-rekursiv)
bliDimensions | BliDimension[] | Liste der Bli-Dimensionen, die diesem Element zugeordnet sind
tags | KeyValuePair[] | Liste an Tags, die diesem Element zugeordnet sind
lon | number (double) | Längengrad der Referenz
lat | number (double) | Breitengrad der Referenz


## Einzelne Osm-Referenzen abrufen und verändern (`/GeoElements/<id>`)

Unter `/GeoElements/<id>` können einzelne Elemente per PATCH- bzw. GET-Request geändert bzw. abgerufen werden


```shell
 curl -v -X GET ${api}/GeoElements/42/ -H "Accept: application/json" -H "Content-Type: application/json"
```

> Beispielausgabe 

```json
{
  "id": 42,
  "created": 1561575107802,
  "name": "Utopiastadt",
  "description": "Überregionaler Denkraum und Projektpartner rund um zukunftsfähige und integrierte Stadtentwicklung, Forum Mirke (Stadtteilkonferenz/ Vernetzung im Quartier), Veranstaltungsort/ Raum für alternative Kultur (Konzerte, Lesungen, Premieren), Werkstätten (Recycling und Upcycling, Fahrradwerkstätten), Raum für Kreativ und Gesellschaftspolitische Arbeit (Atelierräume, Büroräume für Initiativen, Projekte und kulturelle, gesellschaftspolitische Organisationen/ Initiativen zb. Bergische Gartenarche, OpenDaTal/dev/tal, Hackerspace, Utopiastadtgarten)",
  "createdBy": {
    "id": 17,
    "username": "system"
  },
  "osmId": 4543376881,
  "lastChanged": 1561623871985,
  "changedBy": {
    "id": 20,
    "username": "admin"
  },
  "type": "NODE",
  "dataGroups": [],
  "peers": [
    {
      "id": 40,
      "created": 1561575100978,
      "name": "Aufbruch am Arrenberg e.V.",
      "description": "Stadtteilarbeit. Co2 neutrales \"Klimaquartier Arrenberg\" (geteilt in Ernährung, Energie, Mobilität), Teilaspekt Ernährung (Essbarer Arrenberg): Anregung Konsum nachhaltiger regionaler Produkte (Restaurant Day, Urban Farming, Märkte, Foodsharing Anlaufpunkte/ Plattformen, Teilaspekt Energie (Energiereicher Arrenberg): Nachhaltige Energieversorgung fördern/ aufbauen (Fern- und Erdwärme, Wind- und Wasserkraft, Wärmepumpen, Einergieeinspaarung) Teilaspekt Mobilität (Mobiler Arrenberg) Nachhaltige Mobilität fördern (E Bikes, Ladeinfrastruktur, Fahrräder, Carsharingsysteme, Elektromobilität)",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 4618692813,
      "lastChanged": 1561575302412,
      "changedBy": {
        "id": 17,
        "username": "system"
      },
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [
        {
          "id": 32,
          "name": "Sicherheit",
          "description": " Statistische und gefühlte Sicherheit vor Verbrechen und Unfällen aber auch Präventionsarbeit.",
          "slug": null
        },
        {
          "id": 33,
          "name": "Zufriedenheit",
          "description": "Die persönliche Zufriedenheit mit dem eigenen Leben und der Umgebung, wie der Nachbarschaft, dem Quartier und der Stadt oder dem Dorf.",
          "slug": null
        },
        {
          "id": 34,
          "name": "Infrastruktur",
          "description": "Städtische Infrastruktur z.B. für Verkehr aber auch lokale Einkaufsmöglichkeiten.",
          "slug": null
        },
        {
          "id": 25,
          "name": "Wohnbedingungen",
          "description": " Wohnqualität in Wuppertal, inklusive Wohnungsgröße und -qualität, Wohnumgebung, Miethöhe und weitere Aspekte wie Leerstand und Aussehen der Straßenzüge.",
          "slug": null
        },
        {
          "id": 26,
          "name": "Gesundheit",
          "description": " Ein langes, gesundes Leben und die Voraussetzungen dazu wie eine gute medizinische Versorgung und gesundheitsfördernde Angebote und Umgebung (Ernährung, Bewegung, Bildungsarbeit).",
          "slug": null
        },
        {
          "id": 28,
          "name": "Bildung",
          "description": "Vielfältige hochwertige Bildungsangebote, sowohl schulische Bildung(-sabschlüsse) als auch Weiterbildungsangebote, Workshops und Ausbildungsmöglichkeiten.",
          "slug": null
        },
        {
          "id": 29,
          "name": "Gemeinschaft",
          "description": "Die Einbindung in soziale Beziehungen mit FreundInnen, Familie, NachbarInnen und MitbürgerInnen, sowie auch Unterstützungsnetzwerke, Nachbarschaftshilfe und öffentliche Räume.",
          "slug": null
        },
        {
          "id": 30,
          "name": "Engagement/Beteiligung",
          "description": "Die Möglichkeiten der Menschen ihre Umgebung zu gestalten, ob im Ehrenamt, in Wahlen oder durch Bürgerbeteiligung.",
          "slug": null
        },
        {
          "id": 31,
          "name": "Umwelt",
          "description": "Eine saubere Umwelt mit frischer Luft, sauberen Gewässern und Parks, Umweltschutzprojekte und umweltfreundliche Flächennutzung.",
          "slug": null
        }
      ],
      "tags": [
        {
          "id": 38,
          "key": "_gpd:aktionsradius",
          "value": "im Ortsteil",
          "displayName": null,
          "source": "import"
        },
        {
          "id": 39,
          "key": "_gpd:erhebungsmethode",
          "value": "Telefoninterview im WTW-Projekt des TransZent",
          "displayName": null,
          "source": "import"
        }
      ],
      "lon": 7.127844333648682,
      "lat": 51.247615814208984
    },
    {
      "id": 43,
      "created": 1561575107561,
      "name": "Bergische Universität Wuppertal",
      "description": "",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 24476537,
      "lastChanged": 1561621016848,
      "changedBy": {
        "id": 21,
        "username": "sam"
      },
      "type": "WAY",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.149456024169922,
      "lat": 51.244873046875
    },
    {
      "id": 1904,
      "created": 1561578470702,
      "name": "Bob Kulturwerk, 38-40, Wichlinghauser Straße, Bergisches Plateau, Gemarkung Barmen, Wuppertal, Regierungsbezirk Düsseldorf, Nordrhein-Westfalen, 42277, Deutschland",
      "description": "",
      "createdBy": {
        "id": 20,
        "username": "admin"
      },
      "osmId": 6015789123,
      "lastChanged": 1561579084911,
      "changedBy": {
        "id": 20,
        "username": "admin"
      },
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.2185868,
      "lat": 51.2782124
    },
    {
      "id": 49,
      "created": 1561575107857,
      "name": "Stadt Wuppertal",
      "description": null,
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 252025084,
      "lastChanged": 1561575387452,
      "changedBy": null,
      "type": "WAY",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.200001239776611,
      "lat": 51.27216339111328
    },
    {
      "id": 55,
      "created": 1561575101126,
      "name": "Wirtschaftsförderung Wuppertal",
      "description": null,
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 3131638086,
      "lastChanged": 1561575310404,
      "changedBy": null,
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.159621715545654,
      "lat": 51.2367057800293
    },
    {
      "id": 56,
      "created": 1561575106486,
      "name": "Alte Feuerwache Wuppertal",
      "description": "Internationales Jugend- und Begegnungszentrum (offene Kinder- und Jugendeinrichtung, pädagogische und interkulturelle Angebote, Integrationsarbeit Integration der unterschiedlichen Menschen aus verschiedenen Ländern, Flüchtlingshilfe, Bildungsarbeit z.B. mit dem Projekt „Quadratkilometer Bildung“ zur Verbesserung der Bildungschancen von Kindern und Jugendlichen), Zusammenkunfts- und Austauschort für Menschen um Ideen für ein gelingendes Zusammenleben zu entwickeln und auf Problemlagen hinzuweisen, verschiedene Arbeit für Kinder und Jugendliche (Offener Kinder und Jugendbereich, Intensivbetreuung, Kulturwerkstatt für Kinder, Therapie und Beratung, Frühförderung, Mittagstisch, Arbeit für Eltern und Erwachsene (Begleitung und Hilfe für Eltern und junge Familien, Familienhebammen, Sprachfördergruppe, verschiedene Gruppen und Vereine die sich regelmäßig in der alten Feuerwache treffen z.B. das „Cafe Kinderwagen“ als Austauschort für junge Mütter und Väter) Quartiers- und Stadtteilarbeit (Kampagnen wie „Armer Anfang ist schwer“ „M.ein Quartier“ und „Quadratkilometer Bildung“), Kulturarbeit (Kulturwerkstatt mit vielfältigen Kulturangeboten für Kinder und Jugendliche, „Talflimmern“ Filmvorführungen im Hof der alten Feuerwache in den Sommermonaten)",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 153796010,
      "lastChanged": 1561575315315,
      "changedBy": {
        "id": 17,
        "username": "system"
      },
      "type": "WAY",
      "dataGroups": [],
      "bliDimensions": [
        32,
        33,
        34,
        {
          "id": 35,
          "name": "Freizeit und Kultur",
          "description": "Barrierefreier Zugang und gute Angebote zur Freizeitgestaltung wie Kunst und Kultur und auch die Zeit, diese zu nutzen.",
          "slug": null
        },
        28,
        29,
        30,
        31
      ],
      "tags": [
        {
          "id": 64,
          "key": "_gpd:aktionsradius",
          "value": "im Ortsteil",
          "displayName": null,
          "source": "import"
        }
      ],
      "lon": 7.146681785583496,
      "lat": 51.26411437988281
    },
    {
      "id": 120,
      "created": 1561575107738,
      "name": "Freifunk Wuppertal",
      "description": "Der Verein Freifunk Rheinland e.V. wurde von Aktivisten aus der Region Düsseldorf und Wuppertal gegründet um nicht nur individuell sondern als Körperschaft handeln zu können. Dabei ist es das Ziel der Funkzelle Wuppertal die technische Umsetzung eines freien, autarken und bürgernahen Netzwerkes zu ermöglichen. Über die Betreibung der Internetseite wird eine Firmware bereitgestellt, mit der Internetrouter modifiziert werden und somit einen Teil des Internet-Zugangs 'spenden'. Wenn mehrere Router sich in einem geringen räumlichen Abstand zueinander befinden, wird das Netzwerk robuster und die Verbindung deutlich schneller. Das Ziel ist somit, eine Eigendynamik zu schaffen, damit mehr und mehr private Haushalte sich anschließen. Zudem hat die Funkzelle angefangen, städtische Gebäude mit entsprechend modifizierten Routern auszustatten, welche eine größere und weitere Vernetzung mit sich bringen (z.B. Rathausturm Elberfeld).",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 6049466058,
      "lastChanged": 1561575363535,
      "changedBy": {
        "id": 17,
        "username": "system"
      },
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [
        33,
        34,
        25,
        30
      ],
      "tags": [
        {
          "id": 158,
          "key": "_gpd:aktionsradius",
          "value": "im gesamten Ort",
          "displayName": null,
          "source": "import"
        }
      ],
      "lon": 7.144989967346191,
      "lat": 51.26673889160156
    },
    {
      "id": 57,
      "created": 1561575101144,
      "name": "Bergische Struktur- und Wirtschaftsförderungsgesellschaft mbH",
      "description": null,
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 6050601407,
      "lastChanged": 1561575313319,
      "changedBy": null,
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.08034610748291,
      "lat": 51.165138244628906
    },
    {
      "id": 58,
      "created": 1561575101200,
      "name": "Wohnungsgenossenschaft Ölberg eG",
      "description": null,
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 6050613758,
      "lastChanged": 1561575314463,
      "changedBy": null,
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.138179302215576,
      "lat": 51.25881576538086
    },
    {
      "id": 59,
      "created": 1561575101205,
      "name": "TransZent",
      "description": null,
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 6050610430,
      "lastChanged": 1561575317366,
      "changedBy": null,
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.1526570320129395,
      "lat": 51.2552604675293
    },
    {
      "id": 60,
      "created": 1561575101212,
      "name": "Humboldt Universität Berlin",
      "description": "",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 120456814,
      "lastChanged": 1561577688938,
      "changedBy": {
        "id": 20,
        "username": "admin"
      },
      "type": "WAY",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 13.392064094543457,
      "lat": 52.51950454711914
    },
    {
      "id": 61,
      "created": 1561575104860,
      "name": "Hebebühne e.V.",
      "description": "Der Kulturverein Hebebühne e.V. versteht sich als eine Plattform für die Umsetzung und Ausstellung von künstlerischen Ideen der Bürger Wuppertals sowie der Kunstwerke junger Künstler. Letztgenanntere werden konzeptionell unterstützt (Öffentlichkeitsarbeit, Planung, Durchführung, Vermittlung von finanzieller Unterstützung) und somit bei ersten Schritten in den Kunstmarkt begleitet. Dabei ist es dem Verein ausdrücklich wichtig, nicht auf regionale oder lokale Künstler fokussiert zu sein, sondern zieht auch viele Kunstschaffende aus der weiteren Region (Düsseldorf, Köln, Essen, Bonn) an. Neben den Kunstausstellungen werden zudem verschiedene Veranstaltungsformate realisiert, welche jedoch auch stark mit den sich engagierenden Mitgliedern und deren Ideen zusammenhängen. Dabei ist es in letzter Zeit zu mehreren Konzerten und Lesungen gekommen. Die Hebebühne beteiligt sich zudem regelmäßig und produktiv an der Wuppertaler Performancenacht.",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 169012125,
      "lastChanged": 1561575319523,
      "changedBy": {
        "id": 17,
        "username": "system"
      },
      "type": "WAY",
      "dataGroups": [],
      "bliDimensions": [
        33,
        35,
        29
      ],
      "tags": [
        {
          "id": 130,
          "key": "_gpd:aktionsradius",
          "value": "im gesamten Ort",
          "displayName": null,
          "source": "import"
        }
      ],
      "lon": 7.143848419189453,
      "lat": 51.26646423339844
    },
    {
      "id": 62,
      "created": 1561575106052,
      "name": "Wohngruppe Malerstraße/KomMal e.V.",
      "description": "Der Verein ist mit der Verwaltung des Mehrgenerationenhauses in der Malerstraße betraut. Die Inbetriebnahme, der Ausbau, sowie die Konzipierung des Projektes fand noch außerhalt des Vereinsrahmens statt. Das Haus soll ein generationsübergreifendes und barrierefreies Wohnen in einem energiesparendem Passivhaus ermöglichen. Der Verein KomMal dient nun als Kommunikationstreff, um das Projekt zum Einen der Öffentlichkeit zugänglich zu machen, sich andererseits an Initiativen und Stadtteilarbeiten organisiert beteiligen zu können. Bei Projekten des Vereins steht der Aspekt der Förderung von Kunst und Kultur im Vordergrund. Ausstellungen werden in den Gemeinschaftsräumen ausgerichtet, zudem finden in unregelmäßigen Abständen Lesungen statt. Als neue Ausrichtung wird die Vernetzung des Vereins sowie dessen Einbringung in die Quartiersarbeit angestrengt. Der Verein möchte sich zudem zunehmend in die Flüchtlingshilfe miteinklinken.",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 4644158658,
      "lastChanged": 1561575320340,
      "changedBy": {
        "id": 17,
        "username": "system"
      },
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [
        33,
        35,
        25,
        29,
        30,
        31
      ],
      "tags": [
        {
          "id": 143,
          "key": "_gpd:aktionsradius",
          "value": "im Ortsteil",
          "displayName": null,
          "source": "import"
        }
      ],
      "lon": 7.134419918060303,
      "lat": 51.264671325683594
    },
    {
      "id": 63,
      "created": 1561575101232,
      "name": "die Urbanisten e.V. Dortmund",
      "description": null,
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 6050640058,
      "lastChanged": 1561575321300,
      "changedBy": null,
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [],
      "tags": [],
      "lon": 7.438089847564697,
      "lat": 51.5126838684082
    },
    {
      "id": 159,
      "created": 1561575107742,
      "name": "/dev/tal e.V.",
      "description": "",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 1497543573,
      "lastChanged": 1561624331638,
      "changedBy": {
        "id": 20,
        "username": "admin"
      },
      "type": "NODE",
      "dataGroups": [],
      "bliDimensions": [
        32,
        33,
        34,
        35,
        {
          "id": 23,
          "name": "Einkommen",
          "description": "Materielle Ausstattung der Menschen (Einkommen und andere finanzielle Leistungen wie Rente oder Arbeitslosengeld).",
          "slug": null
        },
        {
          "id": 24,
          "name": "Arbeit",
          "description": "Die Verfügbarkeit von guten und sicheren Arbeitsplätzen.",
          "slug": null
        },
        25,
        26,
        {
          "id": 27,
          "name": "Work-Life-Balance",
          "description": "",
          "slug": null
        },
        28,
        29,
        30,
        31
      ],
      "tags": [
        {
          "id": 4552,
          "key": "_gpd:aktionsradius",
          "value": "Überregional",
          "displayName": null,
          "source": null
        }
      ],
      "lon": 7.145033836364746,
      "lat": 51.266719818115234
    }
  ],
  "bliDimensions": [
    33,
    34,
    35,
    24,
    29,
    30
  ],
  "tags": [
    {
      "id": 1009,
      "key": "_gpd:sharing_offers",
      "value": "Foodsharing Kühlschrank",
      "displayName": null,
      "source": null
    },
    {
      "id": 1010,
      "key": "_gpd:sustainable_nutrition",
      "value": "Foodsharing Kühlschrank",
      "displayName": null,
      "source": null
    },
    {
      "id": 54,
      "key": "_gpd:aktionsradius",
      "value": "überregional",
      "displayName": null,
      "source": null
    }
  ],
  "lon": 7.145003318786621,
  "lat": 51.266807556152344
}

```


Die Rückgabe ist vom Typ `OsmReference` (siehe oben).

IDs, die initialen Benutzer und der Zeitpunkt der Erstellung können nicht verändert werden.
Der Benutzer und der Aktualierungszeitpunkt werden Serverseitig ergänzt.

Das Zurückgegebene Element vom Typ OsmReference ist die aktualisierte Version des Elementes.

> Ändern eines Eintrages
```shell
curl -v -X GET ${api}/GeoElements/42/ -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer ${geoportal_bearer}" -d @utopiastadt.json
```
> Hierbei liegt in der Datei `utopiastadt.json` der veränderte Datensatz.

## Elemente anlegen

Per POST-Request nach `/GeoElements/` können Elemente angelegt werden. Dabei muss keine ID übergeben werden. Erstellender Benutzer und Erstellzeitpunkt werden vom Backend ergänzt. 

<aside class="success">
  Bevor Elemente angelegt werden, kann über die OSM-Daten (Typ, Id) geprüft werden, ob die Elemente schon eine Referenzierung im Geoportal haben
</aside>

## Abfrage per OSM-Daten

Unter `GeoElements/byOsmId/nodes/<id>/` bzw. `GeoElements/byOsmId/ways/<id>/` können über Ihre Referenz auf die OSM-Datenbank abgefragt werden um zu prüfen, ob ein Element bereits in der Datenbank referenziert ist.

```shell
    curl -v -X GET ${api}/GeoElements/byOsmId/nodes/4543376881 -H "Accept: application/json" -H "Content-Type: application/json"
```

Sollte das Element in der Datenbank referenziert sein, wird der entsprechende Eintrag zurückgegeben (Http 200), ansonsten wird mit Http 400 quittiert.

## Abfrage per Bounding-Box

Unter dem Pfad `/GeoElements/byBoundingBox` lassen sich die Elemente in einem gewissen Bereich (Längen- und Breitengrad) per GET-Request abfragen.

Das Rechteck definiert man dabei über die GET-Parameter `minX`, `minY`, `maxX` und `maxY` vom Typ float in.

```shell
    curl -v -X GET ${api}'/GeoElements/byBoundingBox/?minX=6.85534732055664&minY=51.157188370168086&maxX=7.43865267944336&maxY=51.36258219686752' -H "Accept: application/json" -H "Content-Type: application/json"
```




## Abfrage der Netzwerkstruktur

Die Netzwerkstuktur lässt sich als CSV-Datei von der API abfragen. Dabei wird eine Liste zurückgegeben, in der pro Zeile jeweils eine Verbindung zwischen zwei Netzwerkpartnern liegt. Die Netzwerkverbindungen liegen dabei an beiden Netzwerkpartnern, daher ist jeder Eintrag doppelt vorhanden. 
Es ist angedacht in Zukunft auch unidirektionale Netzwerkverbindungen zu ermöglichen.

Die Felder sind per Semikolon getrennt, die Zeilen per "\n".
```shell
  wget ${api}/GeoElements/EdgeList/
```
> Beispielausgabe (Datei per Attachment / Download)
```
Bürgerverein Kothener Freunde e.V.;Stadt Wuppertal
Bürgerverein Kothener Freunde e.V.;Unterbarmer Bürgerverein e.V.
Bürgerverein Kothener Freunde e.V.;Rotter Bürgerverein e.V.
Initiative für krebskranke Kinder e.V. Wuppertal;Bürgerforum Oberbarmen
Initiative für krebskranke Kinder e.V. Wuppertal;Wir in Wichlinghausen e.V.
Initiative für krebskranke Kinder e.V. Wuppertal;Bezirksverein Heckinghausen
Initiative für krebskranke Kinder e.V. Wuppertal;Heidter Bürgerverein e.V.
Aufbruch am Arrenberg e.V.;Wuppertaler Stadtwerke
Aufbruch am Arrenberg e.V.;Utopiastadt
Aufbruch am Arrenberg e.V.;Bergische Universität Wuppertal
Wir in Wichlinghausen e.V.;Bürgerforum Oberbarmen
[...]
```


# Suchen

Die API lässt sich über GET-Anfragen auf `/search/` durchsuchen. Momentan ist dies ein einfaches, case-insensitive string-matching auf den Feldern "name" und "description" der OsmReferenzen und Datengruppen.

Der Suchbegriff wird dabei über den Parameter `q` übergeben.



```shell
   curl -v -X GET ${api}'/search/?q=Utopiastadt' -H "Accept: application/json" -H "Content-Type: application/json"
```

Eingabe

Parameter | Typ | Beschreibung
--------- | ------- | -----------
q|string|Suchbegriff

Ausgabe: 

Parameter | Typ | Beschreibung
--------- | ------- | -----------
dataGroups | DataGroup[] | Liste der Datengruppen, die in Beschreibung und/oder Namen den Suchbegriff enthalten
geoElements | OsmReference[] | Liste der GeoElemente, die in Beschreibung und/oder Namen den Suchbegriff enthalten


>Beispielausgabe

```
{
  "dataGroups": [],
  "geoElements": [
    {
      "id": 42,
      "created": 1561575107802,
      "name": "Utopiastadt",
      "description": "Überregionaler Denkraum und Projektpartner rund um zukunftsfähige und integrierte Stadtentwicklung, Forum Mirke (Stadtteilkonferenz/ Vernetzung im Quartier), Veranstaltungsort/ Raum für alternative Kultur (Konzerte, Lesungen, Premieren), Werkstätten (Recycling und Upcycling, Fahrradwerkstätten), Raum für Kreativ und Gesellschaftspolitische Arbeit (Atelierräume, Büroräume für Initiativen, Projekte und kulturelle, gesellschaftspolitische Organisationen/ Initiativen zb. Bergische Gartenarche, OpenDaTal/dev/tal, Hackerspace, Utopiastadtgarten)",
      "createdBy": {
        "id": 17,
        "username": "system"
      },
      "osmId": 4543376881,
      "lastChanged": 1561623871985,
      "changedBy": {
        "id": 20,
        "username": "admin"
      },
      "type": "NODE",
      "dataGroups": [],
      "peers": [
        {
          "id": 40,
          "created": 1561575100978,
          "name": "Aufbruch am Arrenberg e.V.",
          "description": "Stadtteilarbeit. Co2 neutrales \"Klimaquartier Arrenberg\" (geteilt in Ernährung, Energie, Mobilität), Teilaspekt Ernährung (Essbarer Arrenberg): Anregung Konsum nachhaltiger regionaler Produkte (Restaurant Day, Urban Farming, Märkte, Foodsharing Anlaufpunkte/ Plattformen, Teilaspekt Energie (Energiereicher Arrenberg): Nachhaltige Energieversorgung fördern/ aufbauen (Fern- und Erdwärme, Wind- und Wasserkraft, Wärmepumpen, Einergieeinspaarung) Teilaspekt Mobilität (Mobiler Arrenberg) Nachhaltige Mobilität fördern (E Bikes, Ladeinfrastruktur, Fahrräder, Carsharingsysteme, Elektromobilität)",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 4618692813,
          "lastChanged": 1561575302412,
          "changedBy": {
            "id": 17,
            "username": "system"
          },
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [
            {
              "id": 32,
              "name": "Sicherheit",
              "description": " Statistische und gefühlte Sicherheit vor Verbrechen und Unfällen aber auch Präventionsarbeit.",
              "slug": null
            },
            {
              "id": 33,
              "name": "Zufriedenheit",
              "description": "Die persönliche Zufriedenheit mit dem eigenen Leben und der Umgebung, wie der Nachbarschaft, dem Quartier und der Stadt oder dem Dorf.",
              "slug": null
            },
            {
              "id": 34,
              "name": "Infrastruktur",
              "description": "Städtische Infrastruktur z.B. für Verkehr aber auch lokale Einkaufsmöglichkeiten.",
              "slug": null
            },
            {
              "id": 25,
              "name": "Wohnbedingungen",
              "description": " Wohnqualität in Wuppertal, inklusive Wohnungsgröße und -qualität, Wohnumgebung, Miethöhe und weitere Aspekte wie Leerstand und Aussehen der Straßenzüge.",
              "slug": null
            },
            {
              "id": 26,
              "name": "Gesundheit",
              "description": " Ein langes, gesundes Leben und die Voraussetzungen dazu wie eine gute medizinische Versorgung und gesundheitsfördernde Angebote und Umgebung (Ernährung, Bewegung, Bildungsarbeit).",
              "slug": null
            },
            {
              "id": 28,
              "name": "Bildung",
              "description": "Vielfältige hochwertige Bildungsangebote, sowohl schulische Bildung(-sabschlüsse) als auch Weiterbildungsangebote, Workshops und Ausbildungsmöglichkeiten.",
              "slug": null
            },
            {
              "id": 29,
              "name": "Gemeinschaft",
              "description": "Die Einbindung in soziale Beziehungen mit FreundInnen, Familie, NachbarInnen und MitbürgerInnen, sowie auch Unterstützungsnetzwerke, Nachbarschaftshilfe und öffentliche Räume.",
              "slug": null
            },
            {
              "id": 30,
              "name": "Engagement/Beteiligung",
              "description": "Die Möglichkeiten der Menschen ihre Umgebung zu gestalten, ob im Ehrenamt, in Wahlen oder durch Bürgerbeteiligung.",
              "slug": null
            },
            {
              "id": 31,
              "name": "Umwelt",
              "description": "Eine saubere Umwelt mit frischer Luft, sauberen Gewässern und Parks, Umweltschutzprojekte und umweltfreundliche Flächennutzung.",
              "slug": null
            }
          ],
          "tags": [
            {
              "id": 38,
              "key": "_gpd:aktionsradius",
              "value": "im Ortsteil",
              "displayName": null,
              "source": "import"
            },
            {
              "id": 39,
              "key": "_gpd:erhebungsmethode",
              "value": "Telefoninterview im WTW-Projekt des TransZent",
              "displayName": null,
              "source": "import"
            }
          ],
          "lon": 7.127844333648682,
          "lat": 51.247615814208984
        },
        {
          "id": 43,
          "created": 1561575107561,
          "name": "Bergische Universität Wuppertal",
          "description": "",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 24476537,
          "lastChanged": 1561621016848,
          "changedBy": {
            "id": 21,
            "username": "sam"
          },
          "type": "WAY",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.149456024169922,
          "lat": 51.244873046875
        },
        {
          "id": 1904,
          "created": 1561578470702,
          "name": "Bob Kulturwerk, 38-40, Wichlinghauser Straße, Bergisches Plateau, Gemarkung Barmen, Wuppertal, Regierungsbezirk Düsseldorf, Nordrhein-Westfalen, 42277, Deutschland",
          "description": "",
          "createdBy": {
            "id": 20,
            "username": "admin"
          },
          "osmId": 6015789123,
          "lastChanged": 1561579084911,
          "changedBy": {
            "id": 20,
            "username": "admin"
          },
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.2185868,
          "lat": 51.2782124
        },
        {
          "id": 49,
          "created": 1561575107857,
          "name": "Stadt Wuppertal",
          "description": null,
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 252025084,
          "lastChanged": 1561575387452,
          "changedBy": null,
          "type": "WAY",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.200001239776611,
          "lat": 51.27216339111328
        },
        {
          "id": 55,
          "created": 1561575101126,
          "name": "Wirtschaftsförderung Wuppertal",
          "description": null,
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 3131638086,
          "lastChanged": 1561575310404,
          "changedBy": null,
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.159621715545654,
          "lat": 51.2367057800293
        },
        {
          "id": 56,
          "created": 1561575106486,
          "name": "Alte Feuerwache Wuppertal",
          "description": "Internationales Jugend- und Begegnungszentrum (offene Kinder- und Jugendeinrichtung, pädagogische und interkulturelle Angebote, Integrationsarbeit Integration der unterschiedlichen Menschen aus verschiedenen Ländern, Flüchtlingshilfe, Bildungsarbeit z.B. mit dem Projekt „Quadratkilometer Bildung“ zur Verbesserung der Bildungschancen von Kindern und Jugendlichen), Zusammenkunfts- und Austauschort für Menschen um Ideen für ein gelingendes Zusammenleben zu entwickeln und auf Problemlagen hinzuweisen, verschiedene Arbeit für Kinder und Jugendliche (Offener Kinder und Jugendbereich, Intensivbetreuung, Kulturwerkstatt für Kinder, Therapie und Beratung, Frühförderung, Mittagstisch, Arbeit für Eltern und Erwachsene (Begleitung und Hilfe für Eltern und junge Familien, Familienhebammen, Sprachfördergruppe, verschiedene Gruppen und Vereine die sich regelmäßig in der alten Feuerwache treffen z.B. das „Cafe Kinderwagen“ als Austauschort für junge Mütter und Väter) Quartiers- und Stadtteilarbeit (Kampagnen wie „Armer Anfang ist schwer“ „M.ein Quartier“ und „Quadratkilometer Bildung“), Kulturarbeit (Kulturwerkstatt mit vielfältigen Kulturangeboten für Kinder und Jugendliche, „Talflimmern“ Filmvorführungen im Hof der alten Feuerwache in den Sommermonaten)",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 153796010,
          "lastChanged": 1561575315315,
          "changedBy": {
            "id": 17,
            "username": "system"
          },
          "type": "WAY",
          "dataGroups": [],
          "bliDimensions": [
            32,
            33,
            34,
            {
              "id": 35,
              "name": "Freizeit und Kultur",
              "description": "Barrierefreier Zugang und gute Angebote zur Freizeitgestaltung wie Kunst und Kultur und auch die Zeit, diese zu nutzen.",
              "slug": null
            },
            28,
            29,
            30,
            31
          ],
          "tags": [
            {
              "id": 64,
              "key": "_gpd:aktionsradius",
              "value": "im Ortsteil",
              "displayName": null,
              "source": "import"
            }
          ],
          "lon": 7.146681785583496,
          "lat": 51.26411437988281
        },
        {
          "id": 120,
          "created": 1561575107738,
          "name": "Freifunk Wuppertal",
          "description": "Der Verein Freifunk Rheinland e.V. wurde von Aktivisten aus der Region Düsseldorf und Wuppertal gegründet um nicht nur individuell sondern als Körperschaft handeln zu können. Dabei ist es das Ziel der Funkzelle Wuppertal die technische Umsetzung eines freien, autarken und bürgernahen Netzwerkes zu ermöglichen. Über die Betreibung der Internetseite wird eine Firmware bereitgestellt, mit der Internetrouter modifiziert werden und somit einen Teil des Internet-Zugangs 'spenden'. Wenn mehrere Router sich in einem geringen räumlichen Abstand zueinander befinden, wird das Netzwerk robuster und die Verbindung deutlich schneller. Das Ziel ist somit, eine Eigendynamik zu schaffen, damit mehr und mehr private Haushalte sich anschließen. Zudem hat die Funkzelle angefangen, städtische Gebäude mit entsprechend modifizierten Routern auszustatten, welche eine größere und weitere Vernetzung mit sich bringen (z.B. Rathausturm Elberfeld).",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 6049466058,
          "lastChanged": 1561575363535,
          "changedBy": {
            "id": 17,
            "username": "system"
          },
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [
            33,
            34,
            25,
            30
          ],
          "tags": [
            {
              "id": 158,
              "key": "_gpd:aktionsradius",
              "value": "im gesamten Ort",
              "displayName": null,
              "source": "import"
            }
          ],
          "lon": 7.144989967346191,
          "lat": 51.26673889160156
        },
        {
          "id": 57,
          "created": 1561575101144,
          "name": "Bergische Struktur- und Wirtschaftsförderungsgesellschaft mbH",
          "description": null,
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 6050601407,
          "lastChanged": 1561575313319,
          "changedBy": null,
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.08034610748291,
          "lat": 51.165138244628906
        },
        {
          "id": 58,
          "created": 1561575101200,
          "name": "Wohnungsgenossenschaft Ölberg eG",
          "description": null,
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 6050613758,
          "lastChanged": 1561575314463,
          "changedBy": null,
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.138179302215576,
          "lat": 51.25881576538086
        },
        {
          "id": 59,
          "created": 1561575101205,
          "name": "TransZent",
          "description": null,
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 6050610430,
          "lastChanged": 1561575317366,
          "changedBy": null,
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.1526570320129395,
          "lat": 51.2552604675293
        },
        {
          "id": 60,
          "created": 1561575101212,
          "name": "Humboldt Universität Berlin",
          "description": "",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 120456814,
          "lastChanged": 1561577688938,
          "changedBy": {
            "id": 20,
            "username": "admin"
          },
          "type": "WAY",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 13.392064094543457,
          "lat": 52.51950454711914
        },
        {
          "id": 61,
          "created": 1561575104860,
          "name": "Hebebühne e.V.",
          "description": "Der Kulturverein Hebebühne e.V. versteht sich als eine Plattform für die Umsetzung und Ausstellung von künstlerischen Ideen der Bürger Wuppertals sowie der Kunstwerke junger Künstler. Letztgenanntere werden konzeptionell unterstützt (Öffentlichkeitsarbeit, Planung, Durchführung, Vermittlung von finanzieller Unterstützung) und somit bei ersten Schritten in den Kunstmarkt begleitet. Dabei ist es dem Verein ausdrücklich wichtig, nicht auf regionale oder lokale Künstler fokussiert zu sein, sondern zieht auch viele Kunstschaffende aus der weiteren Region (Düsseldorf, Köln, Essen, Bonn) an. Neben den Kunstausstellungen werden zudem verschiedene Veranstaltungsformate realisiert, welche jedoch auch stark mit den sich engagierenden Mitgliedern und deren Ideen zusammenhängen. Dabei ist es in letzter Zeit zu mehreren Konzerten und Lesungen gekommen. Die Hebebühne beteiligt sich zudem regelmäßig und produktiv an der Wuppertaler Performancenacht.",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 169012125,
          "lastChanged": 1561575319523,
          "changedBy": {
            "id": 17,
            "username": "system"
          },
          "type": "WAY",
          "dataGroups": [],
          "bliDimensions": [
            33,
            35,
            29
          ],
          "tags": [
            {
              "id": 130,
              "key": "_gpd:aktionsradius",
              "value": "im gesamten Ort",
              "displayName": null,
              "source": "import"
            }
          ],
          "lon": 7.143848419189453,
          "lat": 51.26646423339844
        },
        {
          "id": 62,
          "created": 1561575106052,
          "name": "Wohngruppe Malerstraße/KomMal e.V.",
          "description": "Der Verein ist mit der Verwaltung des Mehrgenerationenhauses in der Malerstraße betraut. Die Inbetriebnahme, der Ausbau, sowie die Konzipierung des Projektes fand noch außerhalt des Vereinsrahmens statt. Das Haus soll ein generationsübergreifendes und barrierefreies Wohnen in einem energiesparendem Passivhaus ermöglichen. Der Verein KomMal dient nun als Kommunikationstreff, um das Projekt zum Einen der Öffentlichkeit zugänglich zu machen, sich andererseits an Initiativen und Stadtteilarbeiten organisiert beteiligen zu können. Bei Projekten des Vereins steht der Aspekt der Förderung von Kunst und Kultur im Vordergrund. Ausstellungen werden in den Gemeinschaftsräumen ausgerichtet, zudem finden in unregelmäßigen Abständen Lesungen statt. Als neue Ausrichtung wird die Vernetzung des Vereins sowie dessen Einbringung in die Quartiersarbeit angestrengt. Der Verein möchte sich zudem zunehmend in die Flüchtlingshilfe miteinklinken.",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 4644158658,
          "lastChanged": 1561575320340,
          "changedBy": {
            "id": 17,
            "username": "system"
          },
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [
            33,
            35,
            25,
            29,
            30,
            31
          ],
          "tags": [
            {
              "id": 143,
              "key": "_gpd:aktionsradius",
              "value": "im Ortsteil",
              "displayName": null,
              "source": "import"
            }
          ],
          "lon": 7.134419918060303,
          "lat": 51.264671325683594
        },
        {
          "id": 63,
          "created": 1561575101232,
          "name": "die Urbanisten e.V. Dortmund",
          "description": null,
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 6050640058,
          "lastChanged": 1561575321300,
          "changedBy": null,
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [],
          "tags": [],
          "lon": 7.438089847564697,
          "lat": 51.5126838684082
        },
        {
          "id": 159,
          "created": 1561575107742,
          "name": "/dev/tal e.V.",
          "description": "",
          "createdBy": {
            "id": 17,
            "username": "system"
          },
          "osmId": 1497543573,
          "lastChanged": 1561624331638,
          "changedBy": {
            "id": 20,
            "username": "admin"
          },
          "type": "NODE",
          "dataGroups": [],
          "bliDimensions": [
            32,
            33,
            34,
            35,
            {
              "id": 23,
              "name": "Einkommen",
              "description": "Materielle Ausstattung der Menschen (Einkommen und andere finanzielle Leistungen wie Rente oder Arbeitslosengeld).",
              "slug": null
            },
            {
              "id": 24,
              "name": "Arbeit",
              "description": "Die Verfügbarkeit von guten und sicheren Arbeitsplätzen.",
              "slug": null
            },
            25,
            26,
            {
              "id": 27,
              "name": "Work-Life-Balance",
              "description": "",
              "slug": null
            },
            28,
            29,
            30,
            31
          ],
          "tags": [
            {
              "id": 4552,
              "key": "_gpd:aktionsradius",
              "value": "Überregional",
              "displayName": null,
              "source": null
            }
          ],
          "lon": 7.145033836364746,
          "lat": 51.266719818115234
        }
      ],
      "bliDimensions": [
        33,
        34,
        35,
        24,
        29,
        30
      ],
      "tags": [
        {
          "id": 1009,
          "key": "_gpd:sharing_offers",
          "value": "Foodsharing Kühlschrank",
          "displayName": null,
          "source": null
        },
        {
          "id": 1010,
          "key": "_gpd:sustainable_nutrition",
          "value": "Foodsharing Kühlschrank",
          "displayName": null,
          "source": null
        },
        {
          "id": 54,
          "key": "_gpd:aktionsradius",
          "value": "überregional",
          "displayName": null,
          "source": null
        }
      ],
      "lon": 7.145003318786621,
      "lat": 51.266807556152344
    }
  ]
}

```

# Weitere Typen

## KeyValuePair

Der Typ KeyValuePair wird unter anderem Elementen, Datengruppen und Kategorien angehangen. Ein KeyValuePair hat folgende Felder:

Parameter | Typ | Beschreibung
--------- | ------- | -----------
id|number| Id des Elements
key|string| Der Schlüssel
value|string| Der Wert
displayName|string | Anzeigename
source| string | Quellenangabe (für wissenschaftliche Zwecke)



Das Geoportal hat unter anderem die Zielsetzung im Laufe der Entwicklung auch die OSM-Community zu unterstützen. Es sollen daher Daten, die klassischerweise bei OSM liegen auch dort gepflegt werden. Im Fall eines KeyValuePair (~tag bei OSM) sollen daher die im Geoportal liegenden Daten im Key mit dem Präfix `_gpd:` (~ Geoportal-Daten) versehen werden.

