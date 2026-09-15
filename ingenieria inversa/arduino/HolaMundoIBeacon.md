# Diseño Lógico — Programa Principal (HolaMundoIBeacon)

**Origen:** `HolaMundoIBeacon.ino` (Arduino)
**Responsabilidad:** Coordinar la inicialización y el bucle principal: medir y publicar mediciones como iBeacons.

---

## Variables globales

```
elLED: LED          (pin 7)
elPuerto: PuertoSerie (115200 baud)
elPublicador: Publicador
elMedidor: Medidor
cont: N   (contador de bucle, en namespace Loop)
```

---

## Flujo

```
  --> setup() -->
       elPuerto.esperarDisponible()
       inicializarPlaquita()
       elPublicador.encenderEmisora()
       elMedidor.iniciarMedidor()
       esperar(1000)
       elPuerto.escribir("---- setup(): fin ----")

  --> loop() -->        (repite indefinidamente)
       cont++
       elPuerto.escribir("---- loop(): empieza " + cont)

       lucecitas()                       → LED: brillar(100) x3 + brillar(1000)

       valorCO2 => elMedidor.medirCO2()  → Z
       elPublicador.publicarCO2(valorCO2, cont, 1000)

       valorTemperatura => elMedidor.medirTemperatura()  → Z
       elPublicador.publicarTemperatura(valorTemperatura, cont, 1000)

       elPublicador.laEmisora.emitirAnuncioIBeaconLibre("MolaMolaMolaMolaMolaM", 21)

       esperar(2000)
       elPublicador.laEmisora.detenerAnuncio()
       elPuerto.escribir("---- loop(): acaba **** " + cont)
```

---

## Resumen de composición

```
Programa principal (.ino)
 │
 ├─ elLED: LED ──────────────── (ver LED.md)
 ├─ elPuerto: PuertoSerie ───── (ver PuertoSerie.md)
 ├─ elPublicador: Publicador ── (ver Publicador.md)
 │     └─ laEmisora: EmisoraBLE (ver EmisoraBLE.md)
 ├─ elMedidor: Medidor ──────── (ver Medidor.md)
```