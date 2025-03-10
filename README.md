# SereniTune

SereniTune è un'applicazione Android per la gestione e il monitoraggio dei dispositivi di climatizzazione, progettata per semplificare il controllo e la manutenzione dei sistemi HVAC direttamente dal tuo smartphone.

## Caratteristiche

- Gestione completa dei dispositivi di climatizzazione
- Monitoraggio in tempo reale
- Interfaccia utente intuitiva e moderna
- Sistema di notifiche per manutenzioni e allarmi
- Reportistica dettagliata
- Supporto offline
- Sincronizzazione cloud

## Requisiti di Sistema

- Android 6.0 (API level 23) o superiore
- Android Studio 4.0 o superiore
- Gradle 6.7 o superiore
- JDK 11 o superiore

## Installazione

1. Clona il repository:
```bash
git clone https://github.com/yourusername/SereniTune.git
```

2. Apri il progetto in Android Studio:
   - Avvia Android Studio
   - Seleziona "Open an existing Android Studio project"
   - Naviga fino alla cartella del progetto e selezionala

3. Sincronizza il progetto con Gradle:
   - Clicca su "Sync Project with Gradle Files"
   - Attendi il completamento della sincronizzazione

## Configurazione

1. Assicurati di avere configurato correttamente il tuo ambiente di sviluppo Android
2. Configura le variabili d'ambiente necessarie nel file `local.properties`
3. Se necessario, configura le chiavi API nel file `gradle.properties`

## Struttura del Progetto

```
SereniTune/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/serenitune/
│   │   │   │       ├── activities/
│   │   │   │       ├── adapters/
│   │   │   │       ├── models/
│   │   │   │       └── utils/
│   │   │   └── res/
│   │   └── test/
│   └── build.gradle
├── gradle/
├── build.gradle
└── settings.gradle
```

## Sviluppo

Per eseguire l'applicazione:
1. Seleziona un dispositivo o un emulatore
2. Clicca su "Run" (pulsante play verde) o premi Shift+F10

## Contribuire

Le pull request sono benvenute. Per modifiche importanti, apri prima un issue per discutere cosa vorresti cambiare.

## Licenza

[MIT](https://choosealicense.com/licenses/mit/)

## Contatti

Per domande o supporto, contatta il team di sviluppo all'indirizzo: support@serenitune.com 