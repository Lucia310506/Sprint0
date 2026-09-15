# Diseño Lógico — Publicador

**Origen:** `Publicador.h` (Arduino)
**Reponsabilidad:** Orquestar la publicación de mediciones como anuncios iBeacon.

---

## Notación

```
MedicionesID = { CO2 = 11, TEMPERATURA = 12, RUIDO = 13 }
```

---

## Clase

```
                 --------- Publicador ------------------
                 |
                 | beaconUUID: [Z]_16   (="EPSG-GTI-PROY-3A")
                 |
    (públicos)   | laEmisora: EmisoraBLE (nombre "GTI-3A", fabricante 0x004C, txPower 4)
                 | RSSI: Z = -53
                 |
                 |
                 --> Publicador() -->          (no enciende emisora: se hace en setup)
                 |
                 |
                 --> encenderEmisora() -->      (delega en laEmisora)
                 |
                 |
  valorCO2: Z  --> publicarCO2() -->           major = (CO2<<8)|contador; minor=valor;
 contador: N  -->                              emitir → esperar(tiempo) → detener
 tiempo: N    -->
                 |
                 |
 valorTemp: Z --> publicarTemperatura() -->   major = (TEMPERATURA<<8)|contador
 contador: N  -->                              minor = valorTemp
 tiempo: N    -->
                 |
                 --------------------------------------
```

---

## Contrato de emisión (encodificación en major/minor)

```
Medicion = ( tipo: MedicionesID, contador: N, valor: Z )

major = ( tipo << 8 ) | contador
minor = valor            (CO2 o temperatura)

beaconUUID = "EPSG-GTI-PROY-3A"
```

### Flujo interno de `publicarCO2` / `publicarTemperatura`

```
1. major = ( tipo << 8 ) | contador
2. laEmisora.emitirAnuncioIBeacon( beaconUUID, major, valor, RSSI )
3. esperar( tiempo )
4. laEmisora.detenerAnuncio()
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `Publicador()` | pública | muta (construct) | — | — |
| `encenderEmisora()` | pública | muta | — | — |
| `publicarCO2()` | pública | muta | `valorCO2: Z`, `contador: N`, `tiempo: N` | — |
| `publicarTemperatura()` | pública | muta | `valorTemp: Z`, `contador: N`, `tiempo: N` | — |