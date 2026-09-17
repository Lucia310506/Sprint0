<?php

// -------------------------------------------------------------------
// rest/hacerLogin.php
// Capa REST (punto de entrada HTTP del servidor).
//   Es el endpoint de inicio de sesión: recibe nombre+password por GET,
//   delega la comprobación de credenciales en logica/hacerLogin.php y,
//   si es correcta, guarda el usuario en la sesión PHP (para que los
//   demás endpoints sepan que hay un usuario acreditado).
//   Se encarga de:
//   - leer los parámetros nombre/password de la URL
//   - llamar a la función de negocio hacerLogin()
//   - crear/destruir la sesión según el resultado
//   - devolver la respuesta como JSON
//   No contiene la regla de negocio del login: solo la orquesta.
// -------------------------------------------------------------------

require_once('../logica/hacerLogin.php');

// ----------------------------------------------------------------
//
// GET ../rest/hacerLogin.php?nombre=<Texto>&password=<Texto>
//
// @return
//  VoF: true si el login OK
// 
// @return
//  usuario:Texto
//       devuelto implicitamente en la variable global de sesión
//       (en el navegador no se pordrá acceder a la var. global)
//
// ----------------------------------------------------------------

$objetoResultado = new stdClass;

// creo una sesión 
session_start();

// obtengo valores de los parámetros
$nombre = $_GET["nombre"];
$password = $_GET["password"];

//
// llamada a la verdadera función.
//
if ( hacerLogin( $nombre, $password) == true  ) {

  $objetoResultado->resultado = true;
  $objetoResultado->usuario = $nombre;

  // guardo en la sesión el nombre del usuario
  session_start();
  $_SESSION["usuario"] = $_GET["nombre"];

} else {
  session_destroy();
  $objetoResultado->resultado = false;
}

// echo == devolver
echo json_encode( $objetoResultado );
?>
