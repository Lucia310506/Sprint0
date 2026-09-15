# Diseño Lógico — TramaIBeacon

**Origen:** `app/src/main/java/com/example/ldiamur/btlealumnos2021app/TramaIBeacon.java`
**Responsabilidad:** Decodificar la trama cruda de 30 bytes recibida por BLE en los campos estructurados del iBeacon (prefijo, uuid, major, minor, txPower) y sus sub-campos.

---

## Notación

```
TramaIBeacon = ( prefijo: [Z]_9, uuid: [Z]_16, major: [Z]_2, minor: [Z]_2,
                 txPower: Z, advFlags: [Z]_3, advHeader: [Z]_2,
                 companyID: [Z]_2, iBeaconType: Z, iBeaconLength: Z )
```

### Mapa de rangos (troceado de los 30 bytes)

```
losBytes[ 0..8  ] → prefijo        (9 bytes)
losBytes[ 9..24 ] → uuid           (16 bytes)
losBytes[25..26 ] → major          (2 bytes)
losBytes[27..28 ] → minor          (2 bytes)
losBytes[ 29   ] → txPower         (1 byte)

prefijo[0..2] → advFlags
prefijo[3..4] → advHeader
prefijo[5..6] → companyID
prefijo[7]    → iBeaconType
prefijo[8]    → iBeaconLength
```

---

## Clase

```
                 --------- TramaIBeacon ----------------
                 |
                 | losBytes: [Z]
                 | prefijo: [Z]_9
                 | uuid: [Z]_16
                 | major: [Z]_2
                 | minor: [Z]_2
                 | txPower: Z
                 | advFlags: [Z]_3
                 | advHeader: [Z]_2
                 | companyID: [Z]_2
                 | iBeaconType: Z
                 | iBeaconLength: Z
                 |
                 |
    bytes: [Z]  --> TramaIBeacon() -->              (trocea losBytes por rangos)
                 |
                 |
    [Z]_9   <-- getPrefijo() <--
                 |
                 |
   [Z]_16   <-- getUUID() <--
                 |
                 |
    [Z]_2   <-- getMajor() <--
                 |
                 |
    [Z]_2   <-- getMinor() <--
                 |
                 |
       Z    <-- getTxPower() <--
                 |
                 |
    [Z]     <-- getLosBytes() <--
                 |
                 |
    [Z]_3   <-- getAdvFlags() <--
                 |
                 |
    [Z]_2   <-- getAdvHeader() <--
                 |
                 |
    [Z]_2   <-- getCompanyID() <--
                 |
                 |
       Z    <-- getiBeaconType() <--
                 |
                 |
       Z    <-- getiBeaconLength() <--
                 |
                 --------------------------------------
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `TramaIBeacon()` | pública | muta (construct) | `bytes: [Z]` | — |
| `getPrefijo()` | pública | lectura | — | `[Z]_9` |
| `getUUID()` | pública | lectura | — | `[Z]_16` |
| `getMajor()` | pública | lectura | — | `[Z]_2` |
| `getMinor()` | pública | lectura | — | `[Z]_2` |
| `getTxPower()` | pública | lectura | — | `Z` |
| `getLosBytes()` | pública | lectura | — | `[Z]` |
| `getAdvFlags()` | pública | lectura | — | `[Z]_3` |
| `getAdvHeader()` | pública | lectura | — | `[Z]_2` |
| `getCompanyID()` | pública | lectura | — | `[Z]_2` |
| `getiBeaconType()` | pública | lectura | — | `Z` |
| `getiBeaconLength()` | pública | lectura | — | `Z` |