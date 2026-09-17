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

		if( this.readyState == 4 ){

			// ANTES->DESPUÉS (B4): antes solo se atendía cuando status==200;
			// con 400/401/500 la interfaz se quedaba esperando.
			// MOTIVO: hay que devolver siempre algo al callback.
			if ( this.status != 200 ) {
				cb( { resultado: false, error: "error HTTP " + this.status } )
				return
			}

			// ANTES->DESPUÉS (B5): JSON.parse() podía lanzar excepción si el
			// servidor devolvía algo que no es JSON (p.ej. un warning PHP).
			// MOTIVO: no romper la interfaz ante una respuesta inválida.
			var resultado
			try {
				resultado = JSON.parse( this.responseText )
			} catch ( e ) {
				cb( { resultado: false, error: "respuesta JSON inválida" } )
				return
			}

			console.log( "recibo: " + this.responseText )
			cb( resultado ) // devuelvo el resultado
		}
	};
	
	// ANTES->DESPUÉS (V2/V7): se enviaba por GET y las credenciales quedaban
	//        en la URL. MOTIVO: el login debe ir por POST.
	// ANTES->DESPUÉS (V6): nombre/password se pegaban sin codificar; con
	//        caracteres como &, ?, =, # se rompía la consulta.
	//        MOTIVO: encodeURIComponent() escapa esos caracteres.
	// llamamos *remotamente* al fichero hacerLogin.php
	// (la verdadera función de la lógica)
	xmlhttp.open("POST", "../rest/hacerLogin.php", true);
	xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xmlhttp.send( "nombre=" + encodeURIComponent(nombre) + "&password=" + encodeURIComponent(password) );

} // ()