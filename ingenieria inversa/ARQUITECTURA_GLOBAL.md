# Diseño Lógico — Arquitectura Global

**Ámbito:** Sprint0 — sistema de medición con Arduino → App Android → REST → Web PHP.

---

## Componentes

| Componente | Proyecto | Responsabilidad | Diseño lógico |
| :--- | :--- | :--- | :--- |
| Arduino (emisor) | `HolaMundoIBeacon` | Medir sensores y emitirlos como iBeacons BLE | `HolaMundoIBeacon/HolaMundoIBeacon/*.md` |
| App Android | `BTLEAlumnos2021hechoapp` | Escanear BLE, decodificar iBeacons, (falta) enviar a REST | `BTLEAlumnos2021hechoapp/.../*.md` |
| Web PHP | `EsqueletoWebAppEnPHPConSesion` | Recibir/atender peticiones REST con sesión | `EsqueletoWebApp.../.../*.md` |

---

## Diagrama de flujo de datos

```
┌───────────────┐   BLE / iBeacon broadcast     ┌───────────────┐   HTTP + JSON      ┌──────────────────┐
│    ARDUINO    │ ────────────────────────────> │ APP ANDROID   │ ─────────────────> │   WEB PHP        │
│ HolaMundoIB.  │   anuncios de publicidad      │ BTLEAlumnos   │   GET/POST REST    │ EsqueletoWebApp  │
│ (emisor)      │   (sin conexión)              │ 2021hechoapp  │                    │ ConSesion        │
└───────────────┘                               └───────┬───────┘                    └──────────────────┘
                                                        │
                                                        │  PeticionarioREST
                                                        v
                                        ┌───────────────────────────────┐
                                        │  rest/hacerLogin.php          │
                                        │  rest/diHola.php  (sessions)  │
                                        └───────────────────────────────┘
```

---

## Contrato entre capas (encodificación de la medición)

```
Medicion = ( tipo: MedicionesID, contador: N, valor: Z )

major = ( tipo << 8 ) | contador          ── emitido por el Arduino
minor = valor                             ── (CO2 o temperatura)

TramaIBeaconEntrante: [Z]_30
   [0..8]  = prefijo       (advFlags 3 + advHeader 2 + companyID 2 + tipo + longitud)
   [9..24] = uuid
   [25..26]= major  → tipo y contador
   [27..28]= minor  → valor de la medición
   [29]    = txPower
```

---

## Flujo de datos integral

```
1. Arduino (loop)
     medirCO2()→235 ──┐
                      ├─> publicarCO2(235, cont, 1000)  → iBeacon major=(11<<8|cont), minor=235
     medirTemp()→-12 ─┘
                      ├─> publicarTemperatura(-12, cont, 1000) → major=(12<<8|cont), minor=-12
                      └─> emitirAnuncioIBeaconLibre(...) → emisión de prueba (payload libre)

2. App Android (MainActivity)
     inicializarBlueTooth() → permisos + BluetoothLeScanner
     startScan(ScanCallback)  ──recibe 30 bytes──>  TramaIBeacon(bytes)
        getUUID → "EPSG-GTI-PROY-3A"
        getMajor → tipo (CO2=0x0B / TEMP=0x0C) + contador
        getMinor → valor medido
     [pendiente: PeticionarioREST → enviar las mediciones al backend]

3. Servidor PHP
     la app llamaría:  POST/GET rest/...  con JSON
     rest/*.php  → valida sesión → logica/*.php → JSON de vuelta

4. Web (navegador)
     Aplicacion.html → login → logicaFake (AJAX) → rest/hacerLogin.php → $_SESSION
     posteriormente   → logicaFake (AJAX) → rest/diHola.php → muestra saludo si hay sesión
```

---

## Gaps detectados (para el diseño final)

| # | Gap | Proyecto afectado |
| :-- | :--- | :--- |
| 1 | `MainActivity` no llama a `PeticionarioREST` (el envío REST no está activado) | Android |
| 2 | El backend PHP solo tiene "hola": falta `rest/recibirMedicion.php` para recibir {tipo, contador, valor, rssi} y guardarlos (BD) | PHP |
| 3 | `Medidor` es un stub (valores fijos 235 / −12) | Arduino |
| 4 | `TramaIBeacon.getMajor/getMinor` usa `bytesToInt` (BigInteger); para temperaturas negativas convendría `bytesToIntOK` (complemento a 2) | Android |

---

## Índice de documentos por proyecto

### Arduino (`HolaMundoIBeacon/HolaMundoIBeacon/`)
- `HolaMundoIBeacon.md` — programa principal (setup/loop)
- `Publicador.md` — publicación de mediciones como iBeacon
- `EmisoraBLE.md` — emisora BLE y anuncios
- `Medidor.md` — sensor (stub)
- `LED.md` — LED indicador
- `PuertoSerie.md` — depuración serie
- `ServicioEnEmisora.md` — servicio BLE
- `Caracteristica.md` — característica BLE

### Android (`BTLEAlumnos2021hechoapp/app/src/main/java/.../`)
- `MainActivity.md` — escaneo BLE y UI
- `TramaIBeacon.md` — decodificación del anuncio iBeacon
- `Utilidades.md` — conversiones byte/string/UUID
- `PeticionarioREST.md` — cliente REST asíncrono

### PHP (`EsqueletoWebAppEnPHPConSesion/.../`)
- `logica/hacerLogin.md` — lógica de autenticación
- `logica/diHola.md` — lógica de saludo
- `rest/hacerLogin.md` — endpoint login + sesión
- `rest/diHola.md` — endpoint saludo protegido
- `ux/logicaFake/hacerLogin.md` — cliente AJAX login
- `ux/logicaFake/diHola.md` — cliente AJAX saludo
- `ux/Aplicacion.md` — UI web