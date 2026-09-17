// -------------------------------------------------------------------
// ux/logicaFake/diHola.js
// Lógica "fake" que ejecuta el navegador (JavaScript).
//   Es el espejo que usa la parte de interfaz (HTML) para no conocer el
//   lado del servidor: la pantalla llama a diHola(cb) y esta función hace
//   la llamada remota (AJAX/XMLHttpRequest) al endpoint REST
//   rest/diHola.php. Cuando llega la respuesta, la entrega mediante
//   callback(err, resultado).
//   En un futuro, esta "fake" puede convertirse en la llamada real o
//   sustituirse por un fetch() moderno.
// -------------------------------------------------------------------
// ---------------------------------------------------
//
// versión fake de una función de la lógica
//
// usuario:Texto -> diHola() -> (nombre:Texto, saludo:Texto) | error:Texto
//
// usuario: se enviará de forma implícita en la sesión
// (nombre:Texto, saludo:Texto) | error:Texto : devuelto via callback( err, res )
//
// ---------------------------------------------------
function diHola( cb ) {

	// preparar la llamada remota
	var xmlhttp = new XMLHttpRequest()
	xmlhttp.onreadystatechange = function() {
		// callback para cuando llegue la respuesta
		// de la petición que haremos más abajo

		if( this.readyState == 4 ){

			// ANTES->DESPUÉS (B6): antes solo se atendía status==200; con
			// 401 (no acreditado) la interfaz se quedaba esperando.
			// MOTIVO: avisar siempre del fallo vía callback(err).
			if ( this.status != 200 ){
				cb( "error HTTP " + this.status, null )
				return
			}

			// ANTES->DESPUÉS (B5): JSON.parse() podía lanzar excepción con una
			// respuesta no JSON (p.ej. un warning de PHP).
			// MOTIVO: no romper la interfaz ante una respuesta inválida.
			var resultado
			try {
				resultado = JSON.parse( this.responseText )
			} catch ( e ) {
				cb( "respuesta JSON inválida", null )
				return
			}

			console.log( "recibo: " + this.responseText )

			if ( resultado.error != 0 ) {
				cb( resultado.error, null )
				return
			}

			// no hay error, devuelvo el resultado
			cb( null, resultado ) 
		}
	}
	
	// llamamos *remotamente* al fichero diHola.php
	// (la verdadera función de la lógica)
	xmlhttp.open("GET", "../rest/diHola.php", true)
	xmlhttp.send()

} // ()