# Diseño Lógico — MainActivity

**Origen:** `app/src/main/java/com/example/ldiamur/btlealumnos2021app/MainActivity.java`
**Responsabilidad:** Activity principal: inicializar Bluetooth, escanear dispositivos BLE (todos o por nombre), decodificar y mostrar la información de los iBeacons detectados.

---

## Notación

```
ResultadoEscaneo = ( dispositivo: DispositivoBLE, bytes: [Z], rssi: Z )
DispositivoBLE   = ( nombre: Text, direccion: Text )
CallbackEscaneo  = { onScanResult, onBatchScanResults, onScanFailed }
```

---

## Clase

```
                 --------- MainActivity -----------------
                 |  (extends AppCompatActivity)
                 |
                 | ETIQUETA_LOG: Text (const)
                 | CODIGO_PETICION_PERMISOS: N (const)
                 | elEscanner: Escaner_BLE
                 | callbackDelEscaneo: CallbackEscaneo | null
                 |
                 |   (internas/privadas)
                 |   --> buscarTodosLosDispositivosBTLE() -->
                 |   --> detenerBusquedaDispositivosBTLE() -->
                 |   dispBuscado: Text --> buscarEsteDispositivoBTLE() -->
                 |   resultado: ResultadoEscaneo --> mostrarInformacionDispositivoBTLE() -->
                 |   --> inicializarBlueTooth() -->
                 |   res: Resultado --> onScanResult() -->   (callback anónimo del escáner)
                 |
                 |
     estado: Bundle --> onCreate() -->       (ciclo de vida: llama inicializarBlueTooth)
                 |
                 |
        vista: Vista --> botonBuscarDispositivosBTLEPulsado() -->
                 |
                 |
        vista: Vista --> botonBuscarNuestroDispositivoBTLEPulsado() -->
                 |             (busca "fistro" por nombre)
                 |
                 |
        vista: Vista --> botonDetenerBusquedaDispositivosBTLEPulsado() -->
                 |
                 |
  codigo: N,      --> onRequestPermissionsResult() -->   (override)
 permisos: [Text],
resultados: [N] -->
                 |
                 --------------------------------------
```

---

## Desglose de los internos

### `inicializarBlueTooth()`

```
1. adaptador = BluetoothAdapter.getDefaultAdapter()
2. adaptador.enable()
3. elEscanner = adaptador.getBluetoothLeScanner()
4. si no existen permisos BLUETOOTH, BLUETOOTH_ADMIN, ACCESS_FINE_LOCATION
     → pedirlos (CODIGO_PETICION_PERMISOS)
```

### `buscarTodosLosDispositivosBTLE()`

```
1. crear ScanCallback anónimo { onScanResult → mostrarInformacionDispositivoBTLE }
2. elEscanner.startScan( callback )
```

### `buscarEsteDispositivoBTLE(dispositivoBuscado)`

```
1. crear el mismo ScanCallback (filtrado por nombre "fistro")
2. ScanFilter.Builder().setDeviceName( dispositivoBuscado )
3. elEscanner.startScan( callback )
```

### `detenerBusquedaDispositivosBTLE()`

```
si callbackDelEscaneo != null → elEscanner.stopScan( callback ); callback = null
```

### `mostrarInformacionDispositivoBTLE(resultado: ResultadoEscaneo)`

```
tib = TramaIBeacon( resultado.bytes )
log( prefijo, advFlags, advHeader, companyID, iBeaconType, iBeaconLength )
log( uuid: UUID, major → bytesToInt, minor → bytesToInt, txPower )
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `buscarTodosLosDispositivosBTLE()` | privada | muta | — | — |
| `buscarEsteDispositivoBTLE()` | privada | muta | `dispositivoBuscado: Text` | — |
| `detenerBusquedaDispositivosBTLE()` | privada | muta | — | — |
| `mostrarInformacionDispositivoBTLE()` | privada | lectura | `resultado: ResultadoEscaneo` | — |
| `inicializarBlueTooth()` | privada | muta | — | — |
| `onCreate()` | pública (override) | muta | `estado: Bundle` | — |
| `botonBuscarDispositivosBTLEPulsado()` | pública | muta | `vista: Vista` | — |
| `botonBuscarNuestroDispositivoBTLEPulsado()` | pública | muta | `vista: Vista` | — |
| `botonDetenerBusquedaDispositivosBTLEPulsado()` | pública | muta | `vista: Vista` | — |
| `onRequestPermissionsResult()` | pública (override) | muta | `codigo: N`, `permisos: [Text]`, `resultados: [N]` | — |