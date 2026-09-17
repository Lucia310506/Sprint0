<?php

// -------------------------------------------------------------------
// logica/hacerLogin.php
// Capa de lógica de negocio.
//   Contiene la función "de verdad" hacerLogin(): comprueba si las
//   credenciales (nombre + password) son válidas y devuelve true/false.
//   No sabe nada de HTTP ni de sesiones: esa responsabilidad la tiene
//   rest/hacerLogin.php. Es la contrapartida real de
//   ux/logicaFake/hacerLogin.js.
// -------------------------------------------------------------------
// ---------------------------------------------------------------
//
// nombre:Texto, password:Texto -> hacerLogin() -> VoF
//
// ---------------------------------------------------------------

function hacerLogin( $nombre, $password ) {

  // **********************************************************
  // ANTES->DESPUÉS (V1): se comprobaba SOLO el password ("1234"); con
  //        cualquier nombre el login era válido (bastaba adivinar la
  //        contraseña). MOTIVO: el login debe exigir también un nombre
  //        de usuario conocido.
  // **********************************************************

  // **********************************************************
  // ANTES->DESPUÉS (V1): la contraseña era "1234" hardcodeada.
  //        MOTIVO: es una credencial SOLO de práctica del esqueleto
  //        (usuario "Mickey" + password "1234"). En producción la
  //        comprobación debe ir contra los usuarios/hashes guardados
  //        en BD (p.ej. password_hash()/password_verify()), nunca con
  //        texto fijo en el código.
  // **********************************************************
  if ( $nombre == "Mickey" && $password == "1234" ) {
    return true;
  }

  return false;
}
?>
