package com.example.david.inventariosucursal;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ConfiguracionesApp {
    //Usando SharedPreferences
    //Guardando las configuraciones del usuarioSupervisor, en este caso solo sera la pulcera que hemos seleccionado
    //fuente:http://www.ajpdsoft.com/modules.php?name=News&file=print&sid=556


    /**
     * Creamos los nombres de nuestros archivos utilizados y de la informacion colocada para acceder a ellos
     * mediante la instancia de este objeto y no tener que escribir el nombre del archivo o del parametro
     * en la llamada a esta actividad
     */

    private static final String NOMBRE_ARCHIVO_CONFIGURACIONES = "configuracionesInventario";
    private static final String NOMBRE_VALOR_NUMERO_PULSERA_SELECCIONADA = "pulseraSeleccionada";

    //VersionNameLuisda6.5
    private static final String NOMBRE_VALOR_RUTA_ASIGNADA = "numerRutaAsignada";
    private static final String NOMBRE_CODIGO_DISPOSITIVO_UNICO_UUID = "codigoDeDispositivo";
    private static final String NOMBRE_PULSERA_ASIGNADA = "pulseraAsignada";
    private static final String NOMBRE_USUARIO_INICIADO = "agenteAsignado";
    private static final String NOMBRE_COMPLETO_EMPLEADO = "nombreCompletoEmpleado";
    private static final String NOMBRE_ESTADO_SESION = "estadoDeLaSesion";
    private static final String NOMBRE_FECHA_CLIENTES_CONSULTA = "fechaDeLaConsultaDeClientes";
    private static final String NOMBRE_ZONA = "nombreDeLaZonaPorVisitar";
    private static final String NOMBRE_VERSION_INVENTARIO = "versionDelInventario";
    private static final String NOMBRE_FECHA_HORA_INICIO_SESION = "fechaHora";
    private static final String NOMBRE_DIA_INICIO_SESION = "diaInicioSesion";
    //---------------






    //el numero de la pulcera asignada es cuando el la base de datos de pulseras se selecciona que ese
    //dispositivo pertenese a la pulcera 2, es decir que utilizara los codigos que tenga asignados la puclera 2
    public static int getNumeroDePulseraAsignada (Activity activity){
        int recuperado;

        //(Creamos la variable de tipo SharedPreferences haciendo una llama a "getSharedPreferences",
        // le pasamos como parámetro a este procedimiento el nombre del fichero de configuración, en
        // nuestro caso "configuracionesKaliope" y el tipo de acceso "MODE_PRIVATE".)
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getInt" para leer el valor de la preferencia "pulseraSeleccionada"
        // , si no encuentra este valor devolverá el valor por defecto -1. para saber que no se encontro
        // el dato guardado, lo usaremos en main, si hay un -1 significa que tendremos que enviar al
        // usuarioSupervisor a que carge la tabla de pulceras)
        recuperado = sharedPreferences.getInt(NOMBRE_VALOR_NUMERO_PULSERA_SELECCIONADA,-1);

        //Toast.makeText(this,"dato recuperado de configuracion" + recuperado,Toast.LENGTH_SHORT).show();
        return recuperado;



    }
    public static void setNumeroDePulseraAsignada (Activity activity, int valor){
        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putInt(NOMBRE_VALOR_NUMERO_PULSERA_SELECCIONADA,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public void eliminarNumeroDePulseraAsignada(Activity activity){
        //lo vamos a llamar al eliminat la base de datos de las pulseras
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_VALOR_NUMERO_PULSERA_SELECCIONADA).apply();

        //para eliminar totalmente el archivo de configuraciones
        //sharedPreferences.edit().clear().apply();

    }



    //VersionNameLuisda6.5






    public static String getCodigoUnicoDispositivo (Activity activity){

        //(Creamos la variable de tipo SharedPreferences haciendo una llama a "getSharedPreferences",
        // le pasamos como parámetro a este procedimiento el nombre del fichero de configuración, en
        // nuestro caso "configuracionesKaliope" y el tipo de acceso "MODE_PRIVATE".)
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getInt" para leer el valor de la preferencia "codigoDeDispositivo"
        // , si no encuentra este valor devolverá el valor por defecto -1. para saber que no se encontro
        return sharedPreferences.getString(NOMBRE_CODIGO_DISPOSITIVO_UNICO_UUID,"SinValor");



    }

    public static void setCodigoDispositivoUnico ( Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_CODIGO_DISPOSITIVO_UNICO_UUID,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }





    public static String getRutaAsignada ( Activity activity){

        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_VALOR_RUTA_ASIGNADA,"SinValor");


    }
    public static void setRutaAsignada ( Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_VALOR_RUTA_ASIGNADA,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    private static void deleteRutaAsignada (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_VALOR_RUTA_ASIGNADA).apply();

    }

    public static String getCodigoPulseraAsignada(Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_PULSERA_ASIGNADA,"SinValor");
    }
    public static void setCodigoPulseraAsignada(Activity activity, String valor){
        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_PULSERA_ASIGNADA,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static void deleteCodigoPulseraAsignada (Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_PULSERA_ASIGNADA).apply();


    }

    public static String getUsuarioIniciado(Activity activity){

        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_USUARIO_INICIADO,"SinValor");


    }
    public static void setUsuarioIniciado(Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_USUARIO_INICIADO,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    private static void deleteUsuarioIniciado (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_USUARIO_INICIADO).apply();

    }

    private static void setNombreEmpleado (Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_COMPLETO_EMPLEADO,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static String getNombreEmpleado (Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_COMPLETO_EMPLEADO,"SinValor");
    }
    private static void deleteNombreEmpleado (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_COMPLETO_EMPLEADO).apply();

    }

    private static void setEstadoDeSesion (Activity activity, boolean valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putBoolean(NOMBRE_ESTADO_SESION,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static boolean getEstadDeSesion (Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getBoolean(NOMBRE_ESTADO_SESION,false);
    }
    private static void deleteEstadoSesion (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_ESTADO_SESION).apply();

    }


    private static void setFechaClientesConsulta (Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_FECHA_CLIENTES_CONSULTA,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static String getFechaClientesConsulta (Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_FECHA_CLIENTES_CONSULTA,"SinValor");
    }
    private static void deleteFechaClientesConsulta (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_FECHA_CLIENTES_CONSULTA).apply();

    }



    private static void setZonaVisitar (Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_ZONA,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static String getZonaVisitar (Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_ZONA,"SinValor");
    }
    private static void deleteZonaVisitar (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_ZONA).apply();

    }


    public static void setVersionInventario (Activity activity, int valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putInt(NOMBRE_VERSION_INVENTARIO,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static int getVersionInventario (Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getInt(NOMBRE_VERSION_INVENTARIO,0);
    }
    private static void deleteVersionInventario (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_VERSION_INVENTARIO).apply();

    }








    //FECHA de inicio sesion, cuando el usuarioSupervisor inicie sesion se guardara la fecha hora en que lo hiso "13-08-2019 14:21:40"
    //y se enviara esta hora junto con la cadena de mensajes de los clientes, esto porque si
    //el usuarioSupervisor cierra sesion y en la tabla ya tenian mensajes de clientes, si vuelve a iniciar sesion
    //se carga nuevamente la misma zona, y los mensajes nuevos se encimarian a los que ya estan ocacionando perdida de informacion
    //entonces cada inicio de sesion tendra una hora diferente la tabla del servidor los manejara y archivara en otro registro
    //separado del anterior

    private static void setFechaInicioSesion (Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_FECHA_HORA_INICIO_SESION,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static String getFechaInicioSesion(Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_FECHA_HORA_INICIO_SESION,"SinValor");
    }
    private static void deleteFechaInicioSesion (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_FECHA_HORA_INICIO_SESION).apply();

    }







    //(Dia DE INICIO DE SESION, ESTE METODO SOLO GUARDARA EL VALOR DE LA FECHA EN FORMATO dd-MM-yyyy un string
    // cuando se inicie sesion en el movil al igual que ocurre con la fechaInicioSesion guardara el dia en que se inicio
    // cuando la fecha actual del sistema sea mayor en 1 dia a la fecha en que se inicio sesion entonces el sistema
    // forzara un cierre de sesion para que al dia siguiente el usuarioSupervisor forsosamente tenga que cerrar sesion
    // esto para que se cargen los nuevos clientes, y que ademas se borren las tablas como los mensajes, los movimientos
    // y que se comience el dia de trabajo desde 0 y que no se tengan problemas que los movimientos del dia siguiente
    // se guarden con los clientes del dia anterior)

    private static void setDiaInicioSesion (Activity activity, String valor){

        SharedPreferences preferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);//para que solo la app kaliope pueda leer o escribir sobre el archivo


        SharedPreferences.Editor editor = preferences.edit(); // Declaramos una variable (objeto) de tipo SharedPreferences.Editor, necesario para guardar cambios en el fichero de preferencias.
        editor.putString(NOMBRE_DIA_INICIO_SESION,valor);
        //editor.commit();//guardamos nuestro fichero
        editor.apply();

    }
    public static String getDiaInicioSesion(Activity activity){
        SharedPreferences sharedPreferences =
                activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES, Context.MODE_PRIVATE);


        //(Por último llamamos al método "getString" para leer el valor de la preferencia "numeroRutaAsignada"
        // , si no encuentra este valor devolverá el valor por defecto noAsignado. para saber que no se encontro

        return sharedPreferences.getString(NOMBRE_DIA_INICIO_SESION,"SinValor");
    }
    private static void deleteDiaInicioSesion (Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(NOMBRE_ARCHIVO_CONFIGURACIONES,Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(NOMBRE_DIA_INICIO_SESION).apply();

    }



    //(Metodo para obtener el nombre cordo del empleado esto porque es el que aparecera en los mensajes de los clientes cuando dicten el audio de voz
    // lo que pasa es que si usamos el nombre de usuarioSupervisor, este podra tener numeros tambien para diferenciarlo de otros usuarios con el mismo nombre
    // por ejemplo gustavo9411, y nimodo que los cleintes digan yo maria entrego a gustavo9411, tampoco podemos usar su nombre completo
    // para eso mejor usaremos su nombre completo pero crearemos un metodo que cortara el nombre completo hasta el primer espacio, y eso retonrnaremos)

    public static String getNombreCorto(Activity activity){

        String nombreCompleto = getNombreEmpleado(activity);
        String nombreCorto = nombreCompleto.substring(0,nombreCompleto.indexOf(' '));
        return nombreCorto;

    }



    public static void setDatosInicioSesion (Activity activity, JSONArray jsonArray){

        for(int i=0; i<jsonArray.length(); i++){
            try {
                setNombreEmpleado(activity,jsonArray.getJSONObject(i).getString("nombre_empleado"));
                setUsuarioIniciado(activity,jsonArray.getJSONObject(i).getString("usuario"));
                setCodigoPulseraAsignada(activity,jsonArray.getJSONObject(i).getString("codigo_empleado_pulsera"));
                setRutaAsignada(activity,jsonArray.getJSONObject(i).getString("ruta_asignada"));
                setEstadoDeSesion(activity,true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        setFechaInicioSesion(activity,utilidadesApp.dameFehaHora());
        setDiaInicioSesion(activity,utilidadesApp.dameFecha());
    }

    public static void cerrarSesion (Activity activity){

        deleteNombreEmpleado(activity);
        deleteUsuarioIniciado(activity);
        deleteCodigoPulseraAsignada(activity);
        deleteRutaAsignada(activity);
        deleteEstadoSesion(activity);
        deleteFechaClientesConsulta(activity);
        deleteZonaVisitar(activity);
        deleteVersionInventario(activity);
        deleteFechaInicioSesion(activity);
        deleteDiaInicioSesion(activity);

    }


    //final VersionNameLuisda6.5










}
