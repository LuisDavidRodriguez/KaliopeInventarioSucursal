package com.example.david.inventariosucursal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class utilidadesApp extends AppCompatActivity {

    public static String dameFecha() {

        final Calendar c = Calendar.getInstance();
        int a = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);

        String dia = "";
        String mes = "";
        String anio = Integer.toString(a);

        if (d < 10) {
            dia = Integer.toString(d);
            dia = "0" + dia;
        } else {
            dia = Integer.toString(d);
        }

        if (m < 10) {
            mes = Integer.toString(m);
            mes = "0" + mes;
        } else {
            mes = Integer.toString(m);
        }

        return dia + "-" + mes + "-" + anio;

    }



    public static String getFecha(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return dateFormat.format(c.getTime());
    }


    public static long getFechaMillis(){
        Date date = new Date();
        return date.getTime(); //retornamos el tiempo actual pero en millis
    }



    //Obtener el numero de semana
            //este metodo nos entrega el numero de semana del año, un año tiene 52 semanas
            //el numero de semana se actualiza el dia lunes y es el mismo hasta el dia domingo
            //una ves que vuelve a ser lunes cambia el numero de semana, esto siguiendo un ejemplo
            //y una definicion encontrada en google
//    Resulta que en Java existe un método que ya nos devuelve el número de la semana
//    int numberWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
//    pero este número de la semana es dependiente de la región, por lo que podríamos obtener valores que no esperamos.
//    De acuerdo con la norma ISO_8601:
//    Se considera la primera semana de un año (semana W01) aquella que contiene el primer jueves de dicho año,
//    o lo que es lo mismo, aquella que contiene el día 4 de enero.
//    Los días de la semana se representan numéricamente con un dígito, siendo el primero día el lunes (día 1) y el último el domingo (día 7).
//    La semana empieza siempre, por tanto, en lunes.
//    En mi caso necesito obtener el numero de la semana de acuerdo a esta norma ISO y el código queda como sigue:

    //Para la handHeld de china por algun motivo este metodo iniciaba desde el dia domingo, domingo era el primer dia de la semana
    //no el lunes, de nada funciona el setFirdtDayOfWeek, llegaba domingo y cambiaba el numero de semana cuando debia cambiar el lunes
    //los celulares donde esta instalada la app kaliope ahi si cambiaba el numero de semana el dia lunes. bueno lei y dicen que en estados
    //unidos el primer dia de la semana es domingo, y en fracia es el lunes, entonces ecnontre en el inter. Actualizar tambien el de la app kaliope
    //no valla a ser que algunos telefonos si cambien el lunes y otros el domingo


    //If you want to set Monday then use

    //Calendar currentCalendar = Calendar.getInstance(new Locale("en","UK"));

    //If you want to set Sunday then use

    //Calendar currentCalendar = Calendar.getInstance(new Locale("en","US"));



    public static int getNumeroSemana(){

        final Calendar calendar = Calendar.getInstance(new Locale("en","UK"));
        //calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);


        //definimos la fecha de donde comenzara esto solo lo use para hacer pruebas y determinar
        //si devolvia correctamente el numero de semana
        //calendar.set(Calendar.YEAR,2020);
        //calendar.set(Calendar.MONTH,2);
        //calendar.set(Calendar.DATE,2);

        int numSemana = calendar.get(Calendar.WEEK_OF_YEAR);
        return numSemana;
    }




    public static String dameHora(){

        Calendar calendario = new GregorianCalendar();
        int hora, minutos;

        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);

        String sHoras;
        String sMinutos;

        if (hora < 10) {
            sHoras = "0" + Integer.toString(hora);
        } else {
            sHoras = Integer.toString(hora);
        }

        if (minutos < 10) {
            sMinutos = "0" + Integer.toString(minutos);
        } else {
            sMinutos = Integer.toString(minutos);
        }

       return sHoras + ":" + sMinutos;
    }

    public static String dameHoraCompleta(){

        Calendar calendario = new GregorianCalendar();
        int hora, minutos, segundos;

        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);

        String sHoras;
        String sMinutos;
        String sSegundos;

        if (hora < 10) {
            sHoras = "0" + Integer.toString(hora);
        } else {
            sHoras = Integer.toString(hora);
        }

        if (minutos < 10) {
            sMinutos = "0" + Integer.toString(minutos);
        } else {
            sMinutos = Integer.toString(minutos);
        }

        if (segundos < 10) {
            sSegundos = "0" + Integer.toString(segundos);
        } else {
            sSegundos = Integer.toString(segundos);
        }

        return sHoras + ":" + sMinutos + ":" + sSegundos;
    }

    public static String dameFehaHora(){
        return dameFecha() + " " + dameHoraCompleta();
    }







    public static void ponerEnPortapapeles (String mensaje, Activity activity){
        //String version = String.valueOf(android.os.Build.VERSION.SDK_INT);
        //int versionSDK = android.os.Build.VERSION.SDK_INT;
        //Toast.makeText(this,String.valueOf(versionSDK),Toast.LENGTH_SHORT).show();

        //vALIDAMOS SI LA VERSION DEL SDK ES MALOR O IGUAL A 11 USAMOS LA SINTAXIS UNO DE CLIPBOARD

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            //Toast.makeText(this, "tu version es mayor a 11", Toast.LENGTH_SHORT).show();
            ClipData clip = ClipData.newPlainText("El mensaje",mensaje);
            ClipboardManager clipboard = (ClipboardManager)activity.getSystemService(CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(clip);

        }else

        {
            //Toast.makeText(this, "tu version es menor a 11", Toast.LENGTH_SHORT).show();
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)activity.getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText (mensaje);
        }
    }

}


