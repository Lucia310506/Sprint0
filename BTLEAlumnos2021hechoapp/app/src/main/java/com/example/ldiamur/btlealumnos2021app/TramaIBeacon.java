
package com.example.ldiamur.btlealumnos2021app;

import java.util.Arrays;

// -----------------------------------------------------------------------------------
// class TramaIBeacon
//Parsea los bytes de un anuncio BLE y extrae los campos de un
//   iBeacon: prefijo (9 bytes), uuid (16), major (2), minor (2) y txPower (1).
//   - El constructor valida que la trama tenga al menos 30 bytes (si no, no parsea).
//   - esIBeacon() dice si el anuncio es un iBeacon de verdad de Apple
//     (companyID 0x004C, tipo 0x02, longitud 0x15).
// -----------------------------------------------------------------------------------
public class TramaIBeacon {
    private byte[] prefijo = null; // 9 bytes
    private byte[] uuid = null; // 16 bytes
    private byte[] major = null; // 2 bytes
    private byte[] minor = null; // 2 bytes
    private byte txPower = 0; // 1 byte

    private byte[] losBytes;

    private byte[] advFlags = null; // 3 bytes
    private byte[] advHeader = null; // 2 bytes
    private byte[] companyID = new byte[2]; // 2 bytes
    private byte iBeaconType = 0 ; // 1 byte
    private byte iBeaconLength = 0 ; // 1 byte

    // -------------------------------------------------------------------------------
    //[byte]<-getPrefijo()<- Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getPrefijo() {
        return prefijo;
    }

    // -------------------------------------------------------------------------------
    // UUID<-getUUID()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getUUID() {
        return uuid;
    }

    // -------------------------------------------------------------------------------
    //[byte]<-getMajor()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getMajor() {
        return major;
    }

    // -------------------------------------------------------------------------------
    //[byte]<-getMinor()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getMinor() {
        return minor;
    }

    // -------------------------------------------------------------------------------
    //byte<-getTxPower()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte getTxPower() {
        return txPower;
    }

    // -------------------------------------------------------------------------------
    //[byte]<-getLosBytes()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getLosBytes() {
        return losBytes;
    }

    // -------------------------------------------------------------------------------
    //[byte]<-getAdvFlags()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getAdvFlags() {
        return advFlags;
    }

    // -------------------------------------------------------------------------------
    //[byte]<-getAdvHeader()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getAdvHeader() {
        return advHeader;
    }

    // -------------------------------------------------------------------------------
    //[byte]<-getCompanyID()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte[] getCompanyID() {
        return companyID;
    }

    // -------------------------------------------------------------------------------
    //byte<-getiBeaconType()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte getiBeaconType() {
        return iBeaconType;
    }

    // -------------------------------------------------------------------------------
    //byte<-getiBeaconLength()<-Clase(Consultar)
    // -------------------------------------------------------------------------------
    public byte getiBeaconLength() {
        return iBeaconLength;
    }

    // -------------------------------------------------------------------------------
    // esIBeacon()->B ->Clase(Consultar)
    //
    // ANTES->DESPUÉS: no existía este método (aunque el comentario de clase
    //        decía que sí) y cada anuncio se interpretaba como iBeacon.
    // MOTIVO: un iBeacon de Apple de verdad debe llevar companyID 0x004C,
    //         tipo 0x02 y longitud 0x15; si no, los campos son falsos.
    // -------------------------------------------------------------------------------
    public boolean esIBeacon() {
        if ( companyID == null || companyID.length < 2 ) {
            return false;
        }

        int id = ( (companyID[0] & 0xFF) << 8 ) | ( companyID[1] & 0xFF );

        return      id == 0x004C
                && ( iBeaconType & 0xFF ) == 0x02
                && ( iBeaconLength & 0xFF ) == 0x15;
    } // ()

    // -------------------------------------------------------------------------------
    // bytes:[byte]->TramaIBeacon()->Clase(Modificar)
    //
    // ANTES->DESPUÉS: el constructor no validaba la longitud (el comentario
    //        decía que sí) y accedía directamente a losBytes[29].
    // MOTIVO: un anuncio BLE con menos de 30 bytes provocaba un
    //         ArrayIndexOutOfBoundsException (los bytes vienen de otros
    //         dispositivos y no se pueden controlar).
    // -------------------------------------------------------------------------------
    public TramaIBeacon(byte[] bytes ) {
        if ( bytes == null || bytes.length < 30 ) {
            throw new IllegalArgumentException( "Trama iBeacon demasiado corta" );
        }

        this.losBytes = bytes;

        prefijo = Arrays.copyOfRange(losBytes, 0, 8+1 ); // 9 bytes
        uuid = Arrays.copyOfRange(losBytes, 9, 24+1 ); // 16 bytes
        major = Arrays.copyOfRange(losBytes, 25, 26+1 ); // 2 bytes
        minor = Arrays.copyOfRange(losBytes, 27, 28+1 ); // 2 bytes
        txPower = losBytes[ 29 ]; // 1 byte

        advFlags = Arrays.copyOfRange( prefijo, 0, 2+1 ); // 3 bytes
        advHeader = Arrays.copyOfRange( prefijo, 3, 4+1 ); // 2 bytes
        companyID = Arrays.copyOfRange( prefijo, 5, 6+1 ); // 2 bytes
        iBeaconType = prefijo[ 7 ]; // 1 byte
        iBeaconLength = prefijo[ 8 ]; // 1 byte

    } // ()
} // class
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------
// -----------------------------------------------------------------------------------


