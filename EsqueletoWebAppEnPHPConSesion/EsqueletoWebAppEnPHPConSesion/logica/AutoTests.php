<?php

// -----------------------------------------------------------------------------------
// logica/AutoTests.php
// Pruebas automáticas de la capa de LÓGICA del servidor web.
//   Ejecuta y comprueba las funciones "de verdad" del negocio
//   (logica/hacerLogin.php y logica/diHola.php) sin HTTP, sesiones ni JSON.
//   Es el equivalente web de AutoTests.h (Arduino) y AutoTests.java (Android).
//
// CÓMO SE USA
// -----------
//   Abrir en el navegador:  .../logica/AutoTests.php
//   o, si hay PHP de línea de comandos:  php logica/AutoTests.php
//   La salida es texto plano con "TEST OK" / "TEST FALLIDO" y un resumen final.
//
// CÓMO FUNCIONA
// -------------
//   1. ejecutarAutoTests() pone los contadores a 0 y llama a cada testXxx().
//   2. Cada testXxx():
//        - llama a la función real (hacerLogin, diHola),
//        - compara el resultado con lo esperado,
//        - imprime "testXxx(): TEST OK" o "testXxx(): TEST FALLIDO"
//          y suma 1 al contador de aciertos o de fallos.
//   3. Al final se imprime el resumen y "TODO OK" / "HAY FALLOS".
//
//   El patrón de cada prueba es el sencillo de siempre:
//
//     $resultado = hacerLogin("Mickey", "1234");
//     if ( $resultado == true ) {
//       echo "TEST OK";
//     } else {
//       echo "TEST FALLIDO";
//     }
// -----------------------------------------------------------------------------------

header('Content-Type: text/plain; charset=utf-8');

// las funciones que vamos a probar (la lógica de verdad)
require_once( __DIR__ . '/hacerLogin.php' );
require_once( __DIR__ . '/diHola.php' );

class AutoTests {

    // contadores globales para el resumen final
    private static $contadorAciertos = 0;
    private static $contadorFallos = 0;

    // -------------------------------------------------------------------------------
    // ejecutarAutoTests()
    // Ejecuta todas las pruebas y devuelve cuántas han fallado (0 = todo bien).
    // -------------------------------------------------------------------------------
    public static function ejecutarAutoTests() {

        self::$contadorAciertos = 0;
        self::$contadorFallos = 0;

        echo "==========================================\n";
        echo "==== AUTO-TESTS WEB: empieza =============\n";
        echo "==========================================\n";

        // aquí se van ejecutando todas las pruebas, una detrás de otra
        self::testHacerLogin();
        self::testDiHola();

        echo "============ resumen: " . self::$contadorAciertos
            . " aciertos, " . self::$contadorFallos . " fallos ============\n";
        echo ( self::$contadorFallos == 0 )
            ? "============ TODO OK ============\n"
            : "============ HAY FALLOS ============\n";

        return self::$contadorFallos;
    } // ()

    // -------------------------------------------------------------------------------
    // testHacerLogin()
    // Comprueba la regla de negocio del login (usuario "Mickey" + password "1234"):
    //   - Mickey / 1234   -> true
    //   - Mickey / xxxx   -> false (password incorrecto)
    //   - Donald / 1234   -> false (usuario desconocido)
    //   - ""     / ""     -> false (vacíos)
    // -------------------------------------------------------------------------------
    private static function testHacerLogin() {
        $ok = ( hacerLogin( "Mickey", "1234" ) === true )
           && ( hacerLogin( "Mickey", "xxxx" ) === false )
           && ( hacerLogin( "Donald", "1234" ) === false )
           && ( hacerLogin( "", "" )       === false );

        if ( $ok ) {
            echo "testHacerLogin(): TEST OK\n";
            self::$contadorAciertos++;
        } else {
            echo "testHacerLogin(): TEST FALLIDO\n";
            self::$contadorFallos++;
        }
    } // ()

    // -------------------------------------------------------------------------------
    // testDiHola()
    // Comprueba que diHola() devuelve el nombre recibido y el saludo fijo:
    //   diHola("Mickey") -> nombre == "Mickey" y saludo == "That's all folks"
    // -------------------------------------------------------------------------------
    private static function testDiHola() {
        $res = diHola( "Mickey" );

        $ok = ( $res->nombre  === "Mickey" )
           && ( $res->saludo  === "That's all folks" );

        if ( $ok ) {
            echo "testDiHola(): TEST OK\n";
            self::$contadorAciertos++;
        } else {
            echo "testDiHola(): TEST FALLIDO\n";
            self::$contadorFallos++;
        }
    } // ()

} // class

// lanzamos las pruebas al abrir este fichero
AutoTests::ejecutarAutoTests();

?>
