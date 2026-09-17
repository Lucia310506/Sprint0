
// -*- mode: c++ -*-

// ----------------------------------------------------------
// Jordi Bataller i Mascarell
// 2019-07-07
// ----------------------------------------------------------

#ifndef PUERTO_SERIE_H_INCLUIDO
#define PUERTO_SERIE_H_INCLUIDO

// ----------------------------------------------------------
// class PuertoSerie
//  Encapsula el puerto serie del Arduino/Bluefruit:
//   - el constructor arranca Serial a la velocidad (baudios) indicada
//   - esperarDisponible() bloquea hasta que el puerto está listo
//   - escribir(...) manda un mensaje por el puerto (para logs)
// ----------------------------------------------------------
class PuertoSerie  {

public:
  // .........................................................
  // baudios:N->PuertoSerie()->Clase(Modificar)
  // .........................................................
  PuertoSerie (long baudios) {
	Serial.begin( baudios );
	// mejor no poner esto aquí: while ( !Serial ) delay(10);   
  } // ()

  // .........................................................
  // esperarDisponible()
  // .........................................................
  void esperarDisponible() {

	while ( !Serial ) {
	  delay(10);   
	}

  } // ()

  // .........................................................
  // mensaje:cualquier tipo->escribir()
  // .........................................................
  template<typename T> //Una función que puede recibir un tipo de dato cualquiera. A ese tipo lo voy a llamar T."
  void escribir (T mensaje) {
	Serial.print( mensaje );
  } // ()
  
}; // class PuertoSerie

// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
// ----------------------------------------------------------
#endif
