// -------------------------------------------------------------------
// ux/logicaFake/hacerLogin.js
// Lógica "fake" que ejecuta el navegador (JavaScript).
//   Es el espejo que usa la parte de interfaz (HTML) para no conocer el
//   lado del servidor: la pantalla llama a hacerLogin(nombre, password, cb)
//   y esta función hace la llamada remota (AJAX/XMLHttpRequest) al
//   endpoint REST rest/hacerLogin.php. Cuando llega la respuesta, la
//   entrega mediante callback(resultado).
//   En un futuro, esta "fake" puede convertirse en la llamada real o
//   sustituirse por un fetch() moderno.
// -------------------------------------------------------------------
// ---------------------------------------------------
//
// versión fake de una función de la lógica
//
// nombre:Texto, password:Texto -> hacerLogin() -> Boolean
//
// (Boolean devuelto via callback)
//
// ---------------------------------------------------
function hacerLogin( nombre, password, cb ) {

	// preparar la llamada remota
	var xmlhttp = new XMLHttpRequest();
	xmlhttp.onreadystatechange = function() {
		// callback para cuando llegue la respuesta
		// de la petición que haremos más abajo

		if( this.readyState == 4 && this.status == 200 ){
			// este es el texto JSON recibido la llamada a
			// demo_file.php, pasado a objeto JSON 
			console.log( "recibo: " + this.responseText )
			var resultado = JSON.parse(this.responseText);

			cb( resultado ) // devuelvo el resultado
		}
	};
	
	// llamamos *remotamente* al fichero hacerLogin.php
	// (la verdadera función de la lógica)
	xmlhttp.open("GET", "../rest/hacerLogin.php?nombre="+nombre+"&password="+password, true);
	xmlhttp.send();

} // ()
