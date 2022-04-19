package com.example.david.inventariosucursal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class IngresaActivity extends AppCompatActivity implements View.OnClickListener {


    private final int MY_PERMISSION_FINE_LOCATION_CODE = 10;

    Button btIngresar;
    EditText etUsuario,pwCredencial;
    TextView tvInformacion,tvVersionApp, tvEstadoConexion;
    ImageView ivlogotipoKaliope;
    RelativeLayout mainRelativeLayout;

    DataBaseHelper dbHelper = new DataBaseHelper(this);
    String m, t, userToken;



    ProgressDialog progressDialog;



    VariablePassword variablePassword;

    boolean continuarHiloHacerPing = true;






    //instanciamos nuestros objetos para sonido y vibracion
    SoundPool soundPool;
    int sonido;
    Vibrator vibrator;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        variablePassword = new VariablePassword();
        activity = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresa);



        if (    ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){

            Constant.PERMISOS_NECESARIOS_OTORGADOS = false;


        }else {
            Constant.PERMISOS_NECESARIOS_OTORGADOS = true;
            iniciarServiciosDependientesDePermisos();//iniciamos el servicio de localizacion y escribimos la carpeta donde estaran los datos en la memoria

        }







        soundPool = new SoundPool(8, AudioManager.STREAM_MUSIC,0);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);


        //instanciamos la vibracion
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);

        btIngresar = (Button) findViewById(R.id.btIngresar);

        etUsuario = (EditText) findViewById(R.id.mainUsuarioET);
        pwCredencial = (EditText) findViewById(R.id.pwCredencial);
        tvInformacion = (TextView) findViewById(R.id.numeroPulceraMainTV);
        tvVersionApp = (TextView) findViewById(R.id.textView2);
        ivlogotipoKaliope  =(ImageView) findViewById(R.id.imgLogoKaliope);
        tvEstadoConexion = (TextView) findViewById(R.id.mainEstadoConexionTv);

        ivlogotipoKaliope.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                return false;
            }
        });



        try{
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);

            tvVersionApp.setText("®Kaliope México 2018 Version:" + packageInfo.versionName);
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }




        etUsuario.setText("");
        pwCredencial.setText("");
        btIngresar.setOnClickListener(this);







        //(keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (


        //Manejo de la tecla "enter" en el edit text, esto para manejar el evento cuando
        //el escaner de codigo de barras ingresa el enter dijital despues de escanear
        //y tambien al presionar el boton "realizado" en el teclado numerico del celular
        pwCredencial.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){   //este era originalmente el fragmento encontrado en internet lo resumi a solo keycode_enter
                    //el problema con el resumido es que de alguna manera se llamaba 2 veces al evento porque tambien marcaba ActionDown entonces
                    //ejecutaba 2 veces el mismo metod y generaba errores, tube escribirlo de la manera original
                    //if (i == KeyEvent.KEYCODE_ENTER){//este es el resumido que puede llegar a ejecutar 2 veces el mismo codigo aqui no pasa porque llama al metodo ingresar que salta a otro activity
                    //Toast.makeText(getApplicationContext(),"tecla enter",Toast.LENGTH_SHORT).show();
                    //ingresar();
                    return true;
                }

                return false;
            }
        });

    }




    @Override
    public void onResume (){
        super.onResume();
        //Toast.makeText(this,"onResume",Toast.LENGTH_LONG).show();




        String mensaje  ="nombre: " + ConfiguracionesApp.getNombreEmpleado(activity) +
                "Estado Sesion: " + ConfiguracionesApp.getEstadDeSesion(activity) +
                "Ruta: " + ConfiguracionesApp.getRutaAsignada(activity)+
                "Pulsera: " + ConfiguracionesApp.getCodigoPulseraAsignada(activity) +
                "Usuario: " + ConfiguracionesApp.getUsuarioIniciado(activity);

        //tvInformacion.setText(mensaje);


        registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        tvEstadoConexion.setBackgroundColor(Color.RED);
        tvEstadoConexion.setText("Sin Internet");
        if (ConfiguracionesApp.getEstadDeSesion(activity)){
            ingresar();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkStateReceiver);
        continuarHiloHacerPing = false;
    }




    @Override
    public void onClick(View v) {

        //solo activamos la funcion de los botones cuando esta validada la app



        if(Constant.PERMISOS_NECESARIOS_OTORGADOS){
            iniciarServiciosDependientesDePermisos();

            //Activamos los botones
            switch (v.getId()) {

                case R.id.btIngresar:

                    if(etUsuario.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "No has ingresado un usuario", Toast.LENGTH_SHORT).show();
                    }else if (pwCredencial.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), "No has ingresado un Password", Toast.LENGTH_SHORT).show();
                    }else {

                        iniciarSesion();

                    }



                    break;

            }





        }else {
            solicitudDePermisos();
        }




    }



    private void iniciarServiciosDependientesDePermisos (){
        //para que puedamos de alguna manera comprobar que los permisos ya estan otorgados y llamar a este metodo para hacer ciertas cosas


        //escribimos en la memoria la carpeta
        Constant.INSTANCE_PATH = String.valueOf(Environment.getExternalStorageDirectory());

        File nuevaCarpeta = new File(Constant.INSTANCE_PATH, "mx.4103.klp");
        nuevaCarpeta.mkdirs();
        //Constant.INSTANCE_PATH = System.getenv("SECONDARY_STORAGE");
        //Constant.INSTANCE_PATH = android.os.Environment.DIRECTORY_DCIM;
        //Constant.INSTANCE_PATH = this.getExternalMediaDirs();

        Log.d("dbg-GED","Path: " + Constant.INSTANCE_PATH);

    }



    private void ingresar (){

        sonido = soundPool.load(getApplicationContext(),R.raw.exito,1);
        soundPool.play(sonido,1,1,0,0,1);
        vibrator.vibrate(400);


        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }





    /**
     * Solicitar permisos de Geolocalizacion
     * */


    void solicitudDePermisos(){

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE},MY_PERMISSION_FINE_LOCATION_CODE);


    }


    //llamamos a nuestro manejador de eventos que se invoca cuando el usuarioSupervisor responde al permiso
    //el Callback recibe el mismo codigo de solicitud que le pasaste a requestPermissions()
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //VersionNameLuisda6.5 READ_PHONE_STATE


        if (    ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){

            Constant.PERMISOS_NECESARIOS_OTORGADOS = true;
            startActivity(new Intent(this,MainActivity.class));

        }else {
            Constant.PERMISOS_NECESARIOS_OTORGADOS = false;
        }

    }






    @Override
    protected void onStop() {
        super.onStop();
        //finish();
    }



    //coneccion al servidor
    public void iniciarSesion (){
        String uniqueUUID;
        if (ConfiguracionesApp.getCodigoUnicoDispositivo(activity).equals("SinValor")){
            //si aun no se guarda en las preferencias el UUID lo creamos y lo guardamos.
            uniqueUUID  = UUID.randomUUID().toString();
            Log.d("ID","UUID ID: " + uniqueUUID);
            ConfiguracionesApp.setCodigoDispositivoUnico(activity,uniqueUUID);
        }else{
            //corroboramos que el UUID se haya guardado en las preferencias
            uniqueUUID = ConfiguracionesApp.getCodigoUnicoDispositivo(activity);
            Log.d("ID","UUID ID preferences: " + uniqueUUID);
        }


        showProgresDialog(); //mostramos el progreso  indeterminado


        RequestParams parametros = new RequestParams();
        parametros.put("alias" , etUsuario.getText().toString());
        parametros.put("password" , pwCredencial.getText().toString());
        parametros.put("modeloDispositivo" , Build.MODEL);
        parametros.put("UUID" , uniqueUUID);


        KaliopeServerClient.get("app_kaliope/iniciar_sesion_dispositivo_almacen.php",parametros,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();
                //el servidor nos enviara como respuesta un jsonObjet, dentro de ese jsonObjet
                //enviaremos un jsonArray que es el inventario y otro jsonAray el catalogo de clientes
                //tambien podemos enviar solo objetos!!!
                //http://thebestandroide.blogspot.com/2014/11/crear-y-leer-json-desde-android.html?m=1
                //https://support.brightcove.com/es/concepts-introducing-json
                //solo basta entender la forma en como se construllen los json



                try {
                    JSONArray inventario = response.getJSONArray("inventario");
                    JSONObject informacion = response.getJSONObject("informacion");
                    JSONArray infoUsuario = response.getJSONArray("infoUsuario");


                    Log.d("Inventario",String.valueOf(inventario.length()));
                    Log.d("Informacion",String.valueOf(informacion.getString("id")));


                    Toast.makeText(IngresaActivity.this, informacion.getString("id"), Toast.LENGTH_LONG).show();

                    Inventario inventarioActivity = new Inventario();
                    inventarioActivity.llenarInventarioDesdeJsonArray(inventario,activity);


                    //llenamos los datos de uso de sesion
                    ConfiguracionesApp.setDatosInicioSesion(activity,infoUsuario);
                    ingresar();

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


                String info = "Status Code: " + String.valueOf(statusCode) +"  responseString: " + responseString;
                Log.d("onFauile 1" , info);
                //Toast.makeText(MainActivity.this,responseString + "  Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                dialogoDeConexion("Fallo de inicio de sesion", responseString + "\nStatus Code: " + String.valueOf(statusCode));


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


                String info = "StatusCode" + String.valueOf(statusCode) +"  Twhowable:   "+  throwable.toString();
                Log.d("onFauile 2" , info);
                //Toast.makeText(MainActivity.this,info, Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                dialogoDeConexion("Fallo de conexion", info);
            }


            @Override
            public void onRetry(int retryNo) {
                progressDialog.setMessage("Reintentando conexion No: " + String.valueOf(retryNo));
            }
        });
    }

    private void showProgresDialog(){

        progressDialog = new ProgressDialog(IngresaActivity.this);
        progressDialog.setMessage("Conectando al Servidor");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    void dialogoDeConexion (String title,String mensaje){
        new AlertDialog.Builder(this)
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







    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            //https://medium.com/alvareztech/verificar-estado-de-conexi%C3%B3n-a-internet-en-tu-aplicaci%C3%B3n-android-d55e2b501302

            if(networkInfo != null && networkInfo.isConnected()){
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    Log.d("Main","conectado");

                    hacerPingAlServidor();
                    tvEstadoConexion.setText("Intentando Conexion");
                    tvEstadoConexion.setBackgroundColor(Color.CYAN);


                }
                Log.d("Main",String.valueOf(networkInfo.getState()));
            }else{
                Log.d("Main","desconectado");
                continuarHiloHacerPing = false;
                tvEstadoConexion.setText("WiFi o datos apagados\nActivalos");
                tvEstadoConexion.setBackgroundColor(Color.RED);
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
                                        tvEstadoConexion.setText("Conectado");
                                        tvEstadoConexion.setEnabled(true);
                                        tvEstadoConexion.setBackgroundColor(Color.GREEN);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
                                    if (tvEstadoConexion.getText().toString().equals("Sin Conexion al servidor\nRevisa WiFi")){
                                        tvEstadoConexion.setText("Sin Conexion al servidor\nRevisa WiFi.");
                                        tvEstadoConexion.setBackgroundColor(Color.CYAN);
                                    }else{
                                        tvEstadoConexion.setText("Sin Conexion al servidor\nRevisa WiFi");
                                        tvEstadoConexion.setBackgroundColor(Color.YELLOW);
                                    }
                                    String info = "StatusCode" + String.valueOf(statusCode) + "  Twhowable:   " + throwable.toString();
                                    Log.d("onFauile 2", info);
                                    //Toast.makeText(getApplicationContext(), "Falla en Ping Status Code: " + String.valueOf(statusCode) , Toast.LENGTH_LONG).show();


                                }

                            });



                        }
                    });

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        }).start();

    }






}
