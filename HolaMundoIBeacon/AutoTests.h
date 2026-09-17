// -*- mode: c++ -*-

// --------------------------------------------------------------
// AutoTests.h
// Pruebas automáticas (power-on self test) que se ejecutan AL ENCENDER
// la placa, dentro de setup(), y escriben el resultado por el puerto
// serie (Serial Monitor del Arduino IDE).
//
// Cada prueba sigue el ejemplo sencillo:
//
//   int resultado = sumar(2, 3);
//   if (resultado == 5) {
//     Serial.println("TEST OK");
//   } else {
//     Serial.println("TEST FALLIDO");
//   }
//
// Para ver los resultados: Arduino IDE -> Herramientas -> Monitor Serie
// --------------------------------------------------------------
#ifndef AUTO_TESTS_H_INCLUIDO
#define AUTO_TESTS_H_INCLUIDO

// --------------------------------------------------------------
// contadores para el resumen final
// --------------------------------------------------------------
static int contadorAciertosAutoTests = 0;
static int contadorFallosAutoTests = 0;

// --------------------------------------------------------------
// class AutoTests
// --------------------------------------------------------------
class AutoTests {

public:

  // .........................................................
  // ejecutarAutoTests():nueva->N
  // Ejecuta todas las pruebas y devuelve cuántas han fallado.
  // .........................................................
  static int ejecutarAutoTests() {

    contadorAciertosAutoTests = 0;
    contadorFallosAutoTests = 0;

    Globales::elPuerto.escribir( "\n==========================================\n" );
    Globales::elPuerto.escribir( "==== AUTO-TESTS: empieza (al encender) ====\n" );
    Globales::elPuerto.escribir( "==========================================\n" );

    testAlReves();
    testStringAUint8AlReves();
    testMedidor();
    testServicioEnEmisora();
    testEmisoraIBeacon();
    testEmisoraLibre();
    testPublicador();

    Globales::elPuerto.escribir( "============ resumen: " );
    Globales::elPuerto.escribir( contadorAciertosAutoTests );
    Globales::elPuerto.escribir( " aciertos, " );
    Globales::elPuerto.escribir( contadorFallosAutoTests );
    Globales::elPuerto.escribir( " fallos ============\n" );
    Globales::elPuerto.escribir( ( contadorFallosAutoTests == 0 )
                                   ? "============ TODO OK ============\n"
                                   : "============ HAY FALLOS ============\n" );

    return contadorFallosAutoTests;
  } // ()

private:

  // .........................................................
  // testAlReves()
  // .........................................................
  static void testAlReves() {
    int a[5] = { 1, 2, 3, 4, 5 };
    alReves( a, 5 );

    if ( a[0] == 5 && a[1] == 4 && a[2] == 3 && a[3] == 2 && a[4] == 1 ) {
      Serial.println("testAlReves(): TEST OK");
      contadorAciertosAutoTests++;
    } else {
      Serial.println("testAlReves(): TEST FALLIDO");
      contadorFallosAutoTests++;
    }
  } // ()

  // .........................................................
  // testStringAUint8AlReves()
  // .........................................................
  static void testStringAUint8AlReves() {
    uint8_t buf[16] = { 0 };
    const char * s = "hola";
    uint8_t * r = stringAUint8AlReves( s, &buf[0], 16 );

    bool ok = ( r == &buf[0] )
        && ( buf[15] == 'h' ) && ( buf[14] == 'o' )
        && ( buf[13] == 'l' ) && ( buf[12] == 'a' )
        && ( buf[11] == 0 ) && ( buf[0] == 0 );

    ok = ok && ( stringAUint8AlReves( nullptr, &buf[0], 16 ) == nullptr );
    ok = ok && ( stringAUint8AlReves( s, nullptr, 16 ) == nullptr );

    uint8_t buf2[16] = { 0xFF };
    stringAUint8AlReves( "0123456789ABCDEFG", &buf2[0], 16 );
    ok = ok && ( buf2[0] == 'F' ) && ( buf2[15] == '0' );

    if ( ok ) {
      Serial.println("testStringAUint8AlReves(): TEST OK");
      contadorAciertosAutoTests++;
    } else {
      Serial.println("testStringAUint8AlReves(): TEST FALLIDO");
      contadorFallosAutoTests++;
    }
  } // ()

  // .........................................................
  // testMedidor()
  // .........................................................
  static void testMedidor() {
    Medidor m;
    m.iniciarMedidor();
    int resultadoCO2 = m.medirCO2();
    int resultadoTemperatura = m.medirTemperatura();

    if ( resultadoCO2 == 1234 && resultadoTemperatura == -12 ) {
      Serial.println("testMedidor(): TEST OK");
      contadorAciertosAutoTests++;
    } else {
      Serial.println("testMedidor(): TEST FALLIDO");
      contadorFallosAutoTests++;
    }
  } // ()

  // .........................................................
  // testServicioEnEmisora()
  // .........................................................
  static void testServicioEnEmisora() {
    ServicioEnEmisora servicio( "SERV01" );
    ServicioEnEmisora::Caracteristica carta( "CARA01" );
    servicio.anyadirCaracteristica( carta );

    if ( servicio.activarServicio() ) {
      Serial.println("testServicioEnEmisora(): TEST OK");
      contadorAciertosAutoTests++;
    } else {
      Serial.println("testServicioEnEmisora(): TEST FALLIDO");
      contadorFallosAutoTests++;
    }
  } // ()

  // .........................................................
  // testEmisoraIBeacon()
  // Emite un iBeacon REAL y comprueba que empieza y para.
  // .........................................................
  static void testEmisoraIBeacon() {
    EmisoraBLE e( "GTI-3A", 0x004c, 4 );
    uint8_t uuid[16] = {
      'E', 'P', 'S', 'G', '-', 'G', 'T', 'I',
      '-', 'P', 'R', 'O', 'Y', '-', '3', 'A' };

    e.emitirAnuncioIBeacon( uuid, (11 << 8) + 5, 1234, -53 );
    esperar( 100 );
    bool empieza = e.estaAnunciando(); // debe estar anunciando
    e.detenerAnuncio();
    bool para = ( ! e.estaAnunciando() ); // y debe parar

    if ( empieza && para ) {
      Serial.println("testEmisoraIBeacon(): TEST OK");
      contadorAciertosAutoTests++;
    } else {
      Serial.println("testEmisoraIBeacon(): TEST FALLIDO");
      contadorFallosAutoTests++;
    }
  } // ()

  // .........................................................
  // testEmisoraLibre()
  // Emite un anuncio con carga libre y comprueba el guard (sin crash).
  // .........................................................
  static void testEmisoraLibre() {
    EmisoraBLE e( "GTI-3A", 0x004c, 4 );

    e.emitirAnuncioIBeaconLibre( "MolaMolaMolaMolaMolaM", 21 );
    esperar( 100 );
    bool empieza = e.estaAnunciando(); // debe estar anunciando
    e.detenerAnuncio();
    bool para = ( ! e.estaAnunciando() );

    e.emitirAnuncioIBeaconLibre( nullptr, 0 ); // guard: no debe crashear
    esperar( 100 );
    bool guardOk = ( ! e.estaAnunciando() );
    e.detenerAnuncio();

    if ( empieza && para && guardOk ) {
      Serial.println("testEmisoraLibre(): TEST OK");
      contadorAciertosAutoTests++;
    } else {
      Serial.println("testEmisoraLibre(): TEST FALLIDO");
      contadorFallosAutoTests++;
    }
  } // ()

  // .........................................................
  // testPublicador()
  // publicarCO2()/publicarTemperatura() emiten internamente y
  // deben quedar parados (anuncio limpio) al terminar.
  // .........................................................
  static void testPublicador() {
    Publicador p;

    p.publicarCO2( 1234, 5, 0 );
    bool paradoCO2 = ( ! p.laEmisora.estaAnunciando() );

    p.publicarTemperatura( -12, 3, 0 );
    bool paradoTemperatura = ( ! p.laEmisora.estaAnunciando() );

    p.laEmisora.detenerAnuncio();

    if ( paradoCO2 && paradoTemperatura ) {
      Serial.println("testPublicador(): TEST OK");
      contadorAciertosAutoTests++;
    } else {
      Serial.println("testPublicador(): TEST FALLIDO");
      contadorFallosAutoTests++;
    }
  } // ()

}; // class

#endif