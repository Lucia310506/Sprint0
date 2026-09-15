# Diseño Lógico — Utilidades

**Origen:** `app/src/main/java/com/example/ldiamur/btlealumnos2021app/Utilidades.java`
**Responsabilidad:** Funciones estáticas de conversión entre bytes, strings, UUIDs y representaciones hexadecimales. Sin estado (clase estática, instanciable).

---

## Funciones

```
       texto: Text --> stringToBytes() --> [Z]
  uuidTexto: Text   --> stringToUUID()  --> UUID       (debe tener 16 chars exactos)
         u: UUID    --> uuidToString()  --> Text       (16 caracteres legibles)
         u: UUID    --> uuidToHexString() --> Text     ("aa:bb:cc:...")
      bytes: [Z]    --> bytesToString()  --> Text     (cada byte → char)
  masSig: N,        --> dosLongToBytes() --> [Z]      (dos longs → 16 bytes)
  menosSig: N
      bytes: [Z]    --> bytesToInt()    --> Z          (via BigInteger, signo natural)
      bytes: [Z]    --> bytesToLong()   --> N          (via BigInteger)
      bytes: [Z]    --> bytesToIntOK()  --> Z          (desplazamientos + complemento a 2)
      bytes: [Z]    --> bytesToHexString() --> Text   ("aa:bb:cc:...")
```

---

## Detalle de las funciones clave

### `bytesToInt(bytes)`

Convierte un array de bytes en entero usando `BigInteger`. Maneja arrays de cualquier longitud.

### `bytesToIntOK(bytes)`

Versión manual con desplazamientos a la izquierda (`res = (res << 8) + (b & 0xFF)`). Si el byte más significativo tiene signo negativo (`0x8`), aplica complemento a 2. `throws Error` si hay más de 4 bytes.

### `dosLongToBytes(masSignificativos, menosSignificativos)`

Junta dos `long` (128 bits) en un buffer de 16 bytes usando `ByteBuffer.allocate`.

### `stringToUUID(uuid)`

Divide la cadena en dos mitades de 8 chars cada una, convierte cada una a `long` vía `bytesToLong`, y crea `new UUID(mostSig, leastSig)`. `throws Error` si no tiene exactamente 16 caracteres.

---

## Nota sobre `Utilidades.bytesToInt` vs `Utilidades.bytesToIntOK`

Ambas realizan la misma operación conceptual (bytes → entero con signo), pero con implementación diferente:

| Función | Implementación | Límite |
| :--- | :--- | :--- |
| `bytesToInt` | `BigInteger(bytes).intValue()` | Sin límite de longitud |
| `bytesToIntOK` | Desplazamientos manuales + C2 | ≤ 4 bytes |