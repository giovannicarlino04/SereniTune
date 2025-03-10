# SereniTune

SereniTune è un'applicazione per la gestione e il monitoraggio dei dispositivi di climatizzazione, progettata per semplificare il controllo e la manutenzione dei sistemi HVAC.

## Caratteristiche

- Gestione completa dei dispositivi di climatizzazione
- Monitoraggio in tempo reale
- Interfaccia utente intuitiva
- Sistema di notifiche per manutenzioni e allarmi
- Reportistica dettagliata

## Requisiti di Sistema

- Python 3.8 o superiore
- pip (gestore pacchetti Python)
- Ambiente virtuale (consigliato)

## Installazione

1. Clona il repository:
```bash
git clone https://github.com/yourusername/SereniTune.git
cd SereniTune
```

2. Crea e attiva un ambiente virtuale:
```bash
python -m venv venv
source venv/bin/activate  # Per Linux/Mac
venv\Scripts\activate     # Per Windows
```

3. Installa le dipendenze:
```bash
pip install -r requirements.txt
```

## Configurazione

1. Crea un file `.env` nella root del progetto
2. Configura le variabili d'ambiente necessarie (vedi `.env.example`)

## Utilizzo

Per avviare l'applicazione:

```bash
python main.py
```

## Struttura del Progetto

```
SereniTune/
├── src/
│   ├── controllers/
│   ├── models/
│   ├── views/
│   └── utils/
├── tests/
├── docs/
├── requirements.txt
└── README.md
```

## Contribuire

Le pull request sono benvenute. Per modifiche importanti, apri prima un issue per discutere cosa vorresti cambiare.

## Licenza

[MIT](https://choosealicense.com/licenses/mit/)

## Contatti

Per domande o supporto, contatta il team di sviluppo all'indirizzo: support@serenitune.com 