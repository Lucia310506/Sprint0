package com.example.ldiamur.btlealumnos2021app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import android.os.AsyncTask;
import android.util.Log;

// ------------------------------------------------------------------------
// class PeticionarioREST
//  Envía peticiones HTTP/REST (GET, POST, ...) en segundo plano
//   (AsyncTask, para no bloquear la interfaz) y devuelve el código y el
//   cuerpo de la respuesta mediante un callback (RespuestaREST).
//   - Aplica timeouts de 5 s para no quedarse colgado
//   - Envía el cuerpo en UTF-8 y Content-Type application/json; charset=utf-8
//   - Si el servidor no responde bien, no rompe la app
// ------------------------------------------------------------------------
public class PeticionarioREST extends AsyncTask<Void, Void, Boolean> {

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    public interface RespuestaREST {
        void callback (int codigo, String cuerpo);
    }

    // --------------------------------------------------------------------
    // --------------------------------------------------------------------
    private String elMetodo;
    private String urlDestino;
    private String elCuerpo = null;
    private RespuestaREST laRespuesta;

    private int codigoRespuesta;
    private String cuerpoRespuesta = "";

    // --------------------------------------------------------------------
    // metodo:texto, urlDestino:texto, cuerpo:texto,->hacerPeticionREST()->Clase(Modificar)
    // laRespuesta:RespuestaREST
    //
    // ANTES: no validábamos método ni URL; con metodo = null, setRequestMethod(null)
    //        lanzaba excepción a mitad del envío.
    // PROBLEMA (P3): un null podía romper la petición en mitad del envío.
    // AHORA: validamos antes de empezar.
    // --------------------------------------------------------------------
    public void hacerPeticionREST (String metodo, String urlDestino, String cuerpo, RespuestaREST  laRespuesta) {
        if ( metodo == null || metodo.trim().isEmpty() ) {
            throw new IllegalArgumentException( "Método HTTP no válido: " + metodo );
        }
        if ( urlDestino == null || urlDestino.trim().isEmpty() ) {
            throw new IllegalArgumentException( "URL destino no válida" );
        }

        this.elMetodo = metodo;
        this.urlDestino = urlDestino;
        this.elCuerpo = cuerpo;
        this.laRespuesta = laRespuesta;

        this.execute(); // otro thread ejecutará doInBackground()
    }

    // --------------------------------------------------------------------
    // PeticionarioREST()
    // --------------------------------------------------------------------
    public PeticionarioREST() {
        Log.d("clienterestandroid", "constructor()");
    }

    // --------------------------------------------------------------------
    // B<-doInBackground()<-Clase(Consutar)
    //                 ->Clase(Modificar)
    // --------------------------------------------------------------------
    @Override
    protected Boolean doInBackground(Void... params) {
        Log.d("clienterestandroid", "doInBackground()");

        HttpURLConnection connection = null;
        try {

            // envio la peticion

            // pagina web para hacer pruebas: URL url = new URL("https://httpbin.org/html");
            // ordinador del despatx 158.42.144.126 // OK URL url = new URL("http://158.42.144.126:8080");

            Log.d("clienterestandroid", "doInBackground() me conecto a >" + urlDestino + "<");

            URL url = new URL(urlDestino);

            connection = (HttpURLConnection) url.openConnection();

            // ANTES->DESPUÉS (P1): no existían timeouts aunque el comentario afirmaba
            // "Aplica timeouts de 5 s"; una conexión podía quedarse colgada.
            // MOTIVO: sin timeouts la app puede bloquearse esperando una respuesta eterna.
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // ANTES->DESPUÉS (P7): el header decía "charset-utf-8" (con guion, incorrecto)
            // y el cuerpo se enviaba con writeBytes() que no garantiza UTF-8.
            // MOTIVO: el servidor debe interpretar el cuerpo como UTF-8 realmente.
            connection.setRequestProperty( "Content-Type", "application/json; charset=utf-8" );
            connection.setRequestMethod(this.elMetodo);
            // connection.setRequestProperty("Accept", "*/*);

            // connection.setUseCaches(false);
            connection.setDoInput(true);

            if ( ! this.elMetodo.equals("GET") && this.elCuerpo != null ) {
                Log.d("clienterestandroid", "doInBackground(): no es get, pongo cuerpo");
                connection.setDoOutput(true);
                // si no es GET, pongo el cuerpo que me den en la petición
                connection.getOutputStream().write( this.elCuerpo.getBytes(StandardCharsets.UTF_8) ); // P7
            }

            // ya he enviado la petición
            Log.d("clienterestandroid", "doInBackground(): petición enviada ");

            // ............................................................
            // ahora obtengo la respuesta
            // ............................................................

            int rc = connection.getResponseCode();
            String rm = connection.getResponseMessage();
            String respuesta = "" + rc + " : " + rm;
            Log.d("clienterestandroid", "doInBackground() recibo respuesta = " + respuesta);
            this.codigoRespuesta = rc;

            // ANTES->DESPUÉS (P5): antes se hacía getInputStream() siempre y, ante
            // errores HTTP (400/404/500), se lanzaba excepción pero se devolvía true
            // igualmente. AHORA usamos getErrorStream() según el código y, además,
            // devolvemos false si no se pudo leer el cuerpo.
            InputStream is = ( rc >= 400 ) ? connection.getErrorStream() : connection.getInputStream();

            if ( is != null ) {
                // ANTES->DESPUÉS (P8): antes se leía la respuesta con el charset por
                // defecto del sistema; AHORA con UTF-8 explícito.
                BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

                Log.d("clienterestandroid", "leyendo cuerpo");
                StringBuilder acumulador = new StringBuilder ();
                String linea;
                while ( (linea = br.readLine()) != null) {
                    // ANTES->DESPUÉS (P9): antes se volcaba cada línea y el cuerpo
                    // completo a logcat; AHORA no (podría ser información sensible).
                    acumulador.append(linea);
                }
                Log.d("clienterestandroid", "FIN leyendo cuerpo");

                this.cuerpoRespuesta = acumulador.toString();
            }

            return true; // doInBackground() termina bien

        } catch (IOException ex) {
            // excepción al conectar, enviar o leer (p.ej. respuesta sin cuerpo)
            Log.d("clienterestandroid", "doInBackground(): problema de entrada/salida: " + ex.getMessage());
            return false; // ANTES->DESPUÉS (P5): ya no devuelvo true cuando falla la lectura
        } catch (Exception ex) {
            Log.d("clienterestandroid", "doInBackground(): ocurrio alguna otra excepcion: " + ex.getMessage());
            return false;
        } finally {
            // ANTES->DESPUÉS (P6): antes connection.disconnect() solo se ejecutaba si
            // la lectura terminaba bien; AHORA se libera la conexión SIEMPRE.
            if ( connection != null ) {
                connection.disconnect();
            } // if
        } // try-finally
    } // ()

    // --------------------------------------------------------------------
    // comoFue:B->onPostExecute()<-Clase(Consulta)
    // --------------------------------------------------------------------
    protected void onPostExecute(Boolean comoFue) {
        // llamado tras doInBackground()
        Log.d("clienterestandroid", "onPostExecute() comoFue = " + comoFue);

        // ANTES->DESPUÉS (P4): antes se llamaba a callback sin comprobar null,
        // con laRespuesta = null daba NullPointerException.
        if ( this.laRespuesta != null ) {
            this.laRespuesta.callback(this.codigoRespuesta, this.cuerpoRespuesta);
        } // if
    }

} // class