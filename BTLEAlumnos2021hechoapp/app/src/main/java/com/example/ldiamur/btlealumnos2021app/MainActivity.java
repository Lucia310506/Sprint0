
package com.example.ldiamur.btlealumnos2021app;
// ------------------------------------------------------------------
// ------------------------------------------------------------------

import android.app.Activity;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.UUID;

// ------------------------------------------------------------------
// class MainActivity
//Es la pantalla principal de la app.
//   Se encarga de:
//   - pedir los permisos de Bluetooth/Localización que falten según la
//     versión de Android (API >= 31 usa BLUETOOTH_SCAN/CONNECT)
//   - escanear BLE en busca de todos los dispositivos o de uno concreto
//   - parsear cada anuncio como TramaIBeacon y volcarlo a logcat
//     (uuid, major, minor, txPower, rssi)
// ------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private static final String ETIQUETA_LOG = ">>>>";

    private static final int CODIGO_PETICION_PERMISOS = 11223344;

    // para el diálogo "activa Bluetooth" (P14)
    private static final int CODIGO_PETICION_ACTIVAR_BLUETOOTH = 11223345;

    // tiempo máximo de escaneo: superado, se detiene solo (P19)
    private static final long TIEMPO_MAXIMO_ESCANEO_MS = 10000;

    // --------------------------------------------------------------
    // --------------------------------------------------------------
    private BluetoothLeScanner elEscanner;

    private ScanCallback callbackDelEscaneo = null;

    // P19: se detiene el escaneo solo al cabo de TIEMPO_MAXIMO_ESCANEO_MS
    private final Handler manejador = new Handler();
    private final Runnable detenerEscaneoAutomaticamente = new Runnable() {
        @Override
        public void run() {
            Log.d(ETIQUETA_LOG, " detenerEscaneoAutomaticamente(): han pasado "
                    + TIEMPO_MAXIMO_ESCANEO_MS + " ms, detengo el escaneo ");
            detenerBusquedaDispositivosBTLE();
        } // run()
    };

    // --------------------------------------------------------------
    //buscarTodosLosDispositivosBTLE()->Clase(Modificar)
    // --------------------------------------------------------------
    private void buscarTodosLosDispositivosBTLE() {
        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empieza ");

        // P17: sin escáner no hay nada que hacer
        if ( this.elEscanner == null ) {
            Log.e(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): el escáner BLE no está disponible ");
            return;
        }

        // P18: si ya había un escaneo en marcha, se detiene antes de empezar otro
        detenerBusquedaDispositivosBTLE();

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): instalamos scan callback ");

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanResult() ");

                mostrarInformacionDispositivoBTLE( resultado );
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): onScanFailed() ");

            }
        };

        Log.d(ETIQUETA_LOG, " buscarTodosLosDispositivosBTL(): empezamos a escanear ");

        this.elEscanner.startScan( this.callbackDelEscaneo);

        // P19: nos aseguramos de que la búsqueda no se alargue eternamente
        this.manejador.postDelayed( this.detenerEscaneoAutomaticamente, TIEMPO_MAXIMO_ESCANEO_MS );

    } // ()

    // --------------------------------------------------------------
    //resultado:ScanResult->mostrarInformacionDispositivoBTLE()
    // --------------------------------------------------------------
    private void mostrarInformacionDispositivoBTLE( ScanResult resultado ) {

        BluetoothDevice bluetoothDevice = resultado.getDevice();

        // P20: un anuncio puede llegar sin scan record; sin bytes no hay nada que parsear
        if ( resultado.getScanRecord() == null ) {
            Log.d(ETIQUETA_LOG, " mostrarInformacionDispositivoBTLE(): llegó un anuncio sin scan record; lo ignoramos ");
            return;
        }

        byte[] bytes = resultado.getScanRecord().getBytes();
        int rssi = resultado.getRssi();

        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " ****** DISPOSITIVO DETECTADO BTLE ****************** ");
        Log.d(ETIQUETA_LOG, " ****************************************************");
        Log.d(ETIQUETA_LOG, " nombre = " + bluetoothDevice.getName());
        Log.d(ETIQUETA_LOG, " toString = " + bluetoothDevice.toString());

        /*
        ParcelUuid[] puuids = bluetoothDevice.getUuids();
        if ( puuids.length >= 1 ) {
            //Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].getUuid());
           // Log.d(ETIQUETA_LOG, " uuid = " + puuids[0].toString());
        }*/

        Log.d(ETIQUETA_LOG, " dirección = " + bluetoothDevice.getAddress());
        Log.d(ETIQUETA_LOG, " rssi = " + rssi );

        Log.d(ETIQUETA_LOG, " bytes = " + new String(bytes));
        Log.d(ETIQUETA_LOG, " bytes (" + bytes.length + ") = " + Utilidades.bytesToHexString(bytes));

        // P22: solo interpretamos como iBeacon los anuncios que realmente lo son
        boolean esIBeacon = false;
        int iBeaconTypeRaw = -1;
        int iBeaconLengthRaw = -1;

        try {
            TramaIBeacon posible = new TramaIBeacon(bytes);
            esIBeacon = posible.esIBeacon();
            iBeaconTypeRaw = posible.getiBeaconType() & 0xFF;
            iBeaconLengthRaw = posible.getiBeaconLength() & 0xFF;
        } catch (IllegalArgumentException ex) {
            // P21: la trama era demasiado corta para ser iBeacon
        }

        Log.d(ETIQUETA_LOG, " esIBeacon? = " + esIBeacon + " (companyID 0x004C, tipo 0x02, longitud 0x15) ");
        if ( ! esIBeacon ) {
            Log.d(ETIQUETA_LOG, " no parece un iBeacon: tipo=0x" + Integer.toHexString(iBeaconTypeRaw)
                    + " longitud=0x" + Integer.toHexString(iBeaconLengthRaw) );
            return;
        }

        TramaIBeacon tib = new TramaIBeacon(bytes);

        Log.d(ETIQUETA_LOG, " ----------------------------------------------------");
        Log.d(ETIQUETA_LOG, " prefijo  = " + Utilidades.bytesToHexString(tib.getPrefijo()));
        Log.d(ETIQUETA_LOG, "          advFlags = " + Utilidades.bytesToHexString(tib.getAdvFlags()));
        Log.d(ETIQUETA_LOG, "          advHeader = " + Utilidades.bytesToHexString(tib.getAdvHeader()));
        Log.d(ETIQUETA_LOG, "          companyID = " + Utilidades.bytesToHexString(tib.getCompanyID()));
        Log.d(ETIQUETA_LOG, "          iBeacon type = " + Integer.toHexString(tib.getiBeaconType()));
        Log.d(ETIQUETA_LOG, "          iBeacon length 0x = " + Integer.toHexString(tib.getiBeaconLength()) + " ( "
                + tib.getiBeaconLength() + " ) ");
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToHexString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " uuid  = " + Utilidades.bytesToString(tib.getUUID()));
        Log.d(ETIQUETA_LOG, " major  = " + Utilidades.bytesToHexString(tib.getMajor()) + "( "
                + Utilidades.bytesToInt(tib.getMajor()) + " ) ");
        Log.d(ETIQUETA_LOG, " minor  = " + Utilidades.bytesToHexString(tib.getMinor()) + "( "
                + Utilidades.bytesToInt(tib.getMinor()) + " ) ");
        Log.d(ETIQUETA_LOG, " txPower  = " + Integer.toHexString(tib.getTxPower()) + " ( " + tib.getTxPower() + " )");
        Log.d(ETIQUETA_LOG, " ****************************************************");

    } // ()

    // --------------------------------------------------------------
    // dispositivoBuscado:texto->buscarEsteDispositivoBTLE()->Clase(Modificar)
    // --------------------------------------------------------------
    private void buscarEsteDispositivoBTLE(final String dispositivoBuscado ) {
        Log.d(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): empieza ");

        // P17: sin escáner no hay nada que hacer
        if ( this.elEscanner == null ) {
            Log.e(ETIQUETA_LOG, " buscarEsteDispositivoBTLE(): el escáner BLE no está disponible ");
            return;
        }

        // P18: si ya había un escaneo en marcha, se detiene antes de empezar otro
        detenerBusquedaDispositivosBTLE();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): instalamos scan callback ");


        // super.onScanResult(ScanSettings.SCAN_MODE_LOW_LATENCY, result); para ahorro de energía

        this.callbackDelEscaneo = new ScanCallback() {
            @Override
            public void onScanResult( int callbackType, ScanResult resultado ) {
                super.onScanResult(callbackType, resultado);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanResult() ");

                mostrarInformacionDispositivoBTLE( resultado );
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onBatchScanResults() ");

            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): onScanFailed() ");

            }
        };

        ScanFilter sf = new ScanFilter.Builder().setDeviceName( dispositivoBuscado ).build();

        Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado );
        //Log.d(ETIQUETA_LOG, "  buscarEsteDispositivoBTLE(): empezamos a escanear buscando: " + dispositivoBuscado
          //      + " -> " + Utilidades.stringToUUID( dispositivoBuscado ) );

        this.elEscanner.startScan(
                java.util.Collections.singletonList(sf),
                new android.bluetooth.le.ScanSettings.Builder().build(),
                this.callbackDelEscaneo
        );

        // P19: nos aseguramos de que la búsqueda no se alargue eternamente
        this.manejador.postDelayed( this.detenerEscaneoAutomaticamente, TIEMPO_MAXIMO_ESCANEO_MS );
    } // ()

    // --------------------------------------------------------------
    // detenerBusquedaDispositivosBTLE()<-Clase(consultar)
    //                                  ->Clase(Modificar)
    // --------------------------------------------------------------
    private void detenerBusquedaDispositivosBTLE() {

        // P19: desactivamos el autoparado programado (ya no hace falta)
        this.manejador.removeCallbacks( this.detenerEscaneoAutomaticamente );

        if ( this.callbackDelEscaneo == null ) {
            return;
        }

        // P17: si el escáner no está, no podemos parar nada (pero limpiamos el callback)
        if ( this.elEscanner != null ) {
            this.elEscanner.stopScan( this.callbackDelEscaneo );
        }
        this.callbackDelEscaneo = null;

    } // ()

    // --------------------------------------------------------------
    // v:Vista->botonBuscarDispositivosBTLEPulsado()<-Clase(Consultar)
    // --------------------------------------------------------------
    public void botonBuscarDispositivosBTLEPulsado( View v ) {
        Log.d(ETIQUETA_LOG, " boton buscar dispositivos BTLE Pulsado" );
        this.buscarTodosLosDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // v:Vista->botonBuscarNuestroDispositivoBTLEPulsado()<-Clase(Consultar)
    // --------------------------------------------------------------
    public void botonBuscarNuestroDispositivoBTLEPulsado( View v ) {
        Log.d(ETIQUETA_LOG, " boton nuestro dispositivo BTLE Pulsado" );
        //this.buscarEsteDispositivoBTLE( Utilidades.stringToUUID( "EPSG-GTI-PROY-3A" ) );

        //this.buscarEsteDispositivoBTLE( "EPSG-GTI-PROY-3A" );
        this.buscarEsteDispositivoBTLE( "GTI-3A" );

    } // ()

    // --------------------------------------------------------------
    // v:Vista->botonDetenerBusquedaDispositivosBTLEPulsado()<-Clase(Consultar)
    // --------------------------------------------------------------
    public void botonDetenerBusquedaDispositivosBTLEPulsado( View v ) {
        Log.d(ETIQUETA_LOG, " boton detener busqueda dispositivos BTLE Pulsado" );
        this.detenerBusquedaDispositivosBTLE();
    } // ()

    // --------------------------------------------------------------
    // permisos Bluetooth necesarios según la versión de Android:
    //   - API >= 31 : BLUETOOTH_SCAN + BLUETOOTH_CONNECT + ACCESS_FINE_LOCATION
    //   - API <  31 : BLUETOOTH + BLUETOOTH_ADMIN + ACCESS_FINE_LOCATION
    // --------------------------------------------------------------
    private String[] permisosBluetoothNecesarios() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
            return new String[]{
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    } // ()

    // --------------------------------------------------------------
    // tengoTodosLosPermisosBluetooth()->B
    // P16: comprueba TODOS los permisos necesarios, no solo el primero.
    // --------------------------------------------------------------
    private boolean tengoTodosLosPermisosBluetooth() {
        for ( String permiso : permisosBluetoothNecesarios() ) {
            if ( ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED ) {
                return false;
            }
        }
        return true;
    } // ()

    // --------------------------------------------------------------
    // inicializarBlueTooth()->Clase(Modificar)
    //
    // ANTES->DESPUÉS:
    //   - ANTES: bta.enable() sin comprobar que el adaptador existiera (P13):
    //            BluetoothAdapter bta = ...; bta.enable();  -> si no hay BT, NPE.
    //   - ANTES: los permisos pedidos eran los antiguos (BLUETOOTH/ADMIN) incluso
    //            en Android 12+ (P12); en API>=31 hace falta BLUETOOTH_SCAN/CONNECT.
    //   - ANTES: se empezaba a escanear sin comprobar los permisos (P15).
    //   - AHORA: se comprueba primero que haya adaptador y permisos, y en
    //            función de la versión de Android se piden los correctos.
    // --------------------------------------------------------------
    private void inicializarBlueTooth() {
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos adaptador BT ");

        // P13: el dispositivo podría no tener Bluetooth
        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        if ( bta == null ) {
            Log.e(ETIQUETA_LOG, " inicializarBlueTooth(): este dispositivo NO tiene Bluetooth; nos vamos ");
            return;
        }

        // P15/P16: pedir primero los permisos correctos; en onRequestPermissionsResult()
        // se continuará si hace falta (no se escanea sin permiso).
        if ( ! tengoTodosLosPermisosBluetooth() ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): faltan permisos, los pido ");
            ActivityCompat.requestPermissions( MainActivity.this, permisosBluetoothNecesarios(), CODIGO_PETICION_PERMISOS );
            return;
        }
        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): parece que YA tengo los permisos necesarios !!!!");

        // P14: ANTES->DESPUÉS: no se forzaba bta.enable() silenciosamente y solo
        //        se avisaba por log si el Bluetooth estaba apagado.
        //        MOTIVO: para poder escanear hay que activar Bluetooth, y en
        //        Android 12+ enable() no muestra el diálogo; así lo activa el
        //        usuario con el diálogo del sistema (ACTION_REQUEST_ENABLE).
        if ( ! bta.isEnabled() ) {
            Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): el Bluetooth está apagado; pedimos al usuario que lo active " );
            Intent activarBT = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            startActivityForResult( activarBT, CODIGO_PETICION_ACTIVAR_BLUETOOTH );
            return; // seguimos en onActivityResult()
        }

        Log.d(ETIQUETA_LOG, " inicializarBlueTooth(): obtenemos escaner btle ");

        this.elEscanner = bta.getBluetoothLeScanner();

        // P17: si no hay escáner, no podemos escanear (tampoco hay que intentarlo)
        if ( this.elEscanner == null ) {
            Log.e(ETIQUETA_LOG, " inicializarBlueTooth(): NO hemos obtenido escaner btle  !!!!");
            return;
        }
    } // ()


    // --------------------------------------------------------------
    // savedInstanceState:Bundle->onCreate()
    // --------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(ETIQUETA_LOG, " onCreate(): empieza ");

        // ANTES->DESPUÉS: al arrancar la app no se comprobaba nada, así que un
        // fallo en las conversiones de bytes/uuid o en el parseo del iBeacon solo
        // se veía al usar la app. AHORA, al arrancar, se lanzan unas pruebas
        // automáticas (power-on self test) que escriben TEST OK / TEST FALLIDO
        // en logcat (filtrar por ">>>>"). Es el equivalente Android de
        // AutoTests::ejecutarAutoTests() en setup() de la placa Arduino.
        AutoTests.ejecutarAutoTests();

        inicializarBlueTooth();

        Log.d(ETIQUETA_LOG, " onCreate(): termina ");

    } // onCreate()

    // --------------------------------------------------------------
    // requestCode:N, permissions:[texto], grantResults:[Z]->onRequestPermissionsResult()
    //
    // ANTES->DESPUÉS:
    //   - ANTES: solo comprobaba grantResults[0] y no continuaba el flujo .
    //   - AHORA: comprueba TODOS los permisos pedidos y, si se conceden,
    //            reanuda la inicialización de Bluetooth (para tener escáner).
    // --------------------------------------------------------------
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults);

        switch (requestCode) {
            case CODIGO_PETICION_PERMISOS:
                // compruebo que TODOS los permisos pedidos hayan sido concedidos
                boolean todosConcedidos = ( permissions != null && grantResults != null );
                if ( todosConcedidos ) {
                    for ( int i=0; i<permissions.length; i++ ) {
                        if ( grantResults[i] != PackageManager.PERMISSION_GRANTED ) {
                            todosConcedidos = false;
                            break;
                        } // if
                    } // for
                } // if

                if ( todosConcedidos ) {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): permisos concedidos  !!!!");
                    // P15: ahora que tenemos permiso, completamos la inicialización (escáner)
                    inicializarBlueTooth();
                } else {
                    Log.d(ETIQUETA_LOG, " onRequestPermissionResult(): Socorro: permisos NO concedidos  !!!!");
                } // if
                return;
        } // switch
        // Other 'case' lines to check for other
        // permissions this app might request.
    } // ()

    // --------------------------------------------------------------
    // requestCode:N, resultCode:N, data:Intent->onActivityResult()
    //
    // ANTES->DESPUÉS (P14): este método no existía; antes solo se avisaba por
    //        log de que el Bluetooth estaba apagado.
    //        MOTIVO: cuando el usuario acepta el diálogo de activación hay que
    //        continuar la inicialización para poder obtener el escáner BLE.
    // --------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CODIGO_PETICION_ACTIVAR_BLUETOOTH:
                if ( resultCode == Activity.RESULT_OK ) {
                    Log.d(ETIQUETA_LOG, " onActivityResult(): el usuario ACTIVÓ Bluetooth; continuamos ");
                    inicializarBlueTooth();
                } else {
                    Log.d(ETIQUETA_LOG, " onActivityResult(): el usuario NO activó Bluetooth " );
                } // if
                return;
        } // switch
    } // ()

} // class
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------
// --------------------------------------------------------------


