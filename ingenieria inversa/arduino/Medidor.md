# Diseño Lógico — Medidor

**Origen:** `Medidor.h` (Arduino)
**Responsabilidad:** Abstraer el sensor para medir CO2 y temperatura. Estado actual: *stub* (valores fijos).

---

## Clase

```
                 --------- Medidor ---------------------
                 |
                 |
                 --> Medidor() -->
                 |
                 |
                 --> iniciarMedidor() -->       (no-op; donde iría la init HW)
                 |
                 |
              Z <-- medirCO2() <--              (hardcodeado: 235)
                 |
                 |
              Z <-- medirTemperatura() <--      (hardcodeado: -12)
                 |
                 --------------------------------------
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `Medidor()` | pública | muta (construct) | — | — |
| `iniciarMedidor()` | pública | muta | — | — |
| `medirCO2()` | pública | lectura | — | `Z` (235) |
| `medirTemperatura()` | pública | lectura | — | `Z` (-12) |

---

## Pendiente (diseño final)

- Sustituir los valores constantes por lectura real del sensor.
- `iniciarMedidor()` debería inicializar el hardware (p. ej. sensor CO2 vía `Serial1`).