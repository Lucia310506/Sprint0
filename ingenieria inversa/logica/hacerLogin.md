# Diseño Lógico — Logica hacerLogin

**Origen:** `EsqueletoWebAppEnPHPConSesion/.../logica/hacerLogin.php`
**Responsabilidad:** Verificar si unas credenciales son válidas (lógica de negocio pura).

---

## Función

```
nombre: Text, password: Text --> hacerLogin() --> B
     (true si password == "1234")
```

---

## Comportamiento

| password | resultado |
| :--- | :--- |
| `"1234"` | `true` |
| cualquier otro | `false` |

---

## Pendiente (diseño final)

- Conectar a base de datos para verificar usuario/contraseña reales.