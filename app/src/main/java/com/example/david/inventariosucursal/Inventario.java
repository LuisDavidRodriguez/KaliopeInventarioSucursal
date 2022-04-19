package com.example.david.inventariosucursal;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Inventario extends AppCompatActivity implements View.OnClickListener {

    DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
    ArrayList <HashMap> list;
    private int existenciasTotales;
    private int importeTotal;

    TextView existeciasTV;
    TextView importeExistenciasTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        Button imprimirB = (Button) findViewById(R.id.inventarioImprimirB);
        Button exportarB = (Button) findViewById(R.id.inventarioExportarB);
        existeciasTV = (TextView) findViewById(R.id.inventarioExistenciasTV);
        importeExistenciasTV = (TextView) findViewById(R.id.inventarioImporteTV);
        imprimirB.setOnClickListener(this);
        exportarB.setOnClickListener(this);

        Cursor todoElInventario = dataBaseHelper.getAllInventory();
        if (todoElInventario.getCount()==0){
            //si el inventario esta vacio llamaos al metodo de llenado de inventario
            //si la base de datos esta vacia leemos el archivo de texto y la llenamos
            try {
                leerTXT();
            } catch (Exception e) {
                Log.i("ExcepcionLeerTXT",e.toString());
                Toast.makeText(this,"Error: " + e.toString(),Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else {
            //si ya tiene datos solo llenamos la lista
            setListView();
        }




    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.inventarioImprimirB:

                break;

            case R.id.inventarioExportarB:
                dialogoConfirmacionExportarInventario(this);
                break;

        }

    }


    /**leemos nuestro archivo txt y llenamos nuestra base de datos lanzamos cualquier tipo de excepcion al metodo padre*/
    public void leerTXT () throws Exception{

        File folder = new File(Constant.RUTA_MEMORIA_DISPOSITIVO,  "/" + Constant.NOMBRE_CARPETA);
        File file = new File(folder + "/inventario.txt");

        FileReader archivoTXT = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(archivoTXT);

        String line;

        while ((line = bufferedReader.readLine()) != null){

            String [] columnas = line.split(",");

            if (columnas.length!=4){
                continue;
            }else{
                //porcedemos a llenar nuestra base de datos
                ContentValues contentValues = new ContentValues(3);
                contentValues.put(DataBaseHelper.CODIGO,columnas[0]);
                contentValues.put(DataBaseHelper.PRECIO,columnas[1]);
                contentValues.put(DataBaseHelper.PRECIO_VENDEDORA,columnas[2]);
                contentValues.put(DataBaseHelper.EXISTENCIAS,columnas[3]);

                dataBaseHelper.insertInventory(contentValues);
            }
        }

        file.delete();
        setListView();
    }

    /**LLernamos nuestro listview*/
    public void setListView (){

        ListView listView = (ListView) findViewById(R.id.inventarioLV);
        list = new ArrayList<HashMap>();

        AdapterListFourItems adapterListFourItems = new AdapterListFourItems(list,this);
        listView.setAdapter(adapterListFourItems);

        Cursor cursorAllInventory = dataBaseHelper.getAllInventory();
        cursorAllInventory.moveToFirst();

        if (cursorAllInventory.getCount()> 0){



            HashMap encabezados = new HashMap();
            encabezados.put(Constant.UNO,"Codigo");
            encabezados.put(Constant.DOS,"Precio");
            encabezados.put(Constant.TRES,"Ven");
            encabezados.put(Constant.CUATRO,"Exist");
            list.add(encabezados);

            do{
                HashMap items = new HashMap();
                items.put(Constant.UNO,cursorAllInventory.getString(cursorAllInventory.getColumnIndex(DataBaseHelper.CODIGO)));
                items.put(Constant.DOS,cursorAllInventory.getString(cursorAllInventory.getColumnIndex(DataBaseHelper.PRECIO)));
                items.put(Constant.TRES,cursorAllInventory.getString(cursorAllInventory.getColumnIndex(DataBaseHelper.PRECIO_VENDEDORA)));
                items.put(Constant.CUATRO,cursorAllInventory.getString(cursorAllInventory.getColumnIndex(DataBaseHelper.EXISTENCIAS)));
                list.add(items);

                int piezas = cursorAllInventory.getInt(cursorAllInventory.getColumnIndex(DataBaseHelper.EXISTENCIAS));
                int importe = cursorAllInventory.getInt(cursorAllInventory.getColumnIndex(DataBaseHelper.PRECIO_VENDEDORA));
                int importeTemporal = piezas * importe;

                this.existenciasTotales += piezas;
                this.importeTotal += importeTemporal;

            }while (cursorAllInventory.moveToNext());

            existeciasTV.setText(String.valueOf(this.existenciasTotales));
            importeExistenciasTV.setText(String.valueOf(this.importeTotal));

        }


    }



    public void exportaDB(Activity activity) throws IOException {
        DataBaseHelper dataBaseHelper1 = new DataBaseHelper(activity);
        Cursor res = dataBaseHelper1.getAllInventory();
        res.moveToFirst();

        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(Constant.RUTA_MEMORIA_DISPOSITIVO + "/" + Constant.NOMBRE_CARPETA +"/inventarioExportado "+utilidadesApp.getFecha() + ".txt"));
        bw.write("CÓDIGO,PRECIO,VENDEDORA,SOCIA,EMPRESARIA,EXISTENCIAS\n");

        do{
            bw.write(res.getString(res.getColumnIndex(DataBaseHelper.CODIGO)) + ","
                    + res.getString(res.getColumnIndex(DataBaseHelper.PRECIO)) + ","
                    + res.getString(res.getColumnIndex(DataBaseHelper.PRECIO_VENDEDORA)) + ","
                    + res.getString(res.getColumnIndex(DataBaseHelper.PRECIO_SOCIA)) + ","
                    + res.getString(res.getColumnIndex(DataBaseHelper.PRECIO_EMPRESARIA)) + ","
                    + res.getString(res.getColumnIndex(DataBaseHelper.EXISTENCIAS)) + "\n");
        }while(res.moveToNext());

        bw.close();
        Toast.makeText(this, "Los datos fueron grabados correctamente", Toast.LENGTH_SHORT).show();

        dataBaseHelper1.deleteInventory();

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);




    }


    public void llenarInventarioDesdeJsonArray (JSONArray inventarioJsonArray, Activity activity){
        DataBaseHelper dataBaseHelper1 = new DataBaseHelper(activity);
        dataBaseHelper1.deleteInventory();

        for (int i = 0; i<inventarioJsonArray.length() ; i++) {
            try {
                String codigo = inventarioJsonArray.getJSONObject(i).getString("codigo");
                String precio = inventarioJsonArray.getJSONObject(i).getString("precio");
                String existencia = inventarioJsonArray.getJSONObject(i).getString("existencia");
                String vendedora = inventarioJsonArray.getJSONObject(i).getString("vendedora");
                String socia = inventarioJsonArray.getJSONObject(i).getString("socia");
                String empresaria = inventarioJsonArray.getJSONObject(i).getString("empresaria");




                ContentValues contentValues = new ContentValues(5);
                contentValues.put(DataBaseHelper.CODIGO, codigo);
                contentValues.put(DataBaseHelper.PRECIO, precio);
                contentValues.put(DataBaseHelper.PRECIO_VENDEDORA, vendedora);
                contentValues.put(DataBaseHelper.PRECIO_SOCIA, socia);
                contentValues.put(DataBaseHelper.PRECIO_EMPRESARIA,empresaria);
                contentValues.put(DataBaseHelper.EXISTENCIAS, existencia);

                dataBaseHelper1.insertInventory(contentValues);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //fuera del for recuperamos la version de solo un producto
        int version = 0;
        try {
            version = Integer.valueOf(inventarioJsonArray.getJSONObject(0).getString("version"));
            ConfiguracionesApp.setVersionInventario(activity,version);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public String enviarInventarioPorJsonArray (Activity activity){
        DataBaseHelper dataBaseHelper1 = new DataBaseHelper(activity);
        JSONArray jsonArrayInventario = new JSONArray();

        Cursor cursorInventario = dataBaseHelper1.dameInventariosConExistencias();
        if (cursorInventario.getCount() > 0){
            cursorInventario.moveToFirst();

            do{
                try {
                    JSONObject producto = new JSONObject();
                    producto.put("codigo", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.CODIGO)));
                    producto.put("existencias", cursorInventario.getString(cursorInventario.getColumnIndex(DataBaseHelper.EXISTENCIAS)));
                    jsonArrayInventario.put(producto);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }while (cursorInventario.moveToNext());

        }

        //dataBaseHelper1.eliminaInventario();
        Log.d("jsonInventario",jsonArrayInventario.toString());
        return jsonArrayInventario.toString();
    }






    public void dialogoConfirmacionExportarInventario(final Activity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        final Integer[] r = new Integer[1];

        TextView title = new TextView(activity);
        title.setText("Title");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        builder.setTitle("¡Cuidado!");

        builder.setMessage("¿Realmente desea exportar el inventario?")
                .setPositiveButton("SI, EXPORTAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            exportaDB(activity);
                        } catch (IOException e) {
                            Log.i("ExcepcionLeerTXT",e.toString());
                            Toast.makeText(getApplicationContext(),"Error: " + e.toString(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("NO, CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });

        builder.create();
        builder.show();


    }




    /**Necesitamos enlazar la impresora que viene en la handHeld en el puerto serie*/




}
