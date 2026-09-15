# Diseño Lógico — PuertoSerie

**Origen:** `PuertoSerie.h` (Arduino)
**Responsabilidad:** Encapsular la comunicación por puerto serie (depurado de la placa).

---

## Clase

```
                 --------- PuertoSerie -----------------
                 |
                 |
  baudios: N   --> PuertoSerie() -->           (Serial.begin)
                 |
                 |
                 --> esperarDisponible() -->    (while !Serial delay(10))
                 |
                 |
  mensaje: Text --> escribir() -->             (templated: serial.print)
  mensaje: N   -->           (acepta números y texto)
                 |
                 --------------------------------------
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `PuertoSerie()` | pública | muta (construct) | `baudios: N` | — |
| `esperarDisponible()` | pública | muta (bloquea) | — | — |
| `escribir()` | pública | muta | `mensaje: Text` o `mensaje: N` | — |