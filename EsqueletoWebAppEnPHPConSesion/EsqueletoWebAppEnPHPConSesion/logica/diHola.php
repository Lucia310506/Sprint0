<?php
// -------------------------------------------------------------------
// logica/diHola.php
// Capa de lógica de negocio.
//   Contiene la función "de verdad" diHola(): construye el saludo que
//   se devuelve al usuario. No sabe nada de HTTP, sesiones ni JSON;
//   recibe el usuario y devuelve un objeto con nombre + saludo.
//   Es la contrapartida real de ux/logicaFake/diHola.js.
// -------------------------------------------------------------------
// -------------------------------------------------
//
// usuario:Texto -> diHola() -> (nombre:Texto, saludo:Texto) 
//
// -------------------------------------------------

function diHola( $usuario ) {
  $objetoResultado = new stdClass;
  $objetoResultado->nombre = $usuario;
  $objetoResultado->saludo = "That's all folks";

  return $objetoResultado;
}

?>
