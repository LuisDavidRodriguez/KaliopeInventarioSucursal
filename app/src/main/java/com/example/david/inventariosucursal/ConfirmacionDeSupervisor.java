package com.example.david.inventariosucursal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Vibrator;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class ConfirmacionDeSupervisor extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    ArrayList<HashMap> list = new ArrayList<HashMap>();



    TextView cantidadTotalTV,importeTotalTV,estadoConexionTV,mensajeAlUsuarioTV;
    CheckBox entradaCB,salidaCB;
    Button continuarB;
    ImageView carritoIV,flechaIV,carroIV,almacenIV;
    EditText passwordUsuarioET;



    String usuarioSupervisor = "";
    String nombreCompletoSupervisor = "";
    String passwordDeSupervisor = "";






    Transition transition;


    public static int piezasTotales;
    public static int importeTotal;
    DataBaseHelper dataBaseHelper = new DataBaseHelper (this);

    MediaPlayer player;
    Vibrator vibrator;

    Activity activity;
    ProgressDialog progressDialog;

    boolean continuarHiloHacerPing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacion_de_supervisor);
        activity = this;

        entradaCB = (CheckBox) findViewById(R.id.confirma_usuario_entradaCB1);
        salidaCB = (CheckBox) findViewById(R.id.confirma_usuario_salidaCB1);
        continuarB = (Button) findViewById(R.id.confirma_usuario_continuarB1);
        passwordUsuarioET = (EditText)findViewById(R.id.confirma_usuario_passwordET1);
        estadoConexionTV = (TextView)findViewById(R.id.confirmacion_movimiento_estadoConexionTV);
        mensajeAlUsuarioTV = (TextView)findViewById(R.id.confirmacion_mensajeAlAgente);


        entradaCB.setOnCheckedChangeListener(this);
        salidaCB.setOnCheckedChangeListener(this);

        String mensaje = Constant.NOMBRE_CORTO_DEL_AGENTE + "\n por favor dale este dispositivo a uno de tus compañeros para que revise tus piezas";
        mensajeAlUsuarioTV.setText(mensaje);



        //creamos una transicion de entrada
        Slide slide = new Slide (Gravity.END);
        slide.setDuration(Constant.DURATION_TRANSITION);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(false);

        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);






        //respondemos al evento de la tecla enter en el password del supervisor
        passwordUsuarioET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    String pass = passwordUsuarioET.getText().toString();
                    passwordUsuarioET.setText("");

                    if (salidaCB.isChecked()) {
                        Constant.TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR = Constant.SALIDA;
                    }
                    if (entradaCB.isChecked()) {
                        Constant.TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR = Constant.ENTRADA;
                    }

                    if(!pass.isEmpty()){



                        if (entradaCB.isChecked() || salidaCB.isChecked()){
                            if(Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE.equals(Constant.TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR)){


                                if(!pass.equals(Constant.PASSWORD_DEL_AGENTE)){
                                    enviarPasswordAlServidor(pass);

                                }else{
                                    Toast.makeText(activity, "La contraseña del supervisor no puede ser igual a la del agente", Toast.LENGTH_SHORT).show();
                                }

                            }else{

                                Toast.makeText(activity, "El tipo de movimiento que selecciono el agente y el supervisor son diferentes", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(activity, "No ha seleccionado que tipo de movimiento es", Toast.LENGTH_SHORT).show();
                        }



                    }else{
                        Toast.makeText(activity, "Por favor Ingrese un codigo", Toast.LENGTH_SHORT).show();
                    }

                    return true;
                }
                return false;            }
        });




        correrAnimacion();

        setListView(); //listamos nuestro listView





    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()){

            case R.id.confirma_usuario_entradaCB1:
                //permitimos solo una opcion
                if (entradaCB.isChecked() && salidaCB.isChecked()){
                    salidaCB.setChecked(false);
                }
                correrAnimacion();

                break;

            case R.id.confirma_usuario_salidaCB1:
                //para permintir solo una opcion habilitada
                if (salidaCB.isChecked() && entradaCB.isChecked()){
                    entradaCB.setChecked(false);
                }
                correrAnimacion();

                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        //registramso el receiver que recibira el estado de la red, aqui lo ponemos en el onCreate
        registerReceiver(networkStateReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        continuarHiloHacerPing = false;
        unregisterReceiver(networkStateReceiver);
    }

    /**LLernamos nuestro listview*/
    public void setListView (){
        ListView listView = (ListView) findViewById(R.id.alta_movimientoLV1);
        list = new ArrayList<HashMap>();

        AdapterListTwoItems adapterListTwoItems = new AdapterListTwoItems(list,this);
        listView.setAdapter(adapterListTwoItems);

        Cursor cursorMovimientoTemporal = dataBaseHelper.getAllProductTemporal();
        cursorMovimientoTemporal.moveToFirst();

        if (cursorMovimientoTemporal.getCount()> 0){
            HashMap encabezados = new HashMap();
            encabezados.put(Constant.UNO,"Precio");
            encabezados.put(Constant.DOS,"Cantidad");
            list.add(encabezados);

            do{
                HashMap items = new HashMap();
                items.put(Constant.UNO,cursorMovimientoTemporal.getString(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.PRECIO)));
                items.put(Constant.DOS,cursorMovimientoTemporal.getString(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CANTIDAD)));
                list.add(items);
            }while (cursorMovimientoTemporal.moveToNext());

        }

        calcularTotales();
    }

    /**Calculamos los totales*/
    public void calcularTotales (){
        this.importeTotal =0;
        this.piezasTotales = 0;
        cantidadTotalTV = (TextView) findViewById(R.id.alta_movimientoCantidadTV1);
        importeTotalTV = (TextView) findViewById(R.id.alta_movimientoImporteTV1);

        Cursor cursorMovimientoTemporal = dataBaseHelper.getAllProductTemporal();
        cursorMovimientoTemporal.moveToFirst();
        if (cursorMovimientoTemporal.getCount()>0){
            do{
                int piezas = cursorMovimientoTemporal.getInt(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CANTIDAD));
                int precioDistribucion = cursorMovimientoTemporal.getInt(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.PRECIO_DISTRIBUCION));
                int importeTemporal = piezas*precioDistribucion;
                this.importeTotal += importeTemporal;
                this.piezasTotales += piezas;
            }while (cursorMovimientoTemporal.moveToNext());


            cantidadTotalTV.setText(String.valueOf( this.piezasTotales));
            importeTotalTV.setText(String.valueOf(this.importeTotal));
        }

        cantidadTotalTV.setText(String.valueOf( this.piezasTotales));
        importeTotalTV.setText(String.valueOf(this.importeTotal));



    }


    private JSONArray getMovimientos(Activity activity){
        DataBaseHelper dataBaseHelper1 = new DataBaseHelper(activity);
        Cursor cursorMovimientoTemporal = dataBaseHelper1.getAllProductTemporal();
        cursorMovimientoTemporal.moveToFirst();

        JSONArray movimientos = new JSONArray();


        if (cursorMovimientoTemporal.getCount()> 0) {


            try {
                do {
                    JSONObject renglones = new JSONObject();
                    renglones.put("Codigo",cursorMovimientoTemporal.getString(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CODIGO)));
                    renglones.put("Cantidad",cursorMovimientoTemporal.getString(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CANTIDAD)));
                    movimientos.put(renglones);

                } while (cursorMovimientoTemporal.moveToNext());

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
        return movimientos;
    }











    public void correrAnimacion (){
        Animation movimientoCarritoAnim;

        carritoIV = (ImageView)findViewById(R.id.confirma_usuario_carritoTV1);
        flechaIV = (ImageView)findViewById(R.id.confirma_usuario_flechaIV1);
        carroIV = (ImageView)findViewById(R.id.confirma_usuario_carroIV1);
        almacenIV = (ImageView)findViewById(R.id.confirma_usuario_almacenIV1);



        if(entradaCB.isChecked()){


            flechaIV.setImageResource(R.drawable.felchaentrada);

            movimientoCarritoAnim = AnimationUtils.loadAnimation(this,R.anim.anim_entrada);
            carritoIV.startAnimation(movimientoCarritoAnim);
        }

        if (salidaCB.isChecked()){
            flechaIV.setImageResource(R.drawable.flechasalida);

            movimientoCarritoAnim = AnimationUtils.loadAnimation(this,R.anim.anim_salida);
            carritoIV.startAnimation(movimientoCarritoAnim);
        }

        if(!entradaCB.isChecked()&&!salidaCB.isChecked()){
            //si no hay ningun checkbox seleccionado balanceamos el carrito de un lado a otro
            flechaIV.setImageResource(R.drawable.flechaentradasalida);
            movimientoCarritoAnim = AnimationUtils.loadAnimation(this,R.anim.translate);
            carritoIV.startAnimation(movimientoCarritoAnim);
        }



    }







    public void enviarPasswordAlServidor(final String pass) {


        showProgresDialog(); //mostramos el progreso  indeterminado


        RequestParams parametros = new RequestParams();
        parametros.put("password", pass);




        KaliopeServerClient.post("app_kaliope/dispositivo_almacen_obtenerUsuarioPorPassword.php", parametros, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();

                try {
                    usuarioSupervisor = response.getString("usuario");
                    nombreCompletoSupervisor = response.getString("nombreCompleto");
                    passwordDeSupervisor = pass;

                    dialogoDeConfirmacionDatosUsuario();

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
                dialogoDeConexion("Error", responseString + "\nStatus Code: " + String.valueOf(statusCode));


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
                dialogoDeConexion("Fallo de conexion, no podemos conectarnos al servidor", info);
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

    private void dialogoDeConfirmacionDatosUsuario() {
        new AlertDialog.Builder(activity)
                .setTitle("Confirmacion de movimiento")

                .setMessage("Este movimiento afectara al inventario de: " +

                        "\n" + Constant.NOMBRE_DEL_AGENTE +
                        "\n\ny fue revisado por:\n"+ nombreCompletoSupervisor+
                        "\n\n ¿Estas seguro de que quieres terminar el movimiento?"
                        )



                .setPositiveButton("Terminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        enviarMovimientoAlServidor();
                    }
                })



                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
    }









    public void enviarMovimientoAlServidor() {


        showProgresDialog(); //mostramos el progreso  indeterminado


        RequestParams parametros = new RequestParams();
        parametros.put("alias", Constant.USUARIO_DEL_AGENTE);
        parametros.put("nombreCompleto", Constant.NOMBRE_DEL_AGENTE);
        parametros.put("aliasSupervisor", usuarioSupervisor);
        parametros.put("nombreSupervisor", nombreCompletoSupervisor);
        parametros.put("tipoMovimiento", Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE);
        parametros.put("usuarioSucursal",ConfiguracionesApp.getUsuarioIniciado(activity));
        parametros.put("totalPiezas", piezasTotales);
        parametros.put("totalImporte", importeTotal);
        parametros.put("movimientosJsonArray",getMovimientos(activity));







        KaliopeServerClient.post("app_kaliope/dispositivo_almacen_recibirMovimiento.php", parametros, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();

                //En la respuesta que nos envia el servidor una ves que modifique los inventarios pertinentes recibiremos esta informacion
                String piezasFinales = "";
                String versionFinal = "";

                try {
                    String mensaje = response.getString("estado");
                    piezasFinales = response.getString("piezasFinales");
                    versionFinal = response.getString("versionFinal");
                    Toast.makeText(ConfirmacionDeSupervisor.this, mensaje, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dataBaseHelper.deleteAllMovimientoTemporal();
                iniciarActividadSiguiente(piezasFinales,versionFinal);

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
                dialogoDeConexion("Error", responseString + "\nStatus Code: " + String.valueOf(statusCode));


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
                dialogoDeConexion("Fallo de conexion, no podemos conectarnos al servidor", info);
            }


            @Override
            public void onRetry(int retryNo) {
                progressDialog.setMessage("Reintentando conexion No: " + String.valueOf(retryNo));
            }
        });
    }



    @SuppressWarnings("unchecked")
    private void iniciarActividadSiguiente (String piezas, String version){

        player.start();
        vibrator.vibrate(400);


        transition = new Explode();
        transition.setDuration(Constant.DURATION_TRANSITION);
        transition.setInterpolator(new DecelerateInterpolator());
        getWindow().setExitTransition(transition);
        Intent intent = new Intent(this,Exito.class);
        intent.putExtra("piezas",piezas);
        intent.putExtra("version",version);
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());



    }



















    /**
     *Hacemos ping al servidor pero solamente para cambiar el estado del recuadro en la parte superior del activity
     * para informar al usuario si si hay conexion o no la hay
     */



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
                    estadoConexionTV.setText("Intentando Conexion");
                    estadoConexionTV.setBackgroundColor(Color.CYAN);


                }
                Log.d("Main",String.valueOf(networkInfo.getState()));
            }else{
                Log.d("Main","desconectado");
                continuarHiloHacerPing = false;
                estadoConexionTV.setText("WiFi o datos apagados");
                estadoConexionTV.setBackgroundColor(Color.LTGRAY);
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

                                        if(estadoConexionTV.getText().toString().equals("Conectado")){

                                            estadoConexionTV.setText("Conectado..");

                                        }else{

                                            estadoConexionTV.setText("Conectado");
                                        }

                                        estadoConexionTV.setBackgroundColor(Color.GREEN);
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


                                    if (estadoConexionTV.getText().toString().equals("Sin Conexion")){
                                        estadoConexionTV.setText("Intentando Conexion");
                                        estadoConexionTV.setBackgroundColor(Color.CYAN);
                                    }else{
                                        estadoConexionTV.setText("Sin Conexion");
                                        estadoConexionTV.setBackgroundColor(Color.YELLOW);
                                    }
                                    estadoConexionTV.setEnabled(false);
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





}
