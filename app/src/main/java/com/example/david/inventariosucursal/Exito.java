package com.example.david.inventariosucursal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Exito extends AppCompatActivity {

    TextView contadorTV, mensajeAlAjenteTV, versionTV, piezasTV, tituloTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exito);



        //Creamos una transicion de entrada
        Explode explode = new Explode();
        explode.setDuration(Constant.DURATION_TRANSITION);
        explode.setInterpolator(new DecelerateInterpolator());
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setEnterTransition(explode);




        mensajeAlAjenteTV = (TextView) findViewById(R.id.exito_mensajeAlAgente);
        versionTV = (TextView) findViewById(R.id.exito_versionTV);
        piezasTV = (TextView) findViewById(R.id.exito_piezasTV);
        contadorTV = (TextView) findViewById(R.id.exito_contadorTV);
        tituloTV = (TextView) findViewById(R.id.exito_titulo);


        String mensaje = Constant.NOMBRE_CORTO_DEL_AGENTE + "\n tu movimiento se ha creado exitosamente";
        tituloTV.setText(mensaje);

        //recibimos la informacion del activiti anterior
        Bundle datos = getIntent().getExtras();
        if(datos!=null){
            String version = datos.getString("version");
            String piezas = datos.getString("piezas");
            versionTV.setText(version);
            piezasTV.setText(piezas);
        }

        contadorConThread();



    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); //anulamos el boton de regresar
        Toast.makeText(this, "Espera a que se agote el tiempo", Toast.LENGTH_SHORT).show();
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

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);


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
