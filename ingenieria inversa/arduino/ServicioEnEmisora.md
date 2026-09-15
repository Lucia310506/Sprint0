# Diseño Lógico — ServicioEnEmisora

**Origen:** `ServicioEnEmisora.h` (Arduino)
**Responsabilidad:** Abstraer un servicio BLE con sus características sobre Bluefruit. Incluye la clase anidada `Caracteristica` (ver `Caracteristica.md`).

---

## Notación / utilidades libres

```
 p: [T]_n, n: N   --> alReves() <--
      [T]_n       <--     (invierte el array, in situ)

pString: Text, pUint: [N]_m, tamMax: N --> stringAUint8AlReves() <--
                     [N]_m             <--      (string copiado al revés)
```

Estas utilidades convierten un nombre legible en un UUID de 16 bytes con los bytes invertidos (little-endian), tanto para el servicio como para las características.

---

## Clase

```
                 --------- ServicioEnEmisora ------------
                 |
                 | uuidServicio: [Z]_16        (UUID en little-endian)
                 | elServicio: BLE_Servicio
                 | lasCaracteristicas: [ Caracteristica* ]
                 |
                 |
 nombreServicio: Text --> ServicioEnEmisora() -->  (convierte nombre → UUID al revés)
                 |
                 |
                 --> escribeUUID() <--               (debug por Serial)
                 |
                 |
 car: Caracteristica --> anyadirCaracteristica() -->  (push_back)
                 |
                 |
                 --> activarServicio() -->            (elServicio.begin + activar todas)
                 |
                 |
      BLEService <-- operator BLEService&() <--       (conversión de tipo)
                 |
                 --------------------------------------
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `ServicioEnEmisora()` | pública | muta (construct) | `nombreServicio: Text` | — |
| `escribeUUID()` | pública | lectura | — | — |
| `anyadirCaracteristica()` | pública | muta | `car: Caracteristica` | — |
| `activarServicio()` | pública | muta | — | — |
| `operator BLEService&()` | pública | lectura | — | `BLEService` |