# Geoportal-Backend

Work-In-Progress-Version des Geoportals - 

Projekthomepage - (http://www.transformationsstadt.de)

## Entwicklungsserver

Den Entwicklungsserver aufsetzen

### Voraussetzungen ###

* Java JDK 8
* Postgresql 9.6
* Maven 
* Apache Tomcat 9

### Datenbank konfigurieren ###

Im aktuellen Zustand setzt das Backend eine laufende Postgresql-Datenbank auf dem gleichen Host (localhost) voraus.
Der vorkonfigurierte Zustand sieht folgende Daten vor:

* Host: localhost
* Benutzer: dev
* Password: dev
* Datenbank: dev

 Diese Daten können unter 
 
 > 
 > src/main/resources/hibernate.cfg.xml
 > 
 
 geändert werden.
 
 ### Application bauen ###
 
 Maven-Dependencies installieren:
 
 > 
 > mvn package
 > 
 
 Die Erzeugte Datei 
 
 >
 > target\geoportal.war 
 >
 
 lässt sich nun auf dem Tomcat deployen.
 
 
 ### Setup
 
 Einen GET-Request auf
 
 >
 > http://localhost:8080/geoportal/setup/
 >
 
 ausführen. Dadurch werden BLI-Dimension, die Benutzer "System", "Admin" und "Redakteur" angelegt. Siehe main/java/de/transformationsstadt/geoportal/api/Setup.java .
 
 Einen GET-Request auf
 
 >
 > http://localhost:8080/geoportal/runimport/ 
 >
 
 ausführen.
 
 Dadurch werden die vorgefertigten Datensätze aus der Datei main/resources/data.csv eingelesen.
 
 
 