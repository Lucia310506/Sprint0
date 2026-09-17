<?php

// -------------------------------------------------------------------
// rest/hacerLogin.php
// Capa REST (punto de entrada HTTP del servidor).
//   Es el endpoint de inicio de sesión: recibe nombre+password por POST,
//   delega la comprobación de credenciales en logica/hacerLogin.php y,
//   si es correcta, guarda el usuario en la sesión PHP (para que los
//   demás endpoints sepan que hay un usuario acreditado).
//   Se encarga de:
//   - leer los parámetros nombre/password del cuerpo de la petición
//   - llamar a la función de negocio hacerLogin()
//   - crear/destruir la sesión según el resultado
//   - devolver la respuesta como JSON
//   No contiene la regla de negocio del login: solo la orquesta.
// -------------------------------------------------------------------

require_once('../logica/hacerLogin.php');

// ----------------------------------------------------------------
//
// POST ../rest/hacerLogin.php
//   cuerpo (application/x-www-form-urlencoded):
//     nombre=<Texto>&password=<Texto>
//
// ANTES->DESPUÉS (V2/V7): el login se hacía por GET y las credenciales
//        viajaban en la URL (quedaban en logs/historial y visibles).
//        MOTIVO: las contraseñas no deben viajar ni por GET ni en la URL.
//
// @return
//  VoF: true si el login OK
// 
// @return
//  usuario:Texto
//       devuelto implicitamente en la variable global de sesión
//       (en el navegador no se podrá acceder a la var. global)
//
// ----------------------------------------------------------------

// ANTES->DESPUÉS (B3): no se comprobaba el método HTTP; ahora solo se
//        admite POST (el login no debe hacerse por GET nunca).
if ( $_SERVER["REQUEST_METHOD"] !== "POST" ) {
  header("Content-Type: application/json; charset=utf-8");
  http_response_code(405); // Method Not Allowed
  echo json_encode( [ "resultado" => false, "error" => "solo se admite POST" ] );
  return;
}

// ANTES->DESPUÉS (V5): la cookie de sesión se creaba con la config por
//        defecto del servidor. MOTIVO: HttpOnly evita que el JS la lea
//        (robo via XSS) y SameSite=Lax mitiga el CSRF; en producción
//        hay que ponerla SOLO por HTTPS (Secure=true).
session_set_cookie_params( [
  "httponly" => true,
  "samesite" => "Lax",
  // "secure" => true, // descomentar cuando haya HTTPS
] );

// creo una sesión (una sola vez: antes había un segundo session_start)
session_start();

// ANTES->DESPUÉS (B4/V4): antes el atacante podía probar contraseñas sin
//        límite. MOTIVO: limitar los intentos fallidos de login.
$intentosFallidos = isset( $_SESSION["intentos_login_fallidos"] ) ? $_SESSION["intentos_login_fallidos"] : 0;
if ( $intentosFallidos >= 5 ) {
  header("Content-Type: application/json; charset=utf-8");
  http_response_code(429); // Too Many Requests
  echo json_encode( [ "resultado" => false, "error" => "demasiados intentos fallidos de login" ] );
  return;
}

$objetoResultado = new stdClass;

// ANTES->DESPUÉS (B1): se leía $_GET["nombre"] directamente sin comprobar
//        si existía (avisos PHP y posibles null). MOTIVO: con POST además
//        hay que verificar que el campo llegó.
$nombre = isset( $_POST["nombre"] ) ? trim( $_POST["nombre"] ) : "";
$password = isset( $_POST["password"] ) ? $_POST["password"] : "";

//
// llamada a la verdadera función.
//
if ( $nombre !== "" && hacerLogin( $nombre, $password ) == true  ) {

  $objetoResultado->resultado = true;
  $objetoResultado->usuario = $nombre;

  // ANTES->DESPUÉS (V3): tras un login correcto no se regeneraba el id de
  //        sesión. MOTIVO: evitar fijación de sesión (session fixation),
  //        dándole al usuario un id nuevo al acreditarse.
  session_regenerate_id( true );

  $_SESSION["usuario"] = $nombre;

  // login OK: reseteamos el contador de intentos fallidos
  unset( $_SESSION["intentos_login_fallidos"] );

} else {
  session_destroy();
  $objetoResultado->resultado = false;
  $objetoResultado->error = "login incorrecto";

  // contabilizamos el intento fallido para el límite (V4)
  session_start();
  $_SESSION["intentos_login_fallidos"] = isset( $_SESSION["intentos_login_fallidos"] ) ? $_SESSION["intentos_login_fallidos"] + 1 : 1;
}

// ANTES->DESPUÉS (B9): se devolvía JSON sin indicar el Content-Type.
//        MOTIVO: el cliente debe saber que la respuesta es JSON UTF-8.
header('Content-Type: application/json; charset=utf-8');

// echo == devolver
echo json_encode( $objetoResultado );
?>