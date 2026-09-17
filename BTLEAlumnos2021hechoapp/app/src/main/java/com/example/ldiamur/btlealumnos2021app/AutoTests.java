
package com.example.ldiamur.btlealumnos2021app;

import android.util.Log;

import java.util.UUID;

// -----------------------------------------------------------------------------------
// class AutoTests
// Pruebas automáticas (power-on self test) que se ejecutan AL ARRANCAR la app,
// dentro de MainActivity.onCreate(), y escriben el resultado en logcat
// (Android Studio -> pestaña Logcat, filtrando por ">>>>" ).
//
// CÓMO FUNCIONA (resumen)
// -----------------------
// 1. onCreate() llama una sola vez a AutoTests.ejecutarAutoTests().
// 2. Esa función llama, en orden, a cada testXxx().
// 3. Cada testXxx():
//      - prepara los datos que quiere probar,
//      - llama al código real de la app (Utilidades, TramaIBeacon),
//      - compara el resultado con el valor esperado,
//      - escribe "testXxx(): TEST OK" o "testXxx(): TEST FALLIDO" en logcat
//        y suma 1 al contador de aciertos o de fallos.
// 4. Al final se imprime un resumen y "TODO OK" / "HAY FALLOS".
//
// Cada prueba sigue el ejemplo sencillo:
//
//   int resultado = sumar(2, 3);
//   if (resultado == 5) {
//     Log.d(ETIQUETA_LOG, "TEST OK");
//   } else {
//     Log.d(ETIQUETA_LOG, "TEST FALLIDO");
//   }
//
// Nota: igual que en la parte Arduino, no hace falta hardware: estas pruebas
//       comprueban la lógica (conversiones de bytes, UUID y parseo de iBeacon).
// -----------------------------------------------------------------------------------
public class AutoTests {

    private static final String ETIQUETA_LOG = ">>>>";

    // Contadores globales para el resumen final.
    // Los incrementa cada testXxx() según pase o falle.
    private static int contadorAciertos = 0;
    private static int contadorFallos = 0;

    // -------------------------------------------------------------------------------
    // ejecutarAutoTests():nueva->N
    // Ejecuta todas las pruebas y devuelve cuántas han fallado.
    //
    // Es el único método público: lo llama MainActivity.onCreate() al arrancar.
    // Pone los contadores a 0, llama a cada test y escribe el resumen.
    // Devuelve 0 si todo ha ido bien.
    // -------------------------------------------------------------------------------
    public static int ejecutarAutoTests() {

        // partimos de cero en cada ejecución
        contadorAciertos = 0;
        contadorFallos = 0;

        Log.d(ETIQUETA_LOG, "==========================================");
        Log.d(ETIQUETA_LOG, "==== AUTO-TESTS: empieza (al arrancar) ====");
        Log.d(ETIQUETA_LOG, "==========================================");

        // aquí se van ejecutando todas las pruebas, una detrás de otra
        testStringToUUID();
        testBytesToInt();
        testTramaIBeacon();
        testTramaIBeaconCorta();
        testEsIBeaconFalso();

        // resumen: cuántos OK y cuántos fallos ha habido
        Log.d(ETIQUETA_LOG, "============ resumen: " + contadorAciertos
                + " aciertos, " + contadorFallos + " fallos ============");
        Log.d(ETIQUETA_LOG, (contadorFallos == 0)
                ? "============ TODO OK ============"
                : "============ HAY FALLOS ============");

        return contadorFallos;
    } // ()

    // -------------------------------------------------------------------------------
    // testStringToUUID()
    // Comprueba que convertir un texto a UUID y volver a texto da lo mismo
    // (la app usa esto para el uuid "EPSG-GTI-PROY-3A" del beacon).
    //   - stringToUUID("EPSG-GTI-PROY-3A") -> UUID
    //   - uuidToString(UUID) -> "EPSG-GTI-PROY-3A"
    // -------------------------------------------------------------------------------
    private static void testStringToUUID() {
        String original = "EPSG-GTI-PROY-3A";
        UUID id = Utilidades.stringToUUID(original);
        String vuelta = Utilidades.uuidToString(id);

        if (original.equals(vuelta)) {
            Log.d(ETIQUETA_LOG, "testStringToUUID(): TEST OK");
            contadorAciertos++;
        } else {
            Log.d(ETIQUETA_LOG, "testStringToUUID(): TEST FALLIDO (era " + original + " y volvió " + vuelta + ")");
            contadorFallos++;
        }
    } // ()

    // -------------------------------------------------------------------------------
    // testBytesToInt()
    // Comprueba bytesToIntOK(), que pasa bytes a entero respetando el signo:
    //   - {0x04, 0xD2}  -> 1234 (major de ejemplo)
    //   - {0xCB}        -> -53  (txPower negativo; con signo)
    // -------------------------------------------------------------------------------
    private static void testBytesToInt() {
        int positivo = Utilidades.bytesToIntOK(new byte[]{ 0x04, (byte) 0xD2 });
        int negativo = Utilidades.bytesToIntOK(new byte[]{ (byte) 0xCB });

        if (positivo == 1234 && negativo == -53) {
            Log.d(ETIQUETA_LOG, "testBytesToInt(): TEST OK");
            contadorAciertos++;
        } else {
            Log.d(ETIQUETA_LOG, "testBytesToInt(): TEST FALLIDO (positivo=" + positivo + ", negativo=" + negativo + ")");
            contadorFallos++;
        }
    } // ()

    // -------------------------------------------------------------------------------
    // testTramaIBeacon()
    // Construye una trama iBeacon de 30 bytes como la que emite la placa y
    // comprueba que TramaIBeacon la parsea bien:
    //   - esIBeacon() == true (companyID 0x004C, tipo 0x02, longitud 0x15)
    //   - uuid == "EPSG-GTI-PROY-3A"
    //   - major == 2821 ((CO2 << 8) + 5) y minor == 1234
    //   - txPower == -53
    // -------------------------------------------------------------------------------
    private static void testTramaIBeacon() {
        byte[] trama = tramaIBeaconDePrueba(0x4C, 0x00, 0x02, 0x15);
        TramaIBeacon tib = new TramaIBeacon(trama);

        String uuid = Utilidades.bytesToString(tib.getUUID());
        int major = Utilidades.bytesToIntOK(tib.getMajor());
        int minor = Utilidades.bytesToIntOK(tib.getMinor());

        boolean ok = tib.esIBeacon()
                && uuid.equals("EPSG-GTI-PROY-3A")
                && major == 2821
                && minor == 1234
                && tib.getTxPower() == (byte) 0xCB;

        if (ok) {
            Log.d(ETIQUETA_LOG, "testTramaIBeacon(): TEST OK");
            contadorAciertos++;
        } else {
            Log.d(ETIQUETA_LOG, "testTramaIBeacon(): TEST FALLIDO (uuid=" + uuid
                    + ", major=" + major + ", minor=" + minor
                    + ", txPower=" + tib.getTxPower() + ")");
            contadorFallos++;
        }
    } // ()

    // -------------------------------------------------------------------------------
    // testTramaIBeaconCorta()
    // Una trama con menos de 30 bytes (o null) no es un iBeacon: el constructor
    // debe lanzar IllegalArgumentException y NO dejar que el programa pete.
    // -------------------------------------------------------------------------------
    private static void testTramaIBeaconCorta() {
        boolean lanzoConCorta = false;
        boolean lanzoConNull = false;

        try {
            new TramaIBeacon(new byte[]{ 1, 2, 3 });
        } catch (IllegalArgumentException ex) {
            lanzoConCorta = true;
        }

        try {
            new TramaIBeacon(null);
        } catch (IllegalArgumentException ex) {
            lanzoConNull = true;
        }

        if (lanzoConCorta && lanzoConNull) {
            Log.d(ETIQUETA_LOG, "testTramaIBeaconCorta(): TEST OK");
            contadorAciertos++;
        } else {
            Log.d(ETIQUETA_LOG, "testTramaIBeaconCorta(): TEST FALLIDO (corta=" + lanzoConCorta + ", null=" + lanzoConNull + ")");
            contadorFallos++;
        }
    } // ()

    // -------------------------------------------------------------------------------
    // testEsIBeaconFalso()
    // Una trama con un companyID que NO es Apple (0x004C) no debe considerarse
    // iBeacon, aunque tenga la misma estructura.
    // -------------------------------------------------------------------------------
    private static void testEsIBeaconFalso() {
        byte[] trama = tramaIBeaconDePrueba(0x11, 0x22, 0x02, 0x15); // companyID no Apple
        TramaIBeacon tib = new TramaIBeacon(trama);

        if (!tib.esIBeacon()) {
            Log.d(ETIQUETA_LOG, "testEsIBeaconFalso(): TEST OK");
            contadorAciertos++;
        } else {
            Log.d(ETIQUETA_LOG, "testEsIBeaconFalso(): TEST FALLIDO");
            contadorFallos++;
        }
    } // ()

    // -------------------------------------------------------------------------------
    // tramaIBeaconDePrueba(...)->[byte]
    // Crea una trama iBeacon de 30 bytes con la estructura que espera TramaIBeacon:
    //   9 bytes de prefijo (advFlags, advHeader, companyID, tipo, longitud)
    //   + 16 de uuid ("EPSG-GTI-PROY-3A") + 2 de major (0x0B05=2821)
    //   + 2 de minor (0x04D2=1234) + 1 de txPower (0xCB=-53)
    // Los 4 parámetros permiten cambiar el companyID, el tipo y la longitud para
    // probar los casos "falso iBeacon".
    // -------------------------------------------------------------------------------
    private static byte[] tramaIBeaconDePrueba(int companyID0, int companyID1,
                                               int tipo, int longitud) {
        byte[] uuid = Utilidades.stringToBytes("EPSG-GTI-PROY-3A");
        byte[] t = new byte[30];

        t[0] = 0x02; t[1] = 0x01; t[2] = 0x06;           // advFlags
        t[3] = 0x1A; t[4] = (byte) 0xFF;                  // advHeader
        t[5] = (byte) companyID0; t[6] = (byte) companyID1; // companyID
        t[7] = (byte) tipo; t[8] = (byte) longitud;       // iBeacon type + length

        System.arraycopy(uuid, 0, t, 9, 16);              // uuid (16 bytes)

        t[25] = 0x0B; t[26] = 0x05;                       // major = 2821
        t[27] = 0x04; t[28] = (byte) 0xD2;                // minor = 1234
        t[29] = (byte) 0xCB;                              // txPower = -53

        return t;
    } // ()

} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
