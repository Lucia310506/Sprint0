# Diseño Lógico — Caracteristica

**Origen:** `ServicioEnEmisora.h` (Arduino) — clase anidada dentro de `ServicioEnEmisora`.
**Responsabilidad:** Abstraer una característica BLE (UUID, propiedades, permisos, tamaño) sobre Bluefruit.

---

## Notación

```
CallbackCaracteristicaEscrita = ( conn_handle: N, chr: Caracteristica,
                                  data: [Z], len: N ) --> void
```

---

## Clase

```
                 --------- Caracteristica ---------------
                 |
                 | uuidCaracteristica: [Z]_16
                 | laCaracteristica: BLE_Caracteristica
                 |
                 |   (internas/privadas)
                 |      props: [Z]_1 --> asignarPropiedades() -->
                 |      permR: Z, permW: Z --> asignarPermisos() -->
                 |      tam: N --> asignarTamanyoDatos() -->
                 |
                 |
       nombre: Text --> Caracteristica() -->           (uuid al revés desde el nombre)
                 |
                 |
      nombre: Text, --> Caracteristica() -->           (invoca al 1º + asignarPropPerTam)
         props: N,  -->
   permisoRead: N,  -->
  permisoWrite: N,  -->
           tam: N  -->
                 |
                 |
       props: N,   --> asignarPropiedadesPermisosYTamanyoDatos() -->  (3 delegaciones)
  permisoRead: N,  -->
 permisoWrite: N,  -->
           tam: N  -->
                 |
                 |
        texto: Text --> escribirDatos() <--
                 N  <--
                 |
                 |
        texto: Text --> notificarDatos() <--
                 N  <--
                 |
                 |
            cb: Callback --> instalarCallbackCaracteristicaEscrita() -->
                 |
                 |
                 --> activar() -->                    (laCaracteristica.begin)
                 |
                 --------------------------------------
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `Caracteristica(nombre)` | pública | muta (construct) | `nombre: Text` | — |
| `Caracteristica(nombre, props, permR, permW, tam)` | pública | muta (construct) | `nombre: Text`, `props: N`, `permisoRead: N`, `permisoWrite: N`, `tam: N` | — |
| `asignarPropiedadesPermisosYTamanyoDatos()` | pública | muta | `props: N`, `permR: N`, `permW: N`, `tam: N` | — |
| `escribirDatos()` | pública | muta | `texto: Text` | `N` (bytes escritos) |
| `notificarDatos()` | pública | muta | `texto: Text` | `N` (bytes notificados) |
| `instalarCallbackCaracteristicaEscrita()` | pública | muta | `cb: Callback` | — |
| `activar()` | pública | muta | — | — |

### Métodos privados (internos)

| Método | Estado | Entradas |
| :--- | :--- | :--- |
| `asignarPropiedades()` | muta | `props: N` |
| `asignarPermisos()` | muta | `permisoRead: N`, `permisoWrite: N` |
| `asignarTamanyoDatos()` | muta | `tam: N` |