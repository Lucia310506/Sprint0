# Diseño Lógico — REST hacerLogin

**Origen:** `EsqueletoWebAppEnPHPConSesion/.../rest/hacerLogin.php`
**Responsabilidad:** Endpoint REST que autentica un usuario, almacena la sesión y devuelve el resultado en JSON.

---

## Notación

```
ResultadoLogin = ( resultado: B, usuario: Text )
Sesion = ( usuario: Text | ∅ )          (variable $_SESSION)
```

---

## Contrato REST

```
GET ../rest/hacerLogin.php?nombre=Text&password=Text
```

### Respuesta JSON (éxito)

```json
{ "resultado": true, "usuario": "Mickey" }
```

### Respuesta JSON (fallo)

```json
{ "resultado": false }
```

---

## Flujo

```
1. session_start()
2. nombre  = $_GET["nombre"]
3. password = $_GET["password"]
4. si hacerLogin( nombre, password ) == true
     → $_SESSION["usuario"] = nombre
     → echo { resultado: true, usuario: nombre }
5. si no
     → session_destroy()
     → echo { resultado: false }
```

---

## Resumen

| Operación | Método | Entradas | Efecto | Salida |
| :--- | :--- | :--- | :--- | :--- |
| Login | GET | `nombre: Text`, `password: Text` | crea/destruye sesión | `ResultadoLogin` |