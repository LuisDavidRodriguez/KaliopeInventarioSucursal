package com.example.david.inventariosucursal;


public class Constant {

    public static String RUTA_MEMORIA_DISPOSITIVO = "";
    public static final String NOMBRE_CARPETA = "mx.4103.klp";
    public static String INSTANCE_PATH = "";
    public static boolean ULTIMOS_DATOS_SINCRONIZADOS = false;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    public static final String UNO = "uno";
    public static final String DOS = "dos";
    public static final String TRES = "tres";
    public static final String CUATRO = "cuatro";
    public static final String CINCO = "cinco";
    public static final String SEIS = "seis";
    public static final String SIETE = "siete";




    public static final long DURATION_TRANSITION = 1000 ;

    public static final String KEY_INTENTFILTER_ESCANER = "com.qs.scancode";


    /**Constantes para validaciones del movimiento*/

    //cuando el agente seleccione por ejemplo entrada de los checkbox aqui se guardara
    //despues cuando el supervisor seleccione otra ves el tipo de movimiento se guardara en la de supervisor
    //al final compararemos ambas constantes para saber en ambos momentos se seleccionaron las mismas opciones
    public static String TIPO_MOVIMIENTO_SELECCIONADO_AGENTE = "";
    public static String TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR = "";
    public static String PASSWORD_DEL_AGENTE = "";
    public static String USUARIO_DEL_AGENTE = "";
    public static String NOMBRE_DEL_AGENTE = "";
    public static String NOMBRE_CORTO_DEL_AGENTE = "";





    public static String idPulseraAgente = "";//almacenan el numero de ruta al que pertenese la pulsera que se escaneo
    public static String idPulseraSupervisor = "";

    public static final String ENTRADA = "E";
    public static final String SALIDA = "S";

    public static boolean PERMISOS_NECESARIOS_OTORGADOS = false;









}
