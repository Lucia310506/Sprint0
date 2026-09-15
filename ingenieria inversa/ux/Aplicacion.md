# Diseño Lógico — Aplicacion.html (UI)

**Origen:** `EsqueletoWebAppEnPHPConSesion/.../ux/Aplicacion.html`
**Responsabilidad:** Capa de presentación: formulario de login y botón para invocar el saludo. Conecta la UI con las funciones de la lógica (versiones fake) vía callbacks.

---

## Estructura UI

```
Aplicacion.html
 ├─ input  id="nombre"    (valor por defecto "Mickey")
 ├─ input  id="password"  (password, valor por defecto "Mouse")
 ├─ botón  "Log in"        → onclick=loginPulsado()
 ├─ botón  "Llamar hola"   → onclick=botonPulsado()
 └─ <p id="salida">        (zona de resultados, inicial "Antes")
```

## Scripts cargados

```
<script src="logicaFake/diHola.js"></script>
<script src="logicaFake/hacerLogin.js"></script>
```

---

## Funciones de la UI

### `loginPulsado()`

```
nombre   = valor de #nombre
password = valor de #password
hacerLogin( nombre, password, cb )
   cb( res ):
     #salida.innerHTML = " login= " + res.resultado
     si res.resultado == true → alert("login ok")
     si no → alert("fallo en login (el password es: 1234)")
```

### `botonPulsado()`

```
diHola( cb )
   cb( err, res ):
     si err → alert("usuario no acreditado")
     si no  → #salida.innerHTML = res.nombre + " dice: " + res.saludo
```

---

## Resumen

| Función JS | Origen | Entradas | Salida |
| :--- | :--- | :--- | :--- |
| `loginPulsado()` | Aplicacion.html | formulario (nombre, password) | actualiza #salida |
| `botonPulsado()` | Aplicacion.html | — | actualiza #salida |
| `hacerLogin()` | logicaFake/hacerLogin.js | nombre, password, cb | cb |
| `diHola()` | logicaFake/diHola.js | cb | cb |

---

## Nota de arranque

Desde `00-Leeme.txt`:

```
php -S localhost:8080 -t .
# y navegar a http://localhost:8080/ux/Aplicacion.html
```