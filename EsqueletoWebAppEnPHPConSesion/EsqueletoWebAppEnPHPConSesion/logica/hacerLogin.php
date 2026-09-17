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
  // ANTES->DESPUÉS (V1): la contraseña era "1234" hardcodeada.
  //        MOTIVO: es una credencial SOLO de práctica del esqueleto.
  //        En producción la comprobación debe ir contra un hash
  //        guardado en BD (p.ej. password_hash()/password_verify()),
  //        nunca con un texto fijo en el código.
  // **********************************************************
  if ( $password == "1234" ) {
    return true;
  }

  return false;
}
?>
