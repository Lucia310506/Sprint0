# Diseño Lógico — LogicaFake hacerLogin (JS)

**Origen:** `EsqueletoWebAppEnPHPConSesion/.../ux/logicaFake/hacerLogin.js`
**Responsabilidad:** Función cliente (AJAX) que invoca el endpoint REST `hacerLogin.php` y devuelve el resultado vía callback. Es la "versión fake" de la lógica de negocio del lado del navegador.

---

## Notación

```
ResultadoLogin = ( resultado: B, usuario: Text )
```

---

## Función (estilo callback)

```
nombre: Text, password: Text, cb --> hacerLogin() --> (via XHR)
                                    cb( ResultadoLogin )
```

---

## Flujo

```
1. xmlhttp = new XMLHttpRequest()
2. onreadystatechange:
     si readyState == 4 y status == 200
       → resultado = JSON.parse( responseText )
       → cb( resultado )
3. xmlhttp.open("GET", "../rest/hacerLogin.php?nombre=...&password=...", true)
4. xmlhttp.send()
```

---

## Resumen

| Función | Entradas | Salida (callback) |
| :--- | :--- | :--- |
| `hacerLogin()` | `nombre: Text`, `password: Text`, `cb` | `cb( ResultadoLogin )` |