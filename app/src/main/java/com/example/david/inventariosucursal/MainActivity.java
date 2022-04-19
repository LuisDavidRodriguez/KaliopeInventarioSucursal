package com.example.david.inventariosucursal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.david.inventariosucursal.App;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{




    private DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    private ScanBroadcastReceiver scanBroadcastReceiver;
    TextView numeroSemanaTV;

    TextView tvConectando;
    TextView tvEstadoSincronizacion;
    ProgressDialog progressDialog;
    Activity activity;

    private boolean continuarHiloHacerPing = true;

    Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        ImageButton inventario = (ImageButton) findViewById(R.id.mainButtonInventario);
        ImageButton movimiento = (ImageButton) findViewById(R.id.mainNuevoButtonNuevoMovimiento);

        numeroSemanaTV = (TextView) findViewById(R.id.main_numeroSemanaTV);
        tvConectando = (TextView) findViewById(R.id.menuPrincipalEstadoConexiontv);
        //tvEstadoSincronizacion = (TextView) findViewById(R.id.menuPrincipalEstadoDeSincronizacion);


        //creamos una transicion de entrada
        Explode explode = new Explode();
        explode.setDuration(Constant.DURATION_TRANSITION);
        explode.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(explode);
        getWindow().setAllowEnterTransitionOverlap(false);






        tvConectando.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialogoCierreDeSesion();

                return false;
            }
        });

        inventario.setOnClickListener(this);
        movimiento.setOnClickListener(this);


        Log.i("Model", Build.MODEL);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Date date = new Date();
        Log.i ("Hora actual", String.valueOf(date.getTime()));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String fecha = simpleDateFormat.format(date);
        Log.i ("Hora actual", String.valueOf(fecha));


        numeroSemanaTV.setText( "Al " + ConfiguracionesApp.getNombreEmpleado(activity) + "\nNumero semana: " + String.valueOf(utilidadesApp.getNumeroSemana()));

        scanBroadcastReceiver = new ScanBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.KEY_INTENTFILTER_ESCANER);
        this.registerReceiver(scanBroadcastReceiver,intentFilter);

        //Registramos el receiber del escuchador de estado de la red
        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }


    @Override
    public void onClick(View view) {
        final File file = new File(Constant.RUTA_MEMORIA_DISPOSITIVO,Constant.NOMBRE_CARPETA);
        switch (view.getId()){

            case R.id.mainNuevoButtonNuevoMovimiento:

                Cursor getAllInventory = dataBaseHelper.getAllInventory();

                    if (getAllInventory.getCount()>0){

                            Intent intent1 = new Intent(this, AltaMovimiento.class);
                            startActivity(intent1);

                    }else {
                        Toast.makeText(this,"No existe ningun inventario en el dispositivo",Toast.LENGTH_LONG).show();
                    }



                break;


            case R.id.mainButtonInventario:
//                //comprobamos que haya inventario en la base de datos, o si no hay que exista el documento txt
////                Cursor cursorAllInventory = dataBaseHelper.getAllInventory();
////                File txtInventario = new File(file + "/inventario.txt");
////                Intent intent = new Intent(this,Inventario.class);
////
////                if (txtInventario.isFile() || cursorAllInventory.getCount() > 0){
////                    startActivity(intent);
////                }else {
////                    Toast.makeText(this,"No existe Inventario",Toast.LENGTH_LONG).show();
////                }

                Toast.makeText(getApplicationContext(), "Funcion no disponible", Toast.LENGTH_SHORT).show();
                break;




        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(scanBroadcastReceiver);
        unregisterReceiver(networkStateReceiver);
        continuarHiloHacerPing = false;


    }

    @Override
    protected void onDestroy() {


        App.closeCommonApi();
        System.exit(0);
        super.onDestroy();


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(activity, "Para salir de la aplicacion presione el boton de home", Toast.LENGTH_LONG).show();
    }

    private void dialogoCierreDeSesion() {
        new AlertDialog.Builder(activity)
                .setTitle("Confirmacion")
                .setMessage(ConfiguracionesApp.getUsuarioIniciado(activity) + " estas seguro de que quieres cerrar tu sesion?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cerrarSesion();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        })
                .create().show();
    }


    public void cerrarSesion() {

        final Inventario inventarioActivity = new Inventario();



        showProgresDialog(); //mostramos el progreso  indeterminado


        RequestParams parametros = new RequestParams();
        parametros.put("alias", ConfiguracionesApp.getUsuarioIniciado(activity));
        parametros.put("fechaHoraInicioSesion", ConfiguracionesApp.getFechaInicioSesion(activity));
        parametros.put("UUID", ConfiguracionesApp.getCodigoUnicoDispositivo(activity));
        //parametros.put("versionInventario",ConfiguracionesApp.getVersionInventario(activity));
        //parametros.put("inventario",inventarioActivity.enviarInventarioPorJsonArray(activity));
        //parametros.put("mensajesClientes",altaMovimientoActivity.enviarMovimientosJsonObjet(dbHelper,activity));



        KaliopeServerClient.post("app_kaliope/cerrar_sesion_dispositivo_almacen.php", parametros, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();

                try {
                    Toast.makeText(getApplicationContext(), response.getString("resultado"), Toast.LENGTH_SHORT).show();


                    ConfiguracionesApp.cerrarSesion(activity);
                    startActivity(new Intent (getApplicationContext(),IngresaActivity.class));
                    finishAffinity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //cuando por se recibe como respuesta un objeto que no puede ser convertido a jsonData
                //es decir si se conecta al servidor, pero desde el retornamos un echo de error
                //con un simple String lo recibimos en este metodo, crei que lo recibiria en el metodo onSUcces que tiene como parametro el responseString
                //pero parese que no, lo envia a este onFaiulure con Status Code

                //Si el nombre del archivo php esta mal para el ejemplo el correcto es: comprobar_usuario_app_kaliope.php
                // y el incorrecto es :comprobar_usuario_app_kaliop.php se llama a este metodo y entrega el codigo 404
                //lo que imprime en el log es un codigo http donde dice que <h1>Object not found!</h1>
                //            <p>
                //
                //
                //                The requested URL was not found on this server.
                //
                //
                //
                //                If you entered the URL manually please check your
                //                spelling and try again.
                //es decir si se encontro conexion al servidor y este respondio con ese mensaje
                //tambien si hay errores con alguna variable o algo asi, en este medio retorna el error como si lo viernas en el navegador
                //te dice la linea del error etc.


                String info = "Status Code: " + String.valueOf(statusCode) + "  responseString: " + responseString;
                Log.d("onFauile 1", info);
                //Toast.makeText(MainActivity.this,responseString + "  Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                dialogoDeConexion("Fallo al cerrar la sesion", responseString + "\nStatus Code: " + String.valueOf(statusCode));


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                //cuando no se ha podido conectar con el servidor el statusCode=0 cz.msebera.android.httpclient.conn.ConnectTimeoutException: Connect to /192.168.1.10:8080 timed out
                //para simular esto estoy en un servidor local, obiamente el celular debe estar a la misma red, lo desconecte y lo movi a la red movil

                //cuando no hay coneccion a internet apagados datos y wifi se llama al metodo retry 5 veces y arroja la excepcion:
                // java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)


                //Si la url principal del servidor esta mal para simularlo cambiamos estamos a un servidor local con:
                //"http://192.168.1.10:8080/KALIOPE/" cambiamos la ip a "http://192.168.1.1:8080/KALIOPE/";
                //se llama al onRetry 5 veces y se arroja la excepcion en el log:
                //estatus code: 0 java.net.ConnectException: failed to connect to /192.168.1.1 (port 8080) from /192.168.1.71 (port 36134) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //no hay ruta al Host

                //Si desconectamos el servidor de la ip antes la ip en el servidor de la computadora era 192.168.1.10, lo movimos a 192.168.1.1
                //genera lo mismo como si cambiaramos la ip en el programa android la opcion dew arriba. No
                //StatusCode0  Twhowable:   java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /192.168.1.71 (port 37786) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //Llamo a reatry 5 veces


                String info = "StatusCode" + String.valueOf(statusCode) + "  Twhowable:   " + throwable.toString();
                Log.d("onFauile 2", info);
                //Toast.makeText(MainActivity.this,info, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                dialogoDeConexion("Fallo de conexion, no podemos cerrar sesion", info);
            }


            @Override
            public void onRetry(int retryNo) {
                progressDialog.setMessage("Reintentando conexion No: " + String.valueOf(retryNo));
            }
        });
    }

    private void showProgresDialog() {

        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Conectando al Servidor");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    private void dialogoDeConexion(String title, String mensaje) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }



    private void sincronizarDatos (){

        final Inventario inventarioActivity = new Inventario();
        //final AltaMovimientoActivity altaMovimientoActivity = new AltaMovimientoActivity();

        RequestParams parametros = new RequestParams();
        parametros.put("alias", ConfiguracionesApp.getUsuarioIniciado(activity));
        parametros.put("fechaHoraInicioSesion", ConfiguracionesApp.getFechaInicioSesion(activity));
        parametros.put("UUID", ConfiguracionesApp.getCodigoUnicoDispositivo(activity));
        parametros.put("versionInventario",ConfiguracionesApp.getVersionInventario(activity));
        parametros.put("inventario",inventarioActivity.enviarInventarioPorJsonArray(activity));


        KaliopeServerClient.post("app_kaliope/cerrar_sesion_dispositivo_almacen.php", parametros, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    Toast.makeText(getApplicationContext(), response.getString("resultado"), Toast.LENGTH_SHORT).show();
                    Constant.ULTIMOS_DATOS_SINCRONIZADOS = true;
                    //tvEstadoSincronizacion.setText("Ultimos datos Sincronizados");
                    //tvEstadoSincronizacion.setBackgroundColor(Color.GREEN);
                    vibrator.vibrate(500);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //cuando por se recibe como respuesta un objeto que no puede ser convertido a jsonData
                //es decir si se conecta al servidor, pero desde el retornamos un echo de error
                //con un simple String lo recibimos en este metodo, crei que lo recibiria en el metodo onSUcces que tiene como parametro el responseString
                //pero parese que no, lo envia a este onFaiulure con Status Code

                //Si el nombre del archivo php esta mal para el ejemplo el correcto es: comprobar_usuario_app_kaliope.php
                // y el incorrecto es :comprobar_usuario_app_kaliop.php se llama a este metodo y entrega el codigo 404
                //lo que imprime en el log es un codigo http donde dice que <h1>Object not found!</h1>
                //            <p>
                //
                //
                //                The requested URL was not found on this server.
                //
                //
                //
                //                If you entered the URL manually please check your
                //                spelling and try again.
                //es decir si se encontro conexion al servidor y este respondio con ese mensaje
                //tambien si hay errores con alguna variable o algo asi, en este medio retorna el error como si lo viernas en el navegador
                //te dice la linea del error etc.


                String info = "Status Code: " + String.valueOf(statusCode) + "  responseString: " + responseString;
                Log.d("onFauile 1", info);
                //Toast.makeText(MainActivity.this,responseString + "  Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                //cuando no se ha podido conectar con el servidor el statusCode=0 cz.msebera.android.httpclient.conn.ConnectTimeoutException: Connect to /192.168.1.10:8080 timed out
                //para simular esto estoy en un servidor local, obiamente el celular debe estar a la misma red, lo desconecte y lo movi a la red movil

                //cuando no hay coneccion a internet apagados datos y wifi se llama al metodo retry 5 veces y arroja la excepcion:
                // java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)


                //Si la url principal del servidor esta mal para simularlo cambiamos estamos a un servidor local con:
                //"http://192.168.1.10:8080/KALIOPE/" cambiamos la ip a "http://192.168.1.1:8080/KALIOPE/";
                //se llama al onRetry 5 veces y se arroja la excepcion en el log:
                //estatus code: 0 java.net.ConnectException: failed to connect to /192.168.1.1 (port 8080) from /192.168.1.71 (port 36134) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //no hay ruta al Host

                //Si desconectamos el servidor de la ip antes la ip en el servidor de la computadora era 192.168.1.10, lo movimos a 192.168.1.1
                //genera lo mismo como si cambiaramos la ip en el programa android la opcion dew arriba. No
                //StatusCode0  Twhowable:   java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /192.168.1.71 (port 37786) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //Llamo a reatry 5 veces


                String info = "StatusCode" + String.valueOf(statusCode) + "  Twhowable:   " + throwable.toString();
                Log.d("onFauile 2", info);
                //Toast.makeText(MainActivity.this,info, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                dialogoDeConexion("Fallo de conexion", info);
            }

        });

    }


    private void compararVersionesDatosServidor (){
        /*se llama a este metodo que se encarga de revizar si hay algun cambio
         * entre las versiones del inventario de los agentes, si no hay cambios de versiones
         * el movil envia su datos al servidor, si hay cambio en la versiones el servidor
         * envia el inventario al movil*/

        final Inventario inventarioActivity = new Inventario();

        RequestParams parametros = new RequestParams();
        parametros.put("alias", ConfiguracionesApp.getUsuarioIniciado(activity));
        parametros.put("versionInventario",ConfiguracionesApp.getVersionInventario(activity));


        KaliopeServerClient.post("app_kaliope/comparar_versiones_datos_servidor.php", parametros, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray inventario = response.getJSONArray("inventario");

                    inventarioActivity.llenarInventarioDesdeJsonArray(inventario,activity);
                    Toast.makeText(getApplicationContext(), "Se a cargado una nueva version de inventario desde el servidor", Toast.LENGTH_SHORT).show();
                    vibrator.vibrate(1500);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //cuando por se recibe como respuesta un objeto que no puede ser convertido a jsonData
                //es decir si se conecta al servidor, pero desde el retornamos un echo de error
                //con un simple String lo recibimos en este metodo, crei que lo recibiria en el metodo onSUcces que tiene como parametro el responseString
                //pero parese que no, lo envia a este onFaiulure con Status Code

                //Si el nombre del archivo php esta mal para el ejemplo el correcto es: comprobar_usuario_app_kaliope.php
                // y el incorrecto es :comprobar_usuario_app_kaliop.php se llama a este metodo y entrega el codigo 404
                //lo que imprime en el log es un codigo http donde dice que <h1>Object not found!</h1>
                //            <p>
                //
                //
                //                The requested URL was not found on this server.
                //
                //
                //
                //                If you entered the URL manually please check your
                //                spelling and try again.
                //es decir si se encontro conexion al servidor y este respondio con ese mensaje
                //tambien si hay errores con alguna variable o algo asi, en este medio retorna el error como si lo viernas en el navegador
                //te dice la linea del error etc.


                String info = "Status Code: " + String.valueOf(statusCode) + "  responseString: " + responseString;
                Log.d("onFauile 1", info);
                //Toast.makeText(MainActivity.this,responseString + "  Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                //cuando no se ha podido conectar con el servidor el statusCode=0 cz.msebera.android.httpclient.conn.ConnectTimeoutException: Connect to /192.168.1.10:8080 timed out
                //para simular esto estoy en un servidor local, obiamente el celular debe estar a la misma red, lo desconecte y lo movi a la red movil

                //cuando no hay coneccion a internet apagados datos y wifi se llama al metodo retry 5 veces y arroja la excepcion:
                // java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)


                //Si la url principal del servidor esta mal para simularlo cambiamos estamos a un servidor local con:
                //"http://192.168.1.10:8080/KALIOPE/" cambiamos la ip a "http://192.168.1.1:8080/KALIOPE/";
                //se llama al onRetry 5 veces y se arroja la excepcion en el log:
                //estatus code: 0 java.net.ConnectException: failed to connect to /192.168.1.1 (port 8080) from /192.168.1.71 (port 36134) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //no hay ruta al Host

                //Si desconectamos el servidor de la ip antes la ip en el servidor de la computadora era 192.168.1.10, lo movimos a 192.168.1.1
                //genera lo mismo como si cambiaramos la ip en el programa android la opcion dew arriba. No
                //StatusCode0  Twhowable:   java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /192.168.1.71 (port 37786) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                //Llamo a reatry 5 veces


                String info = "StatusCode" + String.valueOf(statusCode) + "  Twhowable:   " + throwable.toString();
                Log.d("onFauile 2", info);
                //Toast.makeText(MainActivity.this,info, Toast.LENGTH_LONG).show();
                dialogoDeConexion("Fallo de conexion", info);
            }

        });

    }









    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //https://medium.com/alvareztech/verificar-estado-de-conexi%C3%B3n-a-internet-en-tu-aplicaci%C3%B3n-android-d55e2b501302

            if (networkInfo!= null){
                Log.d("Main conexionDetail",String.valueOf(networkInfo.getDetailedState()));
                Log.d("Main conexionExtras",String.valueOf(networkInfo.getExtraInfo()));
                Log.d("Main conexionType",String.valueOf(networkInfo.getType()));
                Log.d("Main conexionSubType",String.valueOf(networkInfo.getSubtype()));
            }


            if(networkInfo != null && networkInfo.isConnected()){
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    Log.d("Main","conectado");

                    hacerPingAlServidor();
                    tvConectando.setText("Intentando Conexion");
                    tvConectando.setBackgroundColor(Color.CYAN);


                }
                Log.d("Main",String.valueOf(networkInfo.getState()));
            }else{
                Log.d("Main","desconectado");
                continuarHiloHacerPing = false;
                tvConectando.setText("WiFi o datos apagados");
                tvConectando.setBackgroundColor(Color.LTGRAY);
            }

        }
    };



    public void hacerPingAlServidor(){
        continuarHiloHacerPing = true;

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(continuarHiloHacerPing){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            KaliopeServerClient.postNumeroIntentosTimeOut("app_kaliope/ping_servidor.php", null, new JsonHttpResponseHandler() {


                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                    try {
                                        String estado = response.getString("estado");
                                        //Toast.makeText(MenuPrincipalActivity.this, estado, Toast.LENGTH_SHORT).show();

                                        if(tvConectando.getText().toString().equals("Conectado")){

                                            tvConectando.setText("Conectado..");

                                        }else{

                                            tvConectando.setText("Conectado");
                                        }
                                        tvConectando.setBackgroundColor(Color.GREEN);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    compararVersionesDatosServidor();

//                                    //SI HA HABIDO CAMBIOS EN MOVIMIENTOS INVENTARIO O CLIENTES SINCRONIZAMOS LOS DATOS
                                    //EN LOS DISPOSITIVOS DE ALMACEN YA NO SINCRONIZAMOS LOS DATOS PUESTO QUE TRABAJAN AL 100% EN RED
                                    //(QUIERE DECIR QUE SU INVENTARIO SOLO ES VISUAL, NO REALIZA NINGUNA OPERACION QUE EDITE SU INVENTARIO
                                    // ENVIA LOS DATOS AL SERVIDOR Y ESTE SE ENCARGA DE MODIFICAR LOS INVENTARIOS. AL IGUAL DE LOS MOVIMIENTOS
                                    // NO LLEVA HISTORIAL DE MOVIMIENTOS)
//                                    if(!Constant.ULTIMOS_DATOS_SINCRONIZADOS){
//                                        sincronizarDatos();
//                                    }



                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                                    //cuando no se ha podido conectar con el servidor el statusCode=0 cz.msebera.android.httpclient.conn.ConnectTimeoutException: Connect to /192.168.1.10:8080 timed out
                                    //para simular esto estoy en un servidor local, obiamente el celular debe estar a la misma red, lo desconecte y lo movi a la red movil

                                    //cuando no hay coneccion a internet apagados datos y wifi se llama al metodo retry 5 veces y arroja la excepcion:
                                    // java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /:: (port 0) after 10000ms: connect failed: ENETUNREACH (Network is unreachable)


                                    //Si la url principal del servidor esta mal para simularlo cambiamos estamos a un servidor local con:
                                    //"http://192.168.1.10:8080/KALIOPE/" cambiamos la ip a "http://192.168.1.1:8080/KALIOPE/";
                                    //se llama al onRetry 5 veces y se arroja la excepcion en el log:
                                    //estatus code: 0 java.net.ConnectException: failed to connect to /192.168.1.1 (port 8080) from /192.168.1.71 (port 36134) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                                    //no hay ruta al Host

                                    //Si desconectamos el servidor de la ip antes la ip en el servidor de la computadora era 192.168.1.10, lo movimos a 192.168.1.1
                                    //genera lo mismo como si cambiaramos la ip en el programa android la opcion dew arriba. No
                                    //StatusCode0  Twhowable:   java.net.ConnectException: failed to connect to /192.168.1.10 (port 8080) from /192.168.1.71 (port 37786) after 10000ms: isConnected failed: EHOSTUNREACH (No route to host)
                                    //Llamo a reatry 5 veces


                                    if (tvConectando.getText().toString().equals("Sin Conexion")){
                                        tvConectando.setText("Intentando Conexion");
                                        tvConectando.setBackgroundColor(Color.CYAN);
                                    }else{
                                        tvConectando.setText("Sin Conexion");
                                        tvConectando.setBackgroundColor(Color.YELLOW);
                                    }
                                    tvConectando.setEnabled(false);
                                    String info = "StatusCode" + String.valueOf(statusCode) + "  Twhowable:   " + throwable.toString();
                                    Log.d("onFauile 2", info);
                                    //Toast.makeText(getApplicationContext(), "Falla en Ping Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();


                                }


                            });





                        }
                    });

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();

    }








    class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //String text1 = intent.getExtras().getString("code");
            //Toast.makeText(getApplicationContext(),text1,Toast.LENGTH_SHORT).show();
        }
    }

}
