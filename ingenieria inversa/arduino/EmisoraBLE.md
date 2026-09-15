# Diseño Lógico — EmisoraBLE

**Origen:** `EmisoraBLE.h` (Arduino)
**Responsabilidad:** Encapsular la emisora Bluetooth Low Energy (Bluefruit) para emitir anuncios iBeacon, gestionar servicios y conexiones.

---

## Notación

```
CallbackConexionEstablecida = ( connHandle: N ) --> void
CallbackConexionTerminada   = ( connHandle: N, reason: N ) --> void
```

---

## Clase

```
                 --------- EmisoraBLE ------------------
                 |
                 | nombreEmisora: Text
                 | fabricanteID: N
                 | txPower: Z
                 |
                 |
     nombre: Text --> EmisoraBLE() -->
  fabricanteID: N -->
       txPower: Z -->
                 |
                 |
                 --> encenderEmisora() -->          (inicializa Bluefruit)
                 |
                 |
 cbce: Callback --> encenderEmisora() -->         (variante con callbacks)
 cbct: Callback -->
                 |
                 |
                 --> detenerAnuncio() -->
                 |
                 |
              B <-- estaAnunciando() <--
                 |
                 |
     uuid: [N]_16 --> emitirAnuncioIBeacon() -->   (beacon estándar 30 bytes)
      major: N  -->          (crea BLEBeacon, fija fabricante/txPower/nombre)
      minor: N  -->
        rssi: Z -->
                 |
                 |
      carga: Text --> emitirAnuncioIBeaconLibre() -->  (21 bytes libres, sin formato)
 tamanyoCarga: N -->      (copia carga en prefijo 0x4C:00:02:15 + payload)
                 |
                 |
 svc: ServicioEnEmisora --> anyadirServicio() -->
                  B   <--
                 |
                 |
 svc: ServicioEnEmisora, --> anyadirServicioConSusCaracteristicas() -->
 car: Caracteristica,    -->          (variádica, recursiva)
 resto: ...Caracteristica-->
                  B   <--
                 |
                 |
 svc: ServicioEnEmisora, --> anyadirServicioConSusCaracteristicasYActivar() -->
 resto: ...Caracteristica -->        (como la anterior + activarServicio())
                  B   <--
                 |
                 |
             cb: Callback --> instalarCallbackConexionEstablecida() -->
                 |
                 |
             cb: Callback --> instalarCallbackConexionTerminada() -->
                 |
                 |
     connHandle: N   --> getConexion() <--
          Conexion   <--
                 |
                 --------------------------------------
```

---

## Formato del anuncio iBeacon estándar (30 bytes)

```
prefijo 9B:   0x02 0x01 0x06 | 0x1A 0xFF | 0x4C 0x00 | 0x02 | 0x15
uuid 16B:     identificador del producto ("EPSG-GTI-PROY-3A")
major 2B:     ( tipo << 8 ) | contador
minor 2B:     valor de la medición
txPower 1B:   potencia de transmisión
```

### Formato del anuncio libre (21 bytes)

```
companyID 2B: 0x4C 0x00
iBeaconType 1B: 0x02
iBeaconLength 1B: 0x15 (21)
payload 21B:  contenido arbitrario copiado con memcpy (límite 21)
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `EmisoraBLE()` | pública | muta (construct) | `nombre: Text`, `fabricanteID: N`, `txPower: Z` | — |
| `encenderEmisora()` | pública | muta | — | — |
| `encenderEmisora(cbce, cbct)` | pública | muta | `cbce`, `cbct: Callback` | — |
| `detenerAnuncio()` | pública | muta | — | — |
| `estaAnunciando()` | pública | lectura | — | `B` |
| `emitirAnuncioIBeacon()` | pública | muta | `uuid: [N]_16`, `major: N`, `minor: N`, `rssi: Z` | — |
| `emitirAnuncioIBeaconLibre()` | pública | muta | `carga: Text`, `tamanyoCarga: N` | — |
| `anyadirServicio()` | pública | muta | `svc: ServicioEnEmisora` | `B` |
| `...ConSusCaracteristicas()` | pública | muta | `svc`, `car`, `resto...` | `B` |
| `...YActivar()` | pública | muta | `svc`, `resto...` | `B` |
| `instalarCallbackConexionEstablecida()` | pública | muta | `cb: Callback` | — |
| `instalarCallbackConexionTerminada()` | pública | muta | `cb: Callback` | — |
| `getConexion()` | pública | lectura | `connHandle: N` | `Conexion` |