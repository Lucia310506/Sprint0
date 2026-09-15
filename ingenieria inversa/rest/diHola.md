# Diseño Lógico — REST diHola

**Origen:** `EsqueletoWebAppEnPHPConSesion/.../rest/diHola.php`
**Responsabilidad:** Endpoint REST protegido por sesión: devuelve un saludo si el usuario está acreditado, o un error si no.

---

## Notación

```
RespuestaDiHola = ( nombre: Text, saludo: Text, error: Text | 0 )
Sesion = ( usuario: Text | ∅ )          (variable $_SESSION)
```

---

## Contrato REST

```
GET ../rest/diHola.php
```

(El usuario se envía de forma implícita vía la sesión, no por parámetro.)

### Respuesta JSON (acreditado)

```json
{ "nombre": "Mickey", "saludo": "That's all folks", "error": 0 }
```

### Respuesta JSON (no acreditado)

```json
{ "error": "usuario no acreditado" }
```

---

## Flujo

```
1. session_start()
2. si NO existe $_SESSION["usuario"]
     → echo { error: "usuario no acreditado" }
     → return
3. usuario = $_SESSION["usuario"]
4. resultado = diHola( usuario )
5. resultado.error = 0
6. echo resultado  (nombre, saludo, error)
```

---

## Resumen

| Operación | Método | Entradas | Protección | Salida |
| :--- | :--- | :--- | :--- | :--- |
| Saludo | GET | — (via sesión) | requiere `$_SESSION["usuario"]` | `RespuestaDiHola` |