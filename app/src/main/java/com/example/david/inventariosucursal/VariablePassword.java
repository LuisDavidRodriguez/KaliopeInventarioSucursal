package com.example.david.inventariosucursal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

/**
 * creada por david el 08/01/2019.
 */

public class VariablePassword extends AppCompatActivity{
    private int numero;
    private int password;
    private int numeroSemana;
    private int semilla;
    private boolean validacion;
    SoundPool soundPool;


    //Constructor
    public VariablePassword(){
        numero = 0;
        password = 0;
        numeroSemana = 0;
        semilla = 5;
        validacion = false;
        soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC,1);




    }


    /**
     *
     * Intrucciones de uso:
     * crear el obejto en las variables globales
     * VariablePassword variablepassword;
     *
     * instanciarlo de preferencia en el onResume
     * variablepassword = new VariablePassword();
     *
     * donde se quiera bloquear una funcion llamar al metodo
     * alertDialogValidaPassword
     * y luego al metodo
     * getValidacion(), es el que nos dara el acceso
     *
     *
     * Explicacion, cuando en alguna otra actividad se quiera utilizar esta clase,
     * solo se tendra que llamar al metodo alertDialogValidaPassword dentro de ese metodo
     * ya se crea y se compara la contraseña, y hace el cambio de la variable de instancia
     * validacion, cuando tu creas un objeto de la clase VariablePassword debes de instanciarlo
     * ej. VariablePassword variablePassword = new VariablePassword();
     * se llama a su constructor que inicializa validacion a false, ahora tu actividad donde se
     * creo el objeto variable passsword ya tiene un objeto de esta clase con sus variables de
     * instancia independientes, entonces cuando llamas al metodo getValidacion te devuelve "false"
     * despues llamas al dialogo, si lo validas correctamente validacion cambia a "true"
     * pero se quedara asi en true hasta que se destruya la actividad o hasta que se vuelva a
     * crear el objeto variablePassword.
     * por ejemplo si se instancea hasta la parte superior la validacion se quedara en true hasta
     * que se vuelva a crear el activity pero si en cambio lo inicializas en el onResume
     * cada que el activity desaparesca de foco, se volvera a crear el objeto y por lo tanto
     * la variable validacion se iniciara en false
     * esto es util en diversas circunstancias, un ejemplo perfecto esta en el activity catalogos
     * ahi se declara el objeto pero se inicializa hasta el onResume
     * VariablePassword variablePassword;
     *
     * lo inicializamos en el on resume esto para que se cree nuevamente el objeto
     * cada que se se entra o se sale de la activydad, esto porque, si tu creas el objeto en esta parte
     * una ves que valides la contraseña por primera ves, te deja entrar al activyti de pulseras
     * sales de la activuidad de pulseras, en el objeto VariablePassword su instancia validacion sige estando en true
     * porque anteriormente se valido, esto significa que nos dejara entrar nuevamente a las pulseras,
     * al instanciar el objeto en el onResume, este se creara cada ves que se active el onresume
     * por lo tanto su variable de instancia validacion se inicializara por su constructor a false
     * obteniendose asi que al regresar a la actividad pida nuevamente la claveVariable
     *
     *
     * @Override
     * public void onResume (){
     * super.onResume();
     * variablePassword = new VariablePassword();
     * }
     */


    //a este metodo se accede para crear la contraseña
    public int getPassword(){
        getRandom();
        getNumeroSemana();
        crearPassword();
        return password;
    }

    //este metodo se llamara para retornar el valor de la validacion si se ingreso correctamente la clave

    public boolean getValidacion(){
        return validacion;
    }

    //metodo usado para retornar el numero de random y entregarselo al usuarioSupervisor para que lo envie
    //a administracion y hacer las mismas operaciones con el para obtener la contraseña
    //como cada objeto creado en esta clase concerba su copia de variables de instancia
    //segun las reglas de java, entonces cuando se llama al metodo getPassword desde otra clase,
    //y despues se llama al metodo getNumero este numero random se conserva en la variable
    //porque lo que crei que ocurriria es que al llamar al metodo metodo getPassword se generaria un
    //numero random y luego al llamar a getNumero se generaria otro nuevo numero random que no
    //seria el mismo con el que se creo la contraseña. Por fortuna no funciona asi, entonces
    //el numero entregado corresponde con el que se creo la contraseña, por lo tanto al llamar a
    //este metodo podemos obtener este numero para mostrarselo al usuarioSupervisor.
    public int getNumero (){
        return numero;
    }


    //generamos el codigo random le sumamos 1000 para que no salga por ejemplo 1, y se lo
    //asignamos a su variable de instancia
    private void getRandom(){
        Random random = new Random();
        numero = random.nextInt(3000);
        numero += 1000;
    }

    //obtenemos el numero de semana
    private void getNumeroSemana (){
        numeroSemana = utilidadesApp.getNumeroSemana();
    }


    //metodo para crear la contraseña
    private void crearPassword(){
        double temporal;
        temporal = (numero + 250)*semilla;
        temporal = temporal / numeroSemana;
        temporal = temporal * numero;
        temporal = Math.sqrt(temporal);
        //redondeamos al numero inmediatamente superior
        password = (int) Math.ceil(temporal);
    }




    //Creamos el AlertDialog que se usara cada que se llame a esta clase
    //para que asi aparesca este dialogo en cualquier lugar que se le llame a la clase, y
    //quitar el problema de tener que escribir este codigo en cada activyti que usara la clave
    //variable.

    public void alertDialogValidaPassword (final Context context, final Activity activity, String mensaje){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);


        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        validacion = false;

        getPassword(); //creamos nuestra contraseña variable
        String numero = String.valueOf(getNumero());//obtenemos el nomero sobre el cual se opero



        LayoutInflater inflater = activity.getLayoutInflater();//instanciamos nuestro inflater
        //creamos nuestra vista y la inflamos con el layout personalizado, el siguiente atributo se pone en null
        View v = inflater.inflate(R.layout.alert_dialog_valida_password_variable,null);

        //referenciamos nuestro editText de password
        final EditText passwordIngresado = (EditText) v.findViewById(R.id.editText2);
        final TextView mensajeView = (TextView) v.findViewById(R.id.textView47);

        String mensajeCompleto = mensaje + numero + " - " + String.valueOf(numeroSemana);
        mensajeView.setText(mensajeCompleto);

        builder.setView(v)
                .setPositiveButton("Validar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        //Validamos el password
                        if (String.valueOf(passwordIngresado.getText()).equals(String.valueOf(password))) {
                            Toast.makeText(context,"Contraseña Correcta",Toast.LENGTH_SHORT).show();
                            validacion = true;
                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(400);
                            int sonido;
                            sonido = soundPool.load(context,R.raw.exito,1);
                            soundPool.play(sonido,1,1,0,0,1);


                        } else {
                            Toast.makeText(context,"Codigo Incorrecto",Toast.LENGTH_LONG).show();
                            validacion = false;


                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(100);

                            int sonido;
                            sonido = soundPool.load(context,R.raw.error,2);
                            soundPool.play(sonido,1,1,0,0,1);

                        }
                    }
                });


        //se crea el cuadro de dialogo
        builder.create();

        //se muestra el dialogo
        builder.show();

    }





}
