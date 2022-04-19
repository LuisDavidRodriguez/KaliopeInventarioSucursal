package com.example.david.inventariosucursal;

import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creada por Luisda 28-01-2019
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "kaliopeApp.sqlite" ;
    private static final int DATABASE_VERSION = 1;

    public DataBaseHelper (Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }


    /** Nombres de las tablas */
    private static final String TABLA_INVENTARIO = "tablaInvetario";
    private static final String TABLA_MOVIMIENTO_TEMPORAL = "tablaTemporal";
    public static final String TABLA_ENCABEZADO_MOVIMIENTO = "encabezadoMovimiento"; //es aqui donde se guardaran las pulseras tanto de quien reviso como de quien atendio, se guardara el tipo de movimiento, el total de piezas y el importe total



    /**Campos de tablas **/
    public static final String KEY_ID = "_id";
    public static final String CODIGO = "codigo";
    public static final String PRECIO = "precio";
    public static final String PRECIO_VENDEDORA = "vendedora";
    public static final String PRECIO_SOCIA = "socia";
    public static final String PRECIO_EMPRESARIA = "empresaria";
    public static final String PRECIO_DISTRIBUCION = "precioDistribucion";
    public static final String CANTIDAD = "cantidad";
    public static final String TIPO_MOVIMIENTO = "entradaOsalida";
    public static final String ID_PULSERA_SUPERVISOR = "pulseraSupervisor";
    public static final String NUMERO_DE_SEMANA = "semana";
    public static final String FECHA_HORA_MOVIMIENTO = "fecha";
    public static final String IMPORTE_MOVIMIENTO = "importe";
    public static final String KEY_ENCABEZADO = "key_encabezdo";








    public static final String EXISTENCIAS = "existencias";


    /**
     *CAMPOS DE TABLA PULCERAS
     *En la tabla en la columna idPulsera, sera el id de la pulsera
     *por ejemplo todos los codigos que pertenescan a la pulsera uno
     *este campo tendra un 1, los 52 codigos de la ruta 2 en este campo habra un 2
     *en el campo de numero pulsera se almacenara 1,2,3,4,5...52 significa que aqui
     *estara el numero de semana que corresponde a esta pulsera
     *de esta manera para seleccionar un numero de pulsera se creara un metodo
     * donde a la clausula where se le manda el id de pulcera, el numero de semana y nos retorna
     * el codigo
     *
     */
    public static final String CODIGO_PULCERA = "codigo";
    public static final String ID_PULSERA = "idPulsera";
    public static final String NUMERO_PULSERA = "numeroPulsera";



    public static final String CREATE_TABLE_INVENTARIO = "CREATE TABLE "
            + TABLA_INVENTARIO + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CODIGO + " INTEGER,"
            + PRECIO + " INTEGER,"
            + PRECIO_VENDEDORA + " INTEGER,"
            + EXISTENCIAS + " INTEGER,"
            + PRECIO_SOCIA + " INTEGER,"
            + PRECIO_EMPRESARIA + " INTEGER)";





    public static final String CREATE_TABLE_MOVIMIENTO_TEMPORAL = " CREATE TABLE "
            + TABLA_MOVIMIENTO_TEMPORAL + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CODIGO + " TEXT,"
            + PRECIO + " INTEGER,"
            + PRECIO_DISTRIBUCION + " INTEGER,"
            + CANTIDAD + " INTEGER)";



    public static final String CREATE_TABLE_ENCABEZADO_MOVIMIENTO = " CREATE TABLE "
            + TABLA_ENCABEZADO_MOVIMIENTO + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ID_PULSERA + " INTEGER, "
            + ID_PULSERA_SUPERVISOR + " INTEGER, "
            + NUMERO_DE_SEMANA + " INTEGER, "
            + FECHA_HORA_MOVIMIENTO + " INTEGER, "
            + CANTIDAD + " INTEGER, "
            + IMPORTE_MOVIMIENTO + " INTEGER, "
            + TIPO_MOVIMIENTO + " TEXT)";




    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_INVENTARIO);
        sqLiteDatabase.execSQL(CREATE_TABLE_MOVIMIENTO_TEMPORAL);
        sqLiteDatabase.execSQL(CREATE_TABLE_ENCABEZADO_MOVIMIENTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_INVENTARIO);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_MOVIMIENTO_TEMPORAL);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLA_ENCABEZADO_MOVIMIENTO);

        onCreate(sqLiteDatabase);
    }



    /**Metodos inventario*/
    public Cursor getAllInventory (){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLA_INVENTARIO,null);
    }

    public Cursor dameInventariosConExistencias (){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLA_INVENTARIO + " WHERE " + EXISTENCIAS + "!=0",null);
    }

    public void insertInventory(ContentValues contentValues){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert(TABLA_INVENTARIO,null,contentValues);
    }

    public void deleteInventory (){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLA_INVENTARIO,null,null);
    }

    public Cursor getInventoryByCode (String code){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLA_INVENTARIO + " WHERE " +CODIGO + "=?",new String[]{code});
    }

    public void incrementaInventario(String idProducto, int cantidad){

        Log.d("dbg-sumaStock: ", "UPDATE " + TABLA_INVENTARIO + " SET " + EXISTENCIAS + " = " + EXISTENCIAS + " + " +  cantidad + " WHERE " + CODIGO + " = " + idProducto);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("UPDATE " + TABLA_INVENTARIO + " SET " + EXISTENCIAS + " = " + EXISTENCIAS + " + " +  cantidad + " WHERE " + CODIGO + " = " + idProducto, null);
        res.moveToFirst();

    }

    public void decrementaInventario(String idProducto, int cantidad){

        Log.d("dbg-restaaStock: ", "UPDATE " + TABLA_INVENTARIO + " SET " + EXISTENCIAS + " = " + EXISTENCIAS + " - " +  cantidad + " WHERE " + CODIGO + " = " + idProducto);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery("UPDATE " + TABLA_INVENTARIO + " SET " + EXISTENCIAS + " = " + EXISTENCIAS + " - " +  cantidad + " WHERE " + CODIGO + " = " + idProducto, null);
        res.moveToFirst();

    }







    /**Metodos de movimiento*/
    public long insertarProductoTemporal (ContentValues contentValues){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.insert(TABLA_MOVIMIENTO_TEMPORAL,null, contentValues);

        //retorna -1 si hay error al ingresar los datos
        // RETORNA 1 SI SE INGRESARON CORRECTAMENTE
    }

    public Cursor getProductoTemporal (String code){
        //como queremos que sobre un mismo renglon se inserten las piezas que se escanien con el
        //escaner de codigo de barras donde el codigo sea el mismo, entonces cuando
        //se ingrese un codigo que ya este registrado modificara el total de piezas
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String consulta = "SELECT * FROM " + TABLA_MOVIMIENTO_TEMPORAL + " WHERE " + CODIGO + "=?";
        return sqLiteDatabase.rawQuery(consulta,new String[]{code});

        //https://stackoverflow.com/questions/16962880/android-database-sqlite-sqliteexception-near-syntax-error-code-1
        //(de alguna manera extraña me generaba un erro por usar las cadenas de consulta como siempre lo habia hecho
        // String consulta = "SELECT * FROM " + TABLA_MOVIMIENTO_TEMPORAL + " WHERE " + CODIGO + " = " + code;
        //return sqLiteDatabase.rawQuery(consulta,null);
        // lei y encontre la solucion
        // jaja el error estaba en mi codigo que llamaba a este metodo le estaba enviando "" como codigo
        // entopnces la manera de hacerlo con las clausulas debe servir, pero lo dejamos asi, porque dicen que es como
        // deberia hacerce con el array en selection arg)
        //se supone que el ? indica que ahi va un parametro
        //https://stackoverflow.com/questions/4773789/what-is-use-of-question-mark-in-sql
    }

    public long actualizaProductoTemporal (ContentValues contentValues, String code){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.update(TABLA_MOVIMIENTO_TEMPORAL,contentValues, CODIGO + " = " + code,null);
    }

    public Cursor getAllProductTemporal (){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLA_MOVIMIENTO_TEMPORAL,null);
    }

    public void deleteProductFromTemporalByCode (String code){
        //eliminamos un registro de la tabla temporal
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLA_MOVIMIENTO_TEMPORAL,CODIGO + " = " + code,null);
    }

    public void deleteAllMovimientoTemporal (){
        //eliminamos todos los registros de productos en la tabla movimiento temporal
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLA_MOVIMIENTO_TEMPORAL,null,null);
    }



    /**Metodos para manejar ta tabla permanente de los movimientos y los encabezados de los movimientos
     * el movimiento que se ingresa temporalmente se guarda en la talba temporal, despues al finalizar el movimiento
     * movemos los prodcutos ingresados de esta tabla temporal a la tabla donde prebaleceran mas tiempo
     * la forma de obtenerlos es porque cada producto compartira un id de la tabla encabezaado
     * que tendra guardados los totales y la informacion importante del movimiento
     */

    public void setEncabezado (ContentValues contentValues) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.insert(TABLA_ENCABEZADO_MOVIMIENTO,null, contentValues);

    }

    public Cursor getAllEncabezado (){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
       return sqLiteDatabase.rawQuery( " SELECT * FROM " + TABLA_ENCABEZADO_MOVIMIENTO,null);
    }

    public Cursor getEncabezadoById(String id){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        return sqLiteDatabase.rawQuery(" SELECT * FROM " + TABLA_ENCABEZADO_MOVIMIENTO + " WHERE " + KEY_ID + "=?",new String[]{id});
    }



    public void deleteAllEncabezados (){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLA_ENCABEZADO_MOVIMIENTO,null,null);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLA_ENCABEZADO_MOVIMIENTO + "'");


    }


    /**Metodo que permite eliminar cualquier tabla y resetear su Key id*/
    public void borrarTablayResetKeyId (String tabla){
        //debido a que cuando se elimina una tabla los keyID se quedan en el ultimo registro que se ingreso
        //es decir si tenia 10 registros, el key id para el ultimo seria el 9 (0,1,2,3....9)
        //cuando elimino la tabla al siguiente nuevo registro se le coloca el 10 mas no inicia nuevamente desde 0
        //aveces queremos que cuando se elimina la tabla se reinicien los KeyId para eso es este metodo
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(tabla,null,null);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + tabla + "'");
    }


    public int getLastId(String tabla){
        //nos entrega el ultimo RowId que se haya insertado en la tabla
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT LAST_INSERT_ROWID() FROM " + tabla, null);
        res.moveToNext();
        return res.getInt(0);
    }

}
