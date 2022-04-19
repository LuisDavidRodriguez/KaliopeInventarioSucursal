package com.example.david.inventariosucursal;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CodigoQRActivity extends AppCompatActivity {


    TextView contadorTV, porSuSeguridadTV, infoTV;
    Button finalizarB;





    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    private final String VALIDADOR_DE_QR_KALIOPE = "kaliopeQRA";

    private String cadenaCreada; // en esta variable se guardara la cadena generada por el metodo generar cadena
    //por si el error de que no se cree el codigo qr este en el metodo de crear el QR.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_qr);

        //Creamos una transicion de entrada
        Explode explode = new Explode();
        explode.setDuration(Constant.DURATION_TRANSITION);
        explode.setInterpolator(new DecelerateInterpolator());
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setEnterTransition(explode);


        contadorTV = (TextView) findViewById(R.id.codigo_qr_contadorTV);
        finalizarB = (Button) findViewById(R.id.codigo_qr_finalizarB);
        porSuSeguridadTV = (TextView)findViewById(R.id.codigo_qr_porSeguridadTV);
        infoTV = (TextView) findViewById(R.id.codigo_qr_Info);
        finalizarB.setVisibility(View.INVISIBLE);


        infoTV.setText("Este movimiento es para la pulsera numero: " + Constant.idPulseraAgente +
        "\n y fue Revisado por la pulsera numero: " + Constant.idPulseraSupervisor);




        finalizarB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });







        //Aqui crearemos el codigo Qr con los datos de la tabla temporal del movimiento
        //generaremos los codigos y despues afectaremos el inventario.
        afectarInventario();
        cadenaCreada = generarCadena();
        generarCodigoQR(cadenaCreada);
        crearMovimientoPermanente();
        contadorConThread();




        //registramos nuestro Handler para reejecutar la creacion del codigo en 2 segundos despues

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"creando otra vez",Toast.LENGTH_SHORT).show();
                generarCodigoQR(cadenaCreada);
            }
        },2000);



    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();//lo comentamos para que nos e haga nada cuando se preciona volver
        Toast.makeText(this,"El movimiento se creo, ya no puedes regresar, escanea el codigo y finaliza", Toast.LENGTH_LONG).show();

    }

    private void generarCodigoQR(String cadena){

        //  Obtener la cadena en el cuadro de edición
        String mensaje = cadena.trim(); //devuelve un string con los espacios iniciales y finales omitidos


        Bitmap bitmap = App.createQRImage(mensaje,600,600);
        ImageView imageViewQR = (ImageView) findViewById(R.id.codigo_qr_codigoIV);


        imageViewQR.setImageBitmap(bitmap);


        //App.printQRCode(0, 300,300, mensaje); //para imprimirlo

    }

    private String generarCadena (){
        Cursor cursor = dataBaseHelper.getAllProductTemporal();
        cursor.moveToFirst();

        //inicializamos el string segun la documentacion
        String cadena = VALIDADOR_DE_QR_KALIOPE + ","
                + AltaMovimiento.piezasTotales + ","
                + AltaMovimiento.importeTotal + ","
                + Constant.PASSWORD_DEL_AGENTE + ","
                + Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE;

        Log.i("Inicio Cadena" , cadena);

        if(cursor.getCount()>0){

            do{
                cadena += "," + cursor.getString(cursor.getColumnIndex(DataBaseHelper.CANTIDAD));
                cadena += "," + cursor.getString(cursor.getColumnIndex(DataBaseHelper.CODIGO));
            }while (cursor.moveToNext());

        }

        Log.i("cadena Llena" , cadena);



        return cadena;

    }


    private void afectarInventario (){

        //implimentamos la afectacionde inventario en un nuevo hilo o proceso de background para que no se "sienta" la tardansa al aparecer este activity
        //si la modificacion del inventario es muy larga

        new Thread(new Runnable() {
            @Override
            public void run() {

                Cursor cursor = dataBaseHelper.getAllProductTemporal();
                cursor.moveToFirst();

                if (Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE.equals(Constant.ENTRADA)){
                    //se supone que si el usuarioSupervisor escge que es entrada, es entrada pero para su automovil, significa que es salida de este almacen
                    do {
                        dataBaseHelper.decrementaInventario(cursor.getString(cursor.getColumnIndex(DataBaseHelper.CODIGO)), cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CANTIDAD)));

                    }while (cursor.moveToNext());
                }


                if (Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE.equals(Constant.SALIDA)){
                    do {
                        dataBaseHelper.incrementaInventario(cursor.getString(cursor.getColumnIndex(DataBaseHelper.CODIGO)), cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CANTIDAD)));

                    }while (cursor.moveToNext());
                }





            }
        }).start();





    }

    private void crearMovimientoPermanente (){
        //llenamos la tabla encabezado con la info de las pulseras
        //traspasamos la informacion de la tabla temporal a la tabla permanente
        //y borramos la tabla Temporal


        new Thread(new Runnable() {
            @Override
            public void run() {




                ContentValues contentValuesEncabezado = new ContentValues(7);
                contentValuesEncabezado.put(DataBaseHelper.ID_PULSERA,Constant.idPulseraAgente);
                contentValuesEncabezado.put(DataBaseHelper.ID_PULSERA_SUPERVISOR,Constant.idPulseraSupervisor);
                contentValuesEncabezado.put(DataBaseHelper.NUMERO_DE_SEMANA,utilidadesApp.getNumeroSemana());
                contentValuesEncabezado.put(DataBaseHelper.FECHA_HORA_MOVIMIENTO,utilidadesApp.getFechaMillis());
                contentValuesEncabezado.put(DataBaseHelper.CANTIDAD,AltaMovimiento.piezasTotales);
                contentValuesEncabezado.put(DataBaseHelper.IMPORTE_MOVIMIENTO,AltaMovimiento.importeTotal);
                contentValuesEncabezado.put(DataBaseHelper.TIPO_MOVIMIENTO,Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE);

                dataBaseHelper.setEncabezado(contentValuesEncabezado);


                int idEncabezado = dataBaseHelper.getLastId(DataBaseHelper.TABLA_ENCABEZADO_MOVIMIENTO);

                Cursor cursorMovimientoTemporal = dataBaseHelper.getAllProductTemporal();
                cursorMovimientoTemporal.moveToFirst();


                do{
                    ContentValues contentValues = new ContentValues(5); //KEY_ENCABEZADO, CODIGO, PRECIO, PRECIO_DISTRIBUCION, CANTIDAD;
                    contentValues.put(DataBaseHelper.KEY_ENCABEZADO,idEncabezado);
                    contentValues.put(DataBaseHelper.CODIGO,cursorMovimientoTemporal.getString(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CODIGO)));
                    contentValues.put(DataBaseHelper.PRECIO,cursorMovimientoTemporal.getInt(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.PRECIO)));
                    contentValues.put(DataBaseHelper.PRECIO_DISTRIBUCION,cursorMovimientoTemporal.getInt(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.PRECIO_DISTRIBUCION)));
                    contentValues.put(DataBaseHelper.CANTIDAD,cursorMovimientoTemporal.getInt(cursorMovimientoTemporal.getColumnIndex(DataBaseHelper.CANTIDAD)));

                    //dataBaseHelper.setMovimientoPermanente(contentValues);
                }while (cursorMovimientoTemporal.moveToNext());


                reiniciarYeliminarTablas();







            }
        }).start();

    }

    private void reiniciarYeliminarTablas(){
        dataBaseHelper.deleteAllMovimientoTemporal();
        Constant.TIPO_MOVIMIENTO_SELECCIONADO_AGENTE = "";
        Constant.TIPO_MOVIMIENTO_SELECCIONADO_SUPERVISOR = "";
        Constant.PASSWORD_DEL_AGENTE = "";

        Constant.idPulseraAgente = "";
        Constant.idPulseraSupervisor = "";
    }


    private void contadorConThread (){
        new Thread(new Runnable() {
            @Override
            public void run() {

                        int segundos;


                        for (segundos = 30 ; segundos >= 0 ; segundos--){
                            final int tiempo = segundos;


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    contadorTV.setText(String.valueOf(tiempo));

                                    if (tiempo == 0){
                                        finalizarB.setVisibility(View.VISIBLE);
                                        contadorTV.setVisibility(View.INVISIBLE);
                                        porSuSeguridadTV.setVisibility(View.INVISIBLE);

                                    }
                                }
                            });

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }





            }
        }).start();

    }


}
