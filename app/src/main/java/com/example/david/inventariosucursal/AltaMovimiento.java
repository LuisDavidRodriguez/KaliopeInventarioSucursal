package com.example.david.inventariosucursal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class AltaMovimiento extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    ArrayList <HashMap> list = new ArrayList<HashMap>();


    Button eliminarTodoB;
    EditText codigoET;
    TextView cantidadTotalTV,importeTotalTV,estadoConexionTV;
    LinearLayout linearLayout;
    CheckBox entradaCB,salidaCB;
    Button continuarB;
    ImageView carritoIV,flechaIV,carroIV,almacenIV;
    TextView tituloTipoMovTV,intruccionesTV, pasoTipoMovTV;
    EditText passwordUsuarioET;
    LinearLayout layout;




    String usuario = "";
    String nombreCompleto = "";
    String passwordDeUsuario = "";

    Transition transition;


    public static int piezasTotales;
    public static int importeTotal;
    DataBaseHelper dataBaseHelper = new DataBaseHelper (this);

    boolean botonEliminarEstaPresionado = false;

    MediaPlayer player;
    Vibrator vibrator;

    Activity activity;
    ProgressDialog progressDialog;

    private boolean continuarHiloHacerPing = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_movimiento);
        activity = this;
        codigoET = (EditText) findViewById(R.id.alta_movimientoCodigoET);
        eliminarTodoB = (Button) findViewById(R.id.alta_movimientoEliminarB);
        linearLayout = findViewById(R.id.alta_movimientoLayout);
        entradaCB = (CheckBox) findViewById(R.id.confirma_usuario_entradaCB);
        salidaCB = (CheckBox) findViewById(R.id.confirma_usuario_salidaCB);
        continuarB = (Button) findViewById(R.id.confirma_usuario_continuarB);
        tituloTipoMovTV = (TextView) findViewById(R.id.confirma_usuario_tituloTV);
        intruccionesTV=(TextView)findViewById(R.id.confirma_usuario_instruccionesPulseraTV);
        pasoTipoMovTV = (TextView)findViewById(R.id.confirma_usuario_pasoTV);
        estadoConexionTV = (TextView)findViewById(R.id.alta_movimiento_estadoConexionTV);
        passwordUsuarioET = (EditText)findViewById(R.id.confirma_usuario_passwordET);
        layout = (LinearLayout) findViewById(R.id.confirma_usuario_Layout);



        continuarB.setOnClickListener(this);
        entradaCB.setOnCheckedChangeListener(this);
        salidaCB.setOnCheckedChangeListener(this);
        eliminarTodoB.setOnClickListener(this);



        //creamos una transicion de entrada
        Slide slide = new Slide (Gravity.END);
        slide.setDuration(Constant.DURATION_TRANSITION);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(false);

        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);







/*Aqui usabamos el boton de eliminar en la pantalla pero ya lo borramos es lo que esta comentado usamos el boton fisico de funcion*/
//        eliminarProductoB.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                String accion = String.valueOf(motionEvent.getAction());
//                Log.i("accionOTouchint",accion);
//
//                switch (motionEvent.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        if (!botonEliminarEstaPresionado){
//                            botonEliminarEstaPresionado = true;
//                            linearLayout.setBackgroundResource(R.color.colorEliminar);
//                            player = MediaPlayer.create(getApplicationContext(), R.raw.exito);
//                        }
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        botonEliminarEstaPresionado = false;
//                        linearLayout.setBackgroundColor(Color.TRANSPARENT);
//                        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
//
//
//                        break;
//
//
//                }
//                return true;
//            }
//        });




        codigoET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {


                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    //respondemos al evento de la tecla enter, que envia el escaner en automatico
                    String codigo = codigoET.getText().toString();
                    codigoET.setText("");//limpiamos el editText
                    solicitarFoco();


                    if (!TextUtils.isEmpty(codigo)){
                        recibeCodigo(codigo);

                    }else {
                        Toast.makeText(getBaseContext(),"Se necesita un codigo",Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
                return false;
            }

        });





        //respondemos al evento de la tecla enter en el password del usuarioSupervisor
        passwordUsuarioET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {


                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){

                    String pass = passwordUsuarioET.getText().toString();
                    passwordUsuarioET.setText("");


                    if(!pass.isEmpty()){

                        if (entradaCB.isChecked() || salidaCB.isChecked()){
                            enviarPasswordAlServidor(pass);

                        }else{
                            Toast.makeText(activity, "No ha seleccionado que tipo de movimiento es", Toast.LENGTH_SHORT).show();
                        }



                    }else{
                        Toast.makeText(activity, "Por favor Ingrese un codigo", Toast.LENGTH_SHORT).show();
                    }




                    return true;
                }
                return false;
            }
        });




        correrAnimacion();

        setListView(); //listamos nuestro listView





    }


    @Override
    protected void onResume() {
        super.onResume();
        //registramos nuestro intentFilter para cachar el broadcast del escaner
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.KEY_INTENTFILTER_ESCANER);
        this.registerReceiver(broadcastReceiver,intentFilter);

        //registramos Receiver del estado de la red
        registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
        continuarHiloHacerPing = false;
        unregisterReceiver(networkStateReceiver);

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);


        Log.i("kEY Up Code",String.valueOf(keyCode));


        switch (keyCode) {

            case KeyEvent.KEYCODE_F3:
                //Toast.makeText(getBaseContext(),"telca funcion pulsada",Toast.LENGTH_SHORT).show();
                linearLayout.setBackgroundResource(R.color.colorEliminar);

                Log.i("event", String.valueOf(event.getAction()));

                botonEliminarEstaPresionado = false;
                linearLayout.setBackgroundColor(Color.TRANSPARENT);
                player = MediaPlayer.create(getApplicationContext(), R.raw.beep);


                break;


        }

       return true;

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);


        Log.i("kEY Down Code",String.valueOf(keyCode));

        switch (keyCode){
            case KeyEvent.KEYCODE_F3:
                //Toast.makeText(getBaseContext(),"telca funcion pulsada",Toast.LENGTH_SHORT).show();

                Log.i("keyevent",String.valueOf(event.getAction()));

                if (!botonEliminarEstaPresionado){
                    botonEliminarEstaPresionado = true;
                    linearLayout.setBackgroundResource(R.color.colorEliminar);
                    player = MediaPlayer.create(getApplicationContext(), R.raw.exito);
                }


                break;

            case 280://la tecla del escaner es esta
                //Toast.makeText(getBaseContext(),"telca escaner pulsada",Toast.LENGTH_SHORT).show();
                break;

                default:
                    //si estamos aqui no hay pulsacion que nos interese
        }
        return true;
        //http://www.androidcurso.com/index.php/tutoriales-android-basico/36-unidad-5-entradas-en-android-teclado-pantalla-tactil-y-sensores/145-el-teclado

    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.alta_movimientoEliminarB:
                dialogoEliminarConfirmacion(this,"¿Deseaas eliminar todo el movimiento?");
                break;
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

        switch (compoundButton.getId()){

            case R.id.confirma_usuario_entradaCB:
                //permitimos solo una opcion
                if (entradaCB.isChecked() && salidaCB.isChecked()){
                    salidaCB.setChecked(false);
                }
                correrAnimacion();

                break;

            case R.id.confirma_usuario_salidaCB:
                //para permintir solo una opcion habilitada
                if (salidaCB.isChecked() && entradaCB.isChecked()){
                    entradaCB.setChecked(false);
                }
                correrAnimacion();

                break;

        }

    }


    public void correrAnimacion (){
        Animation movimientoCarritoAnim;

        carritoIV = (ImageView)findViewById(R.id.confirma_usuario_carritoTV);
        flechaIV = (ImageView)findViewById(R.id.confirma_usuario_flechaIV);
        carroIV = (ImageView)findViewById(R.id.confirma_usuario_carroIV);
        almacenIV = (ImageView)findViewById(R.id.confirma_usuario_almacenIV);



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









    private void recibeCodigo(final String codigo) {

        String codigoLimpio = limpiarCodigo(codigo);


            //si el codigo no pertenece a una pulsera entonces continuamos con la busqueda de un producto
            //buscamos en la base de datos de productos el producto indicado
            Cursor cursorDetalleProducto = dataBaseHelper.getInventoryByCode(codigoLimpio);
            cursorDetalleProducto.moveToFirst();


            Log.i("ProductosDevueltos", String.valueOf(cursorDetalleProducto.getCount()));

            if (cursorDetalleProducto.getCount()>0){
                //despues de validar que el codigo exista
                // tanto en el catalogo de codigos como en el inventario
                //revisamos que accion realizaremos si eliminar o agregar producto
                //dependiendo de si el boton eliminar lo esta precionando o no
                if (botonEliminarEstaPresionado){
                    quitarProducto(codigoLimpio);
                }else {
                    insertarProducto(codigoLimpio);
                }

                setListView();

            }else {
                Toast.makeText(this,"El codigo capturado no existe o no se encuentra en el inventario",Toast.LENGTH_LONG).show();
            }







    }

    private String limpiarCodigo (String codigo){
        Log.i("","************En metodo limpiarCodigo*************\n\n");

        Log.i("codigo de entrada ",codigo);
        //limpiamos la cadena obtenida ya que algunos codigos de barras vienen $369.00 otros con 03990
        //entonces vamos a eliminar $ tambien 0 y el ".", importante al eliminar el "0" deberemos solo eliminarlo si esta al inicio o al final 03990 porque hay precios que tienen 309 y si eliminarmos
        //todos los 0 el precio quedaria como 39

        //al recibir el codigo primero vamos a quitar el signo de pesos y el punto
        codigo = codigo.replace("$","").replace(".",""); // quitamos el signo de peso y el punto
        Log.i("sin $ y sin . ",codigo);

        char [] chars = codigo.toCharArray();
        char ch;
        Log.i("longitud de char",String.valueOf(chars.length));

        for (int i = 0; i < chars.length ; i++){
            //(recorremos el array de los caracteres obtenidos del codigo
            // y el caracter del array en la posicion del iterador la guardamos en
            // una variable tipo char, para de esta manera poder evaluarla con un if!!)
            ch = chars[i];

            if (ch == '0' && i==0){
                Log.i("hay un 0 al inicio",String.valueOf(chars[0]));
                chars [i] = 'z';
            }

            if (ch == '0' && i == chars.length-1){
                Log.i("hay un 0 al final", String.valueOf(chars[chars.length - 1]));
                chars [i] = 'z';
            }

        }

        Log.i("encontrar 0","despues de encontrar 0 al inicio y al final " + String.valueOf(chars));
        codigo = String.valueOf(chars).replace("z",""); //remplazamos las z encontradas
        Log.i("al remplazar por vacio",codigo);
        Log.i("nueva logitud", String.valueOf(codigo.length()));

        //en el caso de los productos que vienen marcados como 03090 en este punto ya
        //obtenemos el codigo limpio.
        //Pero ahora para los codigos $309.00 en este punto obtenemos 3090
        //volvemos a repetir la busqueda del ultimo elemento que concuerde con 0

        chars = codigo.toCharArray(); //volvemos a partir el string codigo en chars y llenamos el array
        ch = chars[chars.length-1]; //a ch le ponemos el ultimo elemento del string si este ultimo elemento es un 0
        if(ch == '0'){
            chars[chars.length-1] = 'z'; //remplazamos el ultimo elemento que es 0 por una z
        }
        codigo = String.valueOf(chars).replace("z","");
        Log.i("Eliminando ultimo 0 ", "longitud " + String.valueOf(codigo.length()));
        Log.i("Eliminando ultimo 0 ", codigo);

        Log.i("","************Final de metodo limpiarCodigo*************\n\n");


        return codigo;

    }

    public void insertarProducto (String code){

        Cursor cursorDetalleProducto = dataBaseHelper.getInventoryByCode(code);
        cursorDetalleProducto.moveToFirst();


        //Buscamos en la talba movimiento temporal si hay algun producto del mismo codigo ya ingresado
        Cursor cursorMovimientoTemporal = dataBaseHelper.getProductoTemporal(code);
        cursorMovimientoTemporal.moveToFirst();

        if (cursorMovimientoTemporal.getCount() == 1){
            //si ya hay un producto
            //del mismo codigo ingresado
            ContentValues contentValues = new ContentValues(2);
            int piezas = cursorMovimientoTemporal.getInt(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CANTIDAD));
            piezas++;//incrementamos el valor actual de las piezas 1 vez
            contentValues.put(DataBaseHelper.CANTIDAD,piezas);
            //actualizamos la tabla con el nuevo valor de las piezas
            dataBaseHelper.actualizaProductoTemporal(contentValues,code);
        }else {
            //si no hay algun producto ingresado es decir que el cursor retone 0
            //(aunque tambien podria retortar 2 pero no deberia ya que se supone que en
            // ningun momento habra 2 porductos con el mismo codigo ingresado en la
            // tabla de movimientosTemporal)
            ContentValues contentValues = new ContentValues(4);
            contentValues.put(DataBaseHelper.CODIGO,code);
            contentValues.put(DataBaseHelper.PRECIO,cursorDetalleProducto.getInt(cursorDetalleProducto.getColumnIndex(DataBaseHelper.PRECIO)));
            contentValues.put(DataBaseHelper.PRECIO_DISTRIBUCION,cursorDetalleProducto.getInt(cursorDetalleProducto.getColumnIndex(DataBaseHelper.PRECIO_VENDEDORA)));
            contentValues.put(DataBaseHelper.CANTIDAD,1);//insertamos una sola pieza en cantidad
            long resultado = dataBaseHelper.insertarProductoTemporal(contentValues);
            Log.i("resultadoUpdate", String.valueOf(resultado));

        }

        player.start();
        vibrator.vibrate(400);


    }

    public void quitarProducto(String code){

        Cursor cursorMovimientoTemporal = dataBaseHelper.getProductoTemporal(code);
        cursorMovimientoTemporal.moveToFirst();

        //si existe producto ingresado en la tabla temporal que concuerde con el codigo entonces
        if (cursorMovimientoTemporal.getCount()==1){

            //consultamos que la cantidad a retirar sea mayor a 1 para asi eliminar de la cantidad 1 pz
            int piezas = cursorMovimientoTemporal.getInt(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CANTIDAD));
            if(piezas > 1){
                piezas -=1;//le restamos una pieza
                ContentValues contentValues = new ContentValues(2);
                contentValues.put(DataBaseHelper.CANTIDAD,piezas);
                dataBaseHelper.actualizaProductoTemporal(contentValues,code);
            }else{
                //si la cantidad esta en 1 pieza eliminamos el registro porque al restar seria 0
                dataBaseHelper.deleteProductFromTemporalByCode(code);
            }

            player.start();
            vibrator.vibrate(1500);


        }else{
            //si no existe ningun producto a retirar
            Toast.makeText(getBaseContext(),"No hay ningun Producto a retirar de: " + code,Toast.LENGTH_SHORT).show();

        }
    }

    /**LLernamos nuestro listview*/
    public void setListView (){
        ListView listView = (ListView) findViewById(R.id.alta_movimientoLV);
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
        cantidadTotalTV = (TextView) findViewById(R.id.alta_movimientoCantidadTV);
        importeTotalTV = (TextView) findViewById(R.id.alta_movimientoImporteTV);

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

    public void dialogoEliminarConfirmacion(Activity activity, String mensaje){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);



        builder.setMessage(mensaje)
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dataBaseHelper.deleteAllMovimientoTemporal();
                        setListView();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        builder.create();
        builder.show();
    }


    private void solicitarFoco(){
    //https://www.google.com/search?client=firefox-b-d&ei=M1VbXY_GOYGWsQX2lLmAAQ&q=solicitar+foco+a+edittext+android+java&oq=solicitar+foco+a+edittext+android+java&gs_l=psy-ab.3..33i160.24708.33588..33725...0.2..1.453.4592.3j27j1j1j1......0....1..gws-wiz.......0i71j35i39j0i67j0j0i131j33i22i29i30j33i21.Srz6i4YHZwE&ved=0ahUKEwjPq9-GrpDkAhUBS6wKHXZKDhAQ4dUDCAo&uact=5
        new Handler().post(
                new Runnable() {
                    @Override
                    public void run() {
                        codigoET.requestFocus();
                    }
                }
        );

    }




    //Todo CREAMOS NUESTRO BROADCAST RECEIVER
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String codigo = intent.getExtras().getString("code");
            if(!TextUtils.isEmpty(codigo)){
                recibeCodigo(codigo);
            }
        }
    };









    public void enviarPasswordAlServidor(final String password) {


        showProgresDialog(); //mostramos el progreso  indeterminado


        RequestParams parametros = new RequestParams();
        parametros.put("password", password);




        KaliopeServerClient.post("app_kaliope/dispositivo_almacen_obtenerUsuarioPorPassword.php", parametros, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();

                try {
                    usuario = response.getString("usuario");
                    nombreCompleto = response.getString("nombreCompleto");
                    Constant.NOMBRE_CORTO_DEL_AGENTE = nombreCompleto.substring(0,nombreCompleto.indexOf(" "));
                    passwordDeUsuario = password;

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

                .setTitle("Hola " + Constant.NOMBRE_CORTO_DEL_AGENTE + " este movimiento afectara tu inventario")

                .setMessage("\n" + nombreCompleto +
                        "\n\n¿Estas seguro de que quieres continuar a revision?")



                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {




                        iniciarActividadSiguiente();

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





    @SuppressWarnings("unchecked")
    private void iniciarActividadSiguiente (){

        player.start();
        vibrator.vibrate(400);

        Constant.PASSWORD_DEL_AGENTE = passwordDeUsuario;
        Constant.USUARIO_DEL_AGENTE = usuario;
        Constant.NOMBRE_DEL_AGENTE = nombreCompleto;

        if (salidaCB.isChecked()) {
            Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE = Constant.SALIDA;
        }
        if (entradaCB.isChecked()) {
            Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE = Constant.ENTRADA;
        }

                transition = new Slide(Gravity.START);
                transition.setDuration(Constant.DURATION_TRANSITION);
                transition.setInterpolator(new DecelerateInterpolator());
                getWindow().setExitTransition(transition);
                Intent intent = new Intent(this,ConfirmacionDeSupervisor.class);
                startActivity(intent,ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());

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
