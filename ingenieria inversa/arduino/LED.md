# Diseño Lógico — LED

**Origen:** `LED.h` (Arduino) — incluye también la función libre `esperar()`.
**Responsabilidad:** Control de un LED de la placa (hearth-beat visual).

---

## Función libre

```
  tiempo: N   --> esperar() <--      (envoltura de delay())
```

---

## Clase

```
                 --------- LED -------------------------
                 |
                 | numeroLED: N
                 | encendido: B
                 |
                 |
  numero: N    --> LED() -->                   (pinMode OUTPUT + apagar)
                 |
                 |
                 --> encender() -->             (digitalWrite HIGH)
                 |
                 |
                 --> apagar() -->               (digitalWrite LOW)
                 |
                 |
                 --> alternar() -->             (toggle según encendido)
                 |
                 |
  tiempo: N    --> brillar() -->               (encender + esperar + apagar)
                 |
                 --------------------------------------
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `LED()` | pública | muta (construct) | `numero: N` | — |
| `encender()` | pública | muta | — | — |
| `apagar()` | pública | muta | — | — |
| `alternar()` | pública | muta | — | — |
| `brillar()` | pública | muta | `tiempo: N` | — |