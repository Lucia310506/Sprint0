# Diseño Lógico — PeticionarioREST

**Origen:** `app/src/main/java/com/example/ldiamur/btlealumnos2021app/PeticionarioREST.java`
**Responsabilidad:** Cliente HTTP REST asíncrono (AsyncTask) que envía peticiones GET/POST con cuerpo JSON y recibe la respuesta vía callback.

---

## Notación

```
RespuestaREST = ( codigo: N, cuerpo: Text ) --> void   (callback)
```

---

## Clase

```
                 --------- PeticionarioREST ------------
                 |  (extends AsyncTask<Void,Void,Boolean>)
                 |
                 | elMetodo: Text          (GET / POST / PUT / ...)
                 | urlDestino: Text
                 | elCuerpo: Text | null
                 | laRespuesta: RespuestaREST
                 | codigoRespuesta: N
                 | cuerpoRespuesta: Text
                 |
                 |
                 --> PeticionarioREST() -->
                 |
                 |
  metodo: Text, --> hacerPeticionREST() -->          (guarda campos y lanza execute())
   url: Text,
cuerpo: Text,
respuesta: RespREST
                 |
                 |
  params: [void] --> doInBackground() <--            (hilo 2º plano: abre conexión,
              B  <--                                   escribe cuerpo si no GET,
                                                      lee respuesta línea a línea)
                 |
                 |
    comoFue: B  --> onPostExecute() -->              (override; invoca
                 |                                    laRespuesta.callback(codigo, cuerpo))
                 |
                 --------------------------------------
```

---

## Flujo interno de `doInBackground`

```
1.  url = new URL( urlDestino )
2.  conn = (HttpURLConnection) url.openConnection()
3.  conn.setRequestProperty("Content-Type", "application/json; charset-utf-8")
4.  conn.setRequestMethod( elMetodo )
5.  conn.setDoInput(true)
6.  si elMetodo != "GET" && elCuerpo != null:
      conn.setDoOutput(true)
      dos = new DataOutputStream( conn.getOutputStream() )
      dos.writeBytes( elCuerpo )   →  envío del JSON
      dos.flush(); dos.close()
7.  codigoRespuesta = conn.getResponseCode()
8.  is = conn.getInputStream()
9.  br = new BufferedReader( InputStreamReader(is) )
10. leer línea a línea → cuerpoRespuesta
11. conn.disconnect()
12. return true   (éxito)
    en caso de excepción → return false
```

---

## Resumen

| Método | Visibilidad | Estado | Entradas | Salida |
| :--- | :--- | :--- | :--- | :--- |
| `PeticionarioREST()` | pública | muta (construct) | — | — |
| `hacerPeticionREST()` | pública | muta | `metodo: Text`, `urlDestino: Text`, `cuerpo: Text`, `respuesta: RespuestaREST` | — |
| `doInBackground()` | protegida (override) | muta | `params: [void]` | `B` |
| `onPostExecute()` | protegida (override) | muta | `comoFue: B` | — |

---

## Nota importante

En el flujo actual de `MainActivity`, `PeticionarioREST` **no está invocado**. La clase está presente en el proyecto con permisos `INTERNET` en el manifest, preparada para enviar datos al backend REST, pero la conexión no está implementada todavía (gap del diseño).