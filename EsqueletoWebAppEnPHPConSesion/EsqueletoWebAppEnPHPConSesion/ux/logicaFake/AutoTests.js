// -----------------------------------------------------------------------------------
// ux/logicaFake/AutoTests.js
// Pruebas automáticas de la capa "fake" (JavaScript) del navegador.
//
// CÓMO FUNCIONA
// -------------
//   Las funciones de logicaFake (hacerLogin, diHola) hacen una petición AJAX
//   real con XMLHttpRequest. Para poder probarlas sin servidor, estas pruebas
//   SUSTITUYEN temporalmente XMLHttpRequest por uno falso que devuelve una
//   respuesta preparada (mock) y llama al callback al momento.
//     - testHacerLoginOK():          el servidor responde login correcto
//     - testHacerLoginFallo():       el servidor responde login incorrecto (400)
//     - testDiHolaOK():              el servidor responde con saludo (error:0)
//     - testDiHolaNoAcreditado():    el servidor responde "usuario no acreditado"
//   Al terminar se restaura el XMLHttpRequest auténtico.
//
//   Cada prueba usa el if/else sencillo:
//
//     hacerLogin(...)  ->  callback(res)
//     if ( res.resultado == true ) { console.log("TEST OK") }
//     else                         { console.log("TEST FALLIDO") }
// -----------------------------------------------------------------------------------

// -----------------------------------------------------------------------------------
// AutoTestsFakeXHR( codigoHTTP, textoRespuesta )
// Crea una "clase" XMLHttpRequest falsa que, al hacer send(), devuelve al momento
// el código HTTP y el texto de respuesta indicados y dispara onreadystatechange.
// Es el equivalente a simular el servidor para no depender de la red.
// -----------------------------------------------------------------------------------
function AutoTestsFakeXHR( codigoHTTP, textoRespuesta ) {
	return function () {
		this.readyState = 0;
		this.status = codigoHTTP;
		this.responseText = textoRespuesta;

		this.open = function ( metodo, url, asincrono ) {
			this._metodo = metodo;
			this._url = url;
		};

		this.setRequestHeader = function () { /* no hace falta en la prueba */ };

		this.send = function () {
			// simulamos que el servidor ha respondido ya
			this.readyState = 4;
			if ( typeof this.onreadystatechange === "function" ) {
				this.onreadystatechange();
			}
		};
	};
} // ()

// -----------------------------------------------------------------------------------
// ejecutarAutoTests()
// Ejecuta todas las pruebas y escribe el resultado en la consola del navegador.
// Devuelve cuántas han fallado (0 = todo bien).
// -----------------------------------------------------------------------------------
function ejecutarAutoTests() {

	var XHRReal = window.XMLHttpRequest; // guardamos el de verdad para restaurarlo
	var contadorAciertos = 0;
	var contadorFallos = 0;

	// imprime el resultado de una prueba y actualiza los contadores
	function comprobar( nombre, condicion ) {
		if ( condicion ) {
			console.log( "test" + nombre + "(): TEST OK" );
			contadorAciertos++;
		} else {
			console.log( "test" + nombre + "(): TEST FALLIDO" );
			contadorFallos++;
		}
	}

	console.log( "==== AUTO-TESTS WEB (consola): empieza ====" );

	// ---- testHacerLoginOK: login correcto --------------------------------------
	var resLogin = null;
	window.XMLHttpRequest = AutoTestsFakeXHR( 200, '{"resultado":true,"usuario":"Mickey"}' );
	hacerLogin( "Mickey", "1234", function ( res ) { resLogin = res; } );
	comprobar( "HacerLoginOK",
		resLogin !== null && resLogin.resultado === true && resLogin.usuario === "Mickey" );

	// ---- testHacerLoginFallo: el servidor responde 400 -------------------------
	var resFallo = null;
	window.XMLHttpRequest = AutoTestsFakeXHR( 400, '{"resultado":false,"error":"login incorrecto"}' );
	hacerLogin( "Mickey", "mal", function ( res ) { resFallo = res; } );
	comprobar( "HacerLoginFallo",
		resFallo !== null && resFallo.resultado === false );

	// ---- testDiHolaOK: acreditado, saludo correcto -----------------------------
	var errHola = "sin-llamar";
	var resHola = null;
	window.XMLHttpRequest = AutoTestsFakeXHR( 200, '{"nombre":"Mickey","saludo":"That\'s all folks","error":0}' );
	diHola( function ( err, res ) { errHola = err; resHola = res; } );
	comprobar( "DiHolaOK",
		errHola === null && resHola !== null && resHola.saludo === "That's all folks" );

	// ---- testDiHolaNoAcreditado: el servidor avisa de error ---------------------
	var errNoAcred = null;
	var resNoAcred = "sin-llamar";
	window.XMLHttpRequest = AutoTestsFakeXHR( 200, '{"error":"usuario no acreditado"}' );
	diHola( function ( err, res ) { errNoAcred = err; resNoAcred = res; } );
	comprobar( "DiHolaNoAcreditado",
		resNoAcred === null && errNoAcred === "usuario no acreditado" );

	// restauramos el XMLHttpRequest real (para que la app siga funcionando)
	window.XMLHttpRequest = XHRReal;

	console.log( "============ resumen: " + contadorAciertos
		+ " aciertos, " + contadorFallos + " fallos ============" );
	console.log( ( contadorFallos === 0 ) ? "============ TODO OK ============"
		: "============ HAY FALLOS ============" );

	return contadorFallos;
} // ()

// lanzamos las pruebas al cargar la página
ejecutarAutoTests();
