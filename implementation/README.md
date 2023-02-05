# KarteikartenAG
SWP WS-2022

## Installation
Im Repository befindet sich ein git submodule, dass initialisiert werden muss.
```sh
git clone --recurse-submodules git@gitlab.informatik.uni-bremen.de:wete/swpws2022_karteikartenag.git
```

Oder

```sh
git clone git@gitlab.informatik.uni-bremen.de:wete/swpws2022_karteikartenag.git
git submodule update --init --remote --recursive
```

Die folgenden plattformabhängigen Abschnitte zeigen bekannte Abhängigkeiten/Voraussetzungen,
mögliche Workarounds und die benötigten Befehle zum Kompilieren der Anwendung.
Danach kann es kompiliert und ausgeführt werden.

### Linux
Benötigt wird:
- X Window System
  - falls [wayland](https://wayland.freedesktop.org/) verwendet wird, kann die Installation von `xwayland` (deb) oder `xorg-x11-server-Xwayland` (rpm) helfen.
- [GLIBC](https://www.gnu.org/software/libc/) > 2.14 (empfohlen >= 2.27)
- Java JDK-17 z.B. `openjdk-17-jdk` (deb) oder `java-17-openjdk` (rpm)
- Maven (empfohlen >= 3.6.3)

```sh
# in den Implementations-Ordner vom Repo wechseln
cd swpws2022_karteikartenag/implementation
./build.sh
./run.sh
```


### Mac
Bekannte Probleme:
- Fehler beim rendern der GUI, wenn ein externer Monitor mit anderer Auflösung als der Hauptmonitor angeschlossen ist.

Ansonsten wie bei der Linux-Installation
```sh
# in den Implementations-Ordner vom Repo wechseln
cd swpws2022_karteikartenag/implementation
./build.sh  # Kompilieren
./run.sh    # Programm starten
```


### Windows
Benötigte Programme:
- JDK-17
- Maven

Wenn [chocolatey](https://community.chocolatey.org/) verwendet wird:
```cmd
choco install zulu17
choco install maven
```

Zum Kompilieren und starten
```cmd
cd swpws2022_karteikartenag/implementation
.\build.bat
.\run.bat
```


## Projektstruktur
Im Ordner `implementation` befindet sich unsere Karteikartensystem-Implementierung.
Das Git-Submodule `gum-gui-java` ist die verwendete GUI-Bibliothek.

Der Hauptteil befindet sich unter `client/src/main/java/com/swp/`.
Dort wird das Programm in `GUI`, `Controller`, `Logic`, `Persistence` und `DataModel` unterteilt.
