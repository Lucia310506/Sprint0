// -*- mode: c++ -*-

// --------------------------------------------------------------
// AutoTests.h
// Pruebas automáticas (power-on self test) que se ejecutan AL ENCENDER
// la placa, dentro de setup(), y escriben el resultado por el puerto
// serie (Serial Monitor del Arduino IDE).
//
// CÓMO FUNCIONA (resumen)
// -----------------------
// 1. setup() llama una sola vez a AutoTests::ejecutarAutoTests().
// 2. Esa función llama, en orden, a cada testXxx().
// 3. Cada testXxx():
//      - prepara los datos / objetos que quiere probar,
//      - llama al código real del proyecto,
//      - compara el resultado con el valor esperado,
//      - escribe "TEST OK" o "TEST FALLIDO" por el puerto serie
//        y suma 1 al contador de aciertos o de fallos.
// 4. Al final se imprime un resumen y "TODO OK" / "HAY FALLOS".
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
// IMPORTANTE: esto no sustituye a los tests del PC; aquí se ejecuta en
// el hardware real, así que los tests de emisora comprueban además que
// el Bluetooth de la placa arranca y para de verdad.
//
// Para ver los resultados: Arduino IDE -> Herramientas -> Monitor Serie
// --------------------------------------------------------------
#ifndef AUTO_TESTS_H_INCLUIDO
#define AUTO_TESTS_H_INCLUIDO

// --------------------------------------------------------------
// Contadores globales para el resumen final.
// Los incrementa cada testXxx() según pase o falle.
// Son "static" para que este header tenga su propia copia y no choque
// con otros archivos que se incluyan en el mismo .ino.
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
  //
  // Es el único método público: lo llama setup() al encender.
  // Pone los contadores a 0, llama a cada test y escribe el resumen.
  // Devuelve 0 si todo ha ido bien (útil para comprobarlo en el PC).
  // .........................................................
  static int ejecutarAutoTests() {

    // partimos de cero en cada ejecución
    contadorAciertosAutoTests = 0;
    contadorFallosAutoTests = 0;

    Globales::elPuerto.escribir( "\n==========================================\n" );
    Globales::elPuerto.escribir( "==== AUTO-TESTS: empieza (al encender) ====\n" );
    Globales::elPuerto.escribir( "==========================================\n" );

    // aquí se van ejecutando todas las pruebas, una detrás de otra
    testAlReves();
    testStringAUint8AlReves();
    testMedidor();
    testServicioEnEmisora();
    testEmisoraIBeacon();
    testEmisoraLibre();
    testPublicador();

    // resumen: cuántos OK y cuántos fallos ha habido
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
  // Comprueba la utilidad alReves() de ServicioEnEmisora.h:
  //   alReves(array, n) da la vuelta al array en el sitio.
  // Preparamos {1,2,3,4,5}, llamamos y esperamos {5,4,3,2,1}.
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
  // Comprueba stringAUint8AlReves(), que copia un texto a un bloque
  // de bytes "al revés" (lo usan los UUID de BLE). Se comprueban
  // tres cosas:
  //   1) el texto "hola" queda invertido al final del bloque de 16:
  //      buf[15]='h', buf[14]='o', ... buf[12]='a';
  //   2) con punteros nulos (nullptr) devuelve nullptr y NO crashea;
  //   3) si el texto es más largo que el bloque, solo copia los
  //      primeros 16 caracteres (no se sale del array).
  // Todas las condiciones se acumulan en "ok" para imprimir un solo
  // TEST OK / TEST FALLIDO.
  // .........................................................
  static void testStringAUint8AlReves() {
    uint8_t buf[16] = { 0 };
    const char * s = "hola";
    uint8_t * r = stringAUint8AlReves( s, &buf[0], 16 );

    // 1) copia normal "al revés"
    bool ok = ( r == &buf[0] )
        && ( buf[15] == 'h' ) && ( buf[14] == 'o' )
        && ( buf[13] == 'l' ) && ( buf[12] == 'a' )
        && ( buf[11] == 0 ) && ( buf[0] == 0 );

    // 2) punteros nulos: debe devolver nullptr sin romper nada
    ok = ok && ( stringAUint8AlReves( nullptr, &buf[0], 16 ) == nullptr );
    ok = ok && ( stringAUint8AlReves( s, nullptr, 16 ) == nullptr );

    // 3) texto más largo de la cuenta: solo copia 16 chars
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
  // El Medidor devuelve valores simulados fijos. Comprobamos que
  // medirCO2() da 1234 y medirTemperatura() da -12, que son los
  // que la app verá en el beacon.
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
  // Crea un servicio BLE con una característica, las añade y llama
  // a activarServicio(). Debe devolver true (los begin() han ido bien
  // porque los fakes del PC y el hardware lo permiten).
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
  // Emite un iBeacon REAL y comprueba que la emisora arranca y para:
  //   - tras emitirAnuncioIBeacon() + una pequeña espera,
  //     estaAnunciando() debe ser true;
  //   - tras detenerAnuncio(), estaAnunciando() debe ser false.
  // Esto valida de verdad el Bluetooth de la placa.
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
  // Igual que el anterior pero con emitirAnuncioIBeaconLibre(), que
  // manda una carga libre de 21 bytes. Además comprueba el "guard":
  // llamar con carga nula (nullptr, 0) no debe crashear ni arrancar
  // un anuncio (devuelve '-------' por defecto).
  // .........................................................
  static void testEmisoraLibre() {
    EmisoraBLE e( "GTI-3A", 0x004c, 4 );

    e.emitirAnuncioIBeaconLibre( "MolaMolaMolaMolaMolaM", 21 );
    esperar( 100 );
    bool empieza = e.estaAnunciando(); // debe estar anunciando
    e.detenerAnuncio();
    bool para = ( ! e.estaAnunciando() );

    // caso límite: carga nula (comprueba que el guard funciona)
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
  // Publicador orquesta el anuncio con las mediciones. Al llamar a
  // publicarCO2() y publicarTemperatura(), internamente emite el
  // beacon, espera y lo detiene. Comprobamos que al volver el
  // Publicador ha dejado la emisora parada (estado limpio).
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