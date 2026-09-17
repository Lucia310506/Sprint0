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

  // comprobación "rigurosa" del password
  if ( $password == "1234" ) {
    return true;
  }

  return false;
}
?>
