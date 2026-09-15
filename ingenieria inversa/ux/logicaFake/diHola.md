# Diseño Lógico — LogicaFake diHola (JS)

**Origen:** `EsqueletoWebAppEnPHPConSesion/.../ux/logicaFake/diHola.js`
**Responsabilidad:** Función cliente (AJAX) que invoca el endpoint REST `diHola.php` y devuelve el resultado vía callback estilo `callback( err, res )`.

---

## Notación

```
RespuestaDiHola = ( nombre: Text, saludo: Text, error: Text | 0 )
```

---

## Función (estilo callback)

```
cb --> diHola() --> (via XHR)
   cb( err, res )
        - si RespuestaDiHola.error != 0 → cb( error, null )
        - si no → cb( null, RespuestaDiHola )
```

---

## Flujo

```
1. xmlhttp = new XMLHttpRequest()
2. onreadystatechange:
     si readyState == 4 y status == 200
       → resultado = JSON.parse( responseText )
       → si resultado.error != 0  → cb( resultado.error, null )
       → si no                    → cb( null, resultado )
3. xmlhttp.open("GET", "../rest/diHola.php", true)
4. xmlhttp.send()
```

---

## Resumen

| Función | Entradas | Salida (callback) |
| :--- | :--- | :--- |
| `diHola()` | `cb` | `cb( err, res )` |