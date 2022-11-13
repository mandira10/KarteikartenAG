# Detaillierte Anwendungsfälle
## Kategorien & Karteikästen
### Name:
N26 Karteikasten aus mehreren Kategorien erstellen
### Akteure:
`Lehrer Leo` oder `Studi Susi`
### Vorbedingungen:
- Es existiert bereits mindestens eine Kategorie
- Die ausgewählten Kategorien enthalten mindestens eine Karte
### Regulärer Ablauf:
- Nutzer (`Lehrer Leo` oder `Studi Susi`) wechselt zur Kategorie-Übersicht
- Nutzer wählt eine/mehrere Kategorie(n) aus
- Nutzer führt die Aktion "Karteikasten" auf der Kategorie-Auswahl aus
- Nutzer wechselt damit in eine Ansicht zur Karteikasten-Bearbeitung
- Nutzer gibt dem neuen Karteikasten einen Namen
- Nutzer ändert ggf. weitere Einstellungen (Lernsystem, initiale Kartenreihenfolge)
- Nutzer bestätigt aktuellen Zustand und erstellt den Karteikasten
### Varianten:
- Nutzer wählt in der Karteikasten-Bearbeitungs-Übersicht einzelne Karteikarten dazu/ab
- Nutzer fügt aus der Karteikasten-Bearbeitungs-Übersicht weitere Kategorien hinzu/ab
- Nutzer bricht den Prozess ab, bevor der Karteikasten erstellt wird
- Nutzer betreibt Client mit Server und wählt in den Karteikasten-Einstellungen "privat/öffentlich" aus
### Nachbedingungen:
- Es wurde ein neuer Karteikasten angelegt
  -> Taucht in der Karteikasten-Übersicht auf
  -> Hat initial keinen Lernfortschritt
### Fehler-/Ausnahmefälle mit deren Nachbedinungen:
- Die ausgewählten Kategorien enthalten keine Karteikarten
  -> es wird eine entsprechende Warnung angezeigt
  -> man kann nicht bestätigen und Karteikasten erstellen
- Der ausgewählte Karteikastennamen ist bereits vergeben
  -> entsprechender Fehler wird angezeigt
  -> man kann nicht bestätigen und Karteikasten erstellen
