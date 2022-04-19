package com.example.david.inventariosucursal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ConfirmaUsuarioActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private final int PASO_USUARIO_CONFIRMA = 2;
    private final int PASO_SUPERVISOR_CONFIRMA = 4;

    CheckBox entradaCB,salidaCB;
    Button continuarB;
    ImageView carritoIV,flechaIV,carroIV,almacenIV;
    TextView tituloTipoMovTV,intruccionesTV, pasoTipoMovTV;
    EditText codigoPulseraET;
    LinearLayout layout;

    int numeroDePaso = PASO_USUARIO_CONFIRMA;// lo definimos por default por si acaso en pasos cuando se llama por primera ves a esta activity es el paso 2 porque el 1 es el alta de movimiento


    MediaPlayer player;
    Vibrator vibrator;


    ProgressDialog progressDialog;
    Activity activity;

    Transition transition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirma_usuario);
        activity = this;

        entradaCB = (CheckBox) findViewById(R.id.confirma_usuario_entradaCB);
        salidaCB = (CheckBox) findViewById(R.id.confirma_usuario_salidaCB);
        continuarB = (Button) findViewById(R.id.confirma_usuario_continuarB);
        tituloTipoMovTV = (TextView) findViewById(R.id.confirma_usuario_tituloTV);
        intruccionesTV=(TextView)findViewById(R.id.confirma_usuario_instruccionesPulseraTV);
        pasoTipoMovTV = (TextView)findViewById(R.id.confirma_usuario_pasoTV);
        codigoPulseraET = (EditText)findViewById(R.id.confirma_usuario_passwordET);
        layout = (LinearLayout) findViewById(R.id.confirma_usuario_Layout);


        continuarB.setOnClickListener(this);
        entradaCB.setOnCheckedChangeListener(this);
        salidaCB.setOnCheckedChangeListener(this);


        //creamos una transicion de entrada
        Slide slide = new Slide (Gravity.END);
        slide.setDuration(Constant.DURATION_TRANSITION);
        slide.setInterpolator(new DecelerateInterpolator());
        getWindow().setEnterTransition(slide);
        getWindow().setAllowEnterTransitionOverlap(false);


        player = MediaPlayer.create(getApplicationContext(),R.raw.beep);
        vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);


        //recogemos nuestro bundle Para saber que numero de paso es este activity y definir su comportamiento
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            numeroDePaso = bundle.getInt("numeroPaso");
        }


        switch (numeroDePaso){

            case PASO_USUARIO_CONFIRMA:
                tituloTipoMovTV.setText("Confirmacion de Usuario");
                pasoTipoMovTV.setText("Paso 2-4");
                entradaCB.setText("Entrada de Mercancia a mi Auto");
                salidaCB.setText("Salida de mercancia de mi Auto");
                codigoPulseraET.setHint("Contraseña Usuario");
                intruccionesTV.setText("Ahora ingresa tu contraseña de usuarioSupervisor, asi asignaremos este movimiento solo a tu inventario");
                break;

            case PASO_SUPERVISOR_CONFIRMA:
                tituloTipoMovTV.setText("Confirmacion de Compañero");
                pasoTipoMovTV.setText("Paso 4-4");
                entradaCB.setText("Entrada a su Auto");
                salidaCB.setText("Salida de su Auto");
                codigoPulseraET.setHint("Pulsera de Supervisor");
                intruccionesTV.setText("Es necesario que el compañero que te ayudo a revisar ingrese su Contraseña de usuarioSupervisor");
                layout.setBackgroundResource(R.color.colorRosalBajo);
                break;


        }





        //respondemos al evento de la tecla enter
        codigoPulseraET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)){
                    String codigo = codigoPulseraET.getText().toString();
                    codigoPulseraET.setText("");
                    enviarPasswordAlServidor(codigo);
                    return true;
                }
                return false;            }
        });




        correrAnimacion();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.confirma_usuario_continuarB:
                    //iniciarActividadSiguiente();
                break;


        }


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAfterTransition();
    }

    @SuppressWarnings("unchecked")
    private void iniciarActividadSiguiente (){

        player.start();
        vibrator.vibrate(400);


        if (comprobarCheckBox()){//hacemos el trabajo de los check box



                switch (numeroDePaso){
                    case PASO_USUARIO_CONFIRMA:
                        transition = new Slide(Gravity.START);
                        transition.setDuration(Constant.DURATION_TRANSITION);
                        transition.setInterpolator(new DecelerateInterpolator());
                        getWindow().setExitTransition(transition);
                        Intent intent = new Intent(this,AltaMovimiento.class);
                        intent.putExtra("numeroPaso",3);
                        startActivity(intent,ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                        break;

                    case PASO_SUPERVISOR_CONFIRMA:
                        transition = new Explode();
                        transition.setDuration(Constant.DURATION_TRANSITION);
                        transition.setInterpolator(new DecelerateInterpolator());
                        getWindow().setExitTransition(transition);
                        Intent intent1 = new Intent(this,CodigoQRActivity.class);
                        startActivity(intent1,ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
                        break;
                }



        }

    }



    private boolean comprobarCheckBox() {
        //Para que este metodo devuelva true:
        //Algun checkBox debera estar seleccionado, si es el paso 2 asignara la seleccion del agente a las constantes y entonces sera true
        //Algun checkBox debera estar seleccionado, si es el paso 4 asignara la seleccion del supervisor a las constantes y despues comprobara que la desicion del agente y del supervisor sean iguales, si son iguales sera true


        if (entradaCB.isChecked() || salidaCB.isChecked()) {
                //comprobamos que alguno de los checkBox este seleccionado para poder continuar

            switch (numeroDePaso){

                case PASO_USUARIO_CONFIRMA:
                    //si este es el paso 2 es decir estamos ingresando la pulsera y el tipo de movimiento del agente
                    //guardamos en las connsatnes del agente lo que escogio
                    if (salidaCB.isChecked()) {
                        Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE = Constant.SALIDA;
                    }
                    if (entradaCB.isChecked()) {
                        Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE = Constant.ENTRADA;
                    }

                    break;

                case PASO_SUPERVISOR_CONFIRMA:
                    //si este es el paso 4 es decir que estamos capturando el tipo de movimiento del supervisor
                    //guardamos en las constantes del supervisor lo que escogio
                    if (salidaCB.isChecked()) {
                        Constant.TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR = Constant.SALIDA;
                    }
                    if (entradaCB.isChecked()) {
                        Constant.TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR = Constant.ENTRADA;
                    }

                    //Ahora validamos en el paso 4 que el agente y el supervisor seleccionaran ambos el mismo tipo de movimiento
                    if (!Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE.equals(Constant.TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR)) {
                        //si la seleccion que hicieron tanto el agente como el supervisor son diferentes entonces no permitimos continuar
                        Toast.makeText(getBaseContext(), "Los tipos de movimientos que selecciono el agente y el supervisor son diferentes por favor vuelvan a revisar", Toast.LENGTH_LONG).show();
                        return false;
                    }

                    break;

            }


        } else {
            Toast.makeText(getBaseContext(), "No ha seleccionado ningun tipo de movimiento", Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
        //se llegara hasta el true solo hasta que:
        // -Alguno de los checkBox este seleccionado, si es el paso 2 se asignaran sus decisiones a las constantes y entonces sera true
        // -Alguno de los checkBox este seleccionado, si es el paso 4 se asignaran la desicion del supervisor a sus constantes y despues de eso se validara que ambas decisiones sean iguales, si son iguales sera true



    }








    //cremaos nuestro Receiver que va a recibir y ejecutar el codigo que escaneo el escaner

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String codigo = intent.getExtras().getString("code");
            if (!TextUtils.isEmpty(codigo)){
                //iniciarActividadSiguiente(codigo);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.KEY_INTENTFILTER_ESCANER);
        this.registerReceiver(broadcastReceiver,intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);
    }







    public void enviarPasswordAlServidor(final String password) {

        final Inventario inventarioActivity = new Inventario();



        showProgresDialog(); //mostramos el progreso  indeterminado


        RequestParams parametros = new RequestParams();
        parametros.put("password", password);




        KaliopeServerClient.post("app_kaliope/dispositivo_almacen_obtenerUsuarioPorPassword.php", parametros, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressDialog.dismiss();

                try {
                    String usuario = response.getString("usuarioSupervisor");
                    String nombreCompleto = response.getString("nombreCompletoSupervisor");

                    dialogoDeConfirmacionDatosUsuario("Este movimiento afectara el inventario de:",
                            "\nUsuario: " + usuario +
                                    "\nNombre: " + nombreCompleto , password, usuario, nombreCompleto);

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

    private void dialogoDeConfirmacionDatosUsuario(String title, String mensaje, final String passwordIngresado, final String usuario, final String nombreCompleto) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(mensaje)



                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Constant.PASSWORD_DEL_AGENTE = passwordIngresado;
                        Constant.USUARIO_DEL_AGENTE = usuario;
                        Constant.NOMBRE_DEL_AGENTE = nombreCompleto;
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
}


