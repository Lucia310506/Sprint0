<?php

// -------------------------------------------------------------------
// rest/diHola.php
// Capa REST (punto de entrada HTTP del servidor).
//   Es el "waiter" del endpoint: recibe la petición HTTP de saludar,
//   comprueba que la petición viene de un usuario ya acreditado
//   (sesión activa) y delega el trabajo real en logica/diHola.php.
//   Se encarga de:
//   - leer el usuario de la sesión (no viene en la URL)
//   - llamar a la función de negocio diHola()
//   - devolver la respuesta como JSON
//   No contiene reglas de negocio: solo orquesta HTTP + sesión + JSON.
// -------------------------------------------------------------------

require_once('../logica/diHola.php');

// -------------------------------------------------
//
// GET ../rest/diHola.php
//
// usuario:Texto -> diHola() -> (nombre:Texto, saludo:Texto) | error:Texto
//
// usuario: recibido de forma implícita en la sesión
// (nombre:Texto, saludo:Texto) | error:Texto : devuelto en un mismo JSON
//
// -------------------------------------------------

session_start();

// creo el objeto resultado
$objetoResultado = new stdClass;

// compruebo si esto lo pide un usuario
// antes acreditado mediante login
if ( ! isset( $_SESSION["usuario"]) ) {
  // no es un usuario acreditado
  $objetoResultado->error = "usuario no acreditado";
  // $objetoResultado->nombre = "";
  // $objetoResultado->saludo = "";
  // echo == devolver
  echo json_encode( $objetoResultado );
  return;
}

// Sí que es un usuario acreditado:
$usuario = $_SESSION["usuario"];

//
// llamada a la verdadera función.
//
$objetoResultado = diHola( $usuario );

$objetoResultado->error = 0;

// echo == devolver
echo json_encode( $objetoResultado );
?>
