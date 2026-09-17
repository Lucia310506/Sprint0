// -*- mode: c++ -*-

// --------------------------------------------------------------
// Jordi Bataller i Mascarell
// --------------------------------------------------------------

#ifndef PUBLICADOR_H_INCLUIDO
#define PUBLICADOR_H_INCLUIDO

// --------------------------------------------------------------
// class Publicador
// Orquesta el anuncio iBeacon con las mediciones:
//   guarda el UUID y la configuración de la emisora (nombre, txPower, RSSI),
//   define los tipos de medición (MedicionesID) y publica cada medida
//   como un iBeacon donde:
//     major = (tipo de medición << 8) + contador
//     minor = valor medido
//   publicarCO2()/publicarTemperatura() emiten el anuncio, esperan y lo paran.
// --------------------------------------------------------------

// --------------------------------------------------------------
// --------------------------------------------------------------
class Publicador {

  // ............................................................
  // ............................................................
private:
//Mensaje que se envia
// ******************************************
//  SE MODIFICA: el UUID del beacon (16 bytes = 16 caracteres)
//  La app lo muestra en logcat como "uuid = EPSG-GTI-PROY-3A"
// ******************************************
  uint8_t beaconUUID[16] = { 
	'E', 'P', 'S', 'G', '-', 'G', 'T', 'I', 
	'-', 'P', 'R', 'O', 'Y', '-', '3', 'A'
	};
  // ............................................................
  // ............................................................
public:
//Beacon que se envía.
// ******************************************
// 
//  SE MODIFICA: los datos de identificación de la emisora
// 
// ******************************************
  EmisoraBLE laEmisora {
	"GTI-3A", // nombre que la app usa para filtrar
	  0x004c, // fabricanteID (NO tocar: es Apple, los iBeacon lo exigen)
	  4 // 			 txPower (potencia de transmisión en dBm)
	  };
  
  const int RSSI = -53; // por poner algo, de momento no lo uso

  // ............................................................
  // ............................................................
public:

  // ............................................................
  // ............................................................
  enum MedicionesID  {
	CO2 = 11,
	TEMPERATURA = 12,
	RUIDO = 13
  };

  // ............................................................
	// Publicador()
  // ............................................................
  Publicador( ) {
	// ATENCION: no hacerlo aquí. (*this).laEmisora.encenderEmisora();
	// Pondremos un método para llamarlo desde el setup() más tarde
  } // ()

  // ............................................................
	// encenderEmisora()<-Clase(Consulta)
  // ............................................................
  void encenderEmisora() {
	(*this).laEmisora.encenderEmisora();
  } // ()

  // ............................................................
	// valorCO2:N(Antes era Z), contador:N, tiempoEspera:N->publicarCO2()<- Clase(Consulta)
	//																																	 ->Clase(Modificar)
  // ............................................................
  void publicarCO2( uint16_t valorCO2, uint8_t contador,
					long tiempoEspera ) {

	//
	// 1. empezamos anuncio
	//
	// ******************************************
	//  SE MODIFICA: los valores que viajan en el beacon
	//    major = (tipo de medición << 8) + contador
	//            MedicionesID::CO2 = 11  (ver enum más abajo)
	//    minor = valorCO2 (lo que devuelve elMedidor.medirCO2())
	//  La app los lee en logcat como "major" y "minor".
	// ******************************************
	uint16_t major = (MedicionesID::CO2 << 8) + contador;
	(*this).laEmisora.emitirAnuncioIBeacon( (*this).beaconUUID, 
											major,
											valorCO2, // minor
											(*this).RSSI // rssi
									);

	/*
	Globales::elPuerto.escribir( "   publicarCO2(): valor=" );
	Globales::elPuerto.escribir( valorCO2 );
	Globales::elPuerto.escribir( "   contador=" );
	Globales::elPuerto.escribir( contador );
	Globales::elPuerto.escribir( "   todo="  );
	Globales::elPuerto.escribir( major );
	Globales::elPuerto.escribir( "\n" );
	*/

	//
	// 2. esperamos el tiempo que nos digan
	//
	esperar( tiempoEspera );

	//
	// 3. paramos anuncio
	//
	(*this).laEmisora.detenerAnuncio();
  } // ()

  // ............................................................
	// valorTemperatura:Z, contador:N, tiempoEspera:N->publicarTemperatura()<- Clase(Consulta)
	//																																	    ->Clase(Modificar)
  // ............................................................
  void publicarTemperatura( int16_t valorTemperatura,
							uint8_t contador, long tiempoEspera ) {

	// ******************************************
	//  SE MODIFICA: igual que en publicarCO2()
	//    major = (MedicionesID::TEMPERATURA << 8) + contador
	//    minor = valorTemperatura
	//  MedicionesID::TEMPERATURA = 12  (ver enum más abajo)
	// ******************************************
	uint16_t major = (MedicionesID::TEMPERATURA << 8) + contador;
	(*this).laEmisora.emitirAnuncioIBeacon( (*this).beaconUUID, 
											major,
											valorTemperatura, // minor
											(*this).RSSI // rssi
									);
	esperar( tiempoEspera );

	(*this).laEmisora.detenerAnuncio();
  } // ()
	
}; // class

// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
#endif
