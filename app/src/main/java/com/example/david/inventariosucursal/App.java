package com.example.david.inventariosucursal;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import android.zyapi.CommonApi;

import static com.example.david.inventariosucursal.Constant.KEY_INTENTFILTER_ESCANER;

/**Varios codigos reutilizados del SDK enviado de china
 *
 * esta "aplicacion" se llama desde el manifest, es la primera que se ejecuta al iniciar la app
 *
 * */

public class App extends Application {

    private static int pin_L = 84;// 5501L
    private static int pin_H = 68;// 5501H
    public static int pin = 68;// 5501H

    private static int mComFd = -1;
    static CommonApi mCommonApi;


    static App instance = null;


    public static boolean isCanprint = false;


    private MediaPlayer player;


    private boolean isOpen = false;
    private final int MAX_RECV_BUF_SIZE = 1024;
    private final static int SHOW_RECV_DATA = 1;
    private byte[] recv;
    private String strRead;


    public static StringBuffer sb1 = new StringBuffer();

    // SCAN按键监听 Botón SCAN monitor
    private ScanBroadcastReceiver scanBroadcastReceiver;


    Handler h;
    static Handler handler1 = new Handler();

    public App() {
        super.onCreate();
        instance = this;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        init();

        player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
    }


    public void init() {

        //iniciamos nuestros puertos serie
        openGPIO();//al quitar este la app se detiene porque se llama a initGPIO pero si se quitan ambos ni la impresora ni el escaner funcionan
        initGPIO();//al quitar esto el escaner funciona pero la impresora no!! son los enlaces para el microcontrolador que se comunica por puerto serie
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (mComFd > 0) {
                    open();
                    isOpen = true;
                    readData();
                    // 默认开启黑标 La marca negra está habilitada por defecto
                    App.send(new byte[] { 0x1F, 0x1B, 0x1F, (byte) 0x80, 0x04,
                            0x05, 0x06, 0x66 });
                } else {
                    isOpen = false;
                }
            }
        }, 2000);






        // 利用Handler更新UI Actualizar la interfaz de usuarioSupervisor con el controlador
        h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    if (msg.obj != null) {
                        String str = "" + msg.obj;
                        if (!str.trim().contains("##56")) {
                            if (!str.trim().contains("##55")) {
                                if (!str.trim().equals("start")) {

                                    //player.start();  //suspendemos el audio desde aqui lo hara cada activity

                                    Intent intentBroadcast = new Intent();
                                    intentBroadcast.setAction(Constant.KEY_INTENTFILTER_ESCANER);//com.qs.scancode esta es la key

                                    intentBroadcast.putExtra("code", str.trim());

                                    sendBroadcast(intentBroadcast);

                                }
                            }
                        }
                    }
                }
            }
        };


        //REGISTRAMOS NUESTRO BROADCAST QUE RECIBIRA EL BROADCAST QUE LANZA CUANDO SE PRECIONA EL BOTON DE ESCANER
        scanBroadcastReceiver = new ScanBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ismart.intent.scandown");
        this.registerReceiver(scanBroadcastReceiver, intentFilter);

    }






    public static void openGPIO() {
        System.err.println(" EN openGPIOOOOOOO   1\n");
        mCommonApi = new CommonApi();
        System.err.println(" EN openGPIOOOOOOO   2\n");

//		mCommonApi.setGpioDir(84, 0);
//		mCommonApi.getGpioIn(84);
//
//		mCommonApi.setGpioDir(84, 1);
//		mCommonApi.setGpioOut(84, 1);

        //preguinta el numero de modelo para asi asignar un pin diferente, en el caso de la que llego es el modelo 5501L
        //añadi la linea que muestra el modelo en la actividad Main en un Log
        if(Build.MODEL.equalsIgnoreCase("5501H")) {
            pin=pin_H;
        } else {
            pin=pin_L;
        }

        Log.i("Model", Build.MODEL);

        mCommonApi.setGpioDir(pin,0);
        mCommonApi.getGpioIn(pin);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mCommonApi.setGpioDir(pin, 1);
                mCommonApi.setGpioOut(pin, 1);

            }
        }, 1000);


    }

    public static void initGPIO() {
        // TODO Auto-generated method stub
         //de alguna manera devuelve un numero entero porque mComFd se inicializa en -1 y al ejecutarse este metodo y conectarse satisfacotiramente
        //al puerto serie entonces cambia de -1 a unnumero mayor a 0
        mComFd = mCommonApi.openCom("/dev/ttyMT1", 115200, 8, 'N', 1);

        if (mComFd > 0) {
            Toast.makeText(instance, "init success", Toast.LENGTH_SHORT).show();
        }
    }



    // 进入App拉高55和56脚
    public static void open() {
        /**
         * 1. Levante 55, 56 pies (APP-> Impresora) 1B 23 23 XXXX donde XXXX es el código ASCII: 56UP ie 1B 23 23
         * 35 36 55 50 El microcontrolador recibe un pull-up de 55, nivel de 56 pines
         */
        //
        //Entra y levanta 55 y 56 pies
        App.send(new byte[] { 0x1B, 0x23, 0x23, 0x35, 0x36, 0x55, 0x50 });

        //  Tire hacia abajo 55 pies al escanear
        // App.send(new byte[] { 0x1B, 0x23, 0x23, 0x35, 0x35, 0x44, 0x4E });

    }

    /**
     * Enviar datos
     */
    public static void send(byte[] data) {
        if (data == null)
            return;
        //si la conexion esta establecida exitosamente con el serie envia los datos
        if (mComFd > 0) {
            mCommonApi.writeCom(mComFd, data, data.length);
        }

    }




    /**
     * 读数据线程
     * Leer hilo de datos
     */
    private void readData() {
        new Thread() {
            public void run() {


                while (isOpen) {
                    //este while siempre esta corriendo mientras este iniciado el escaner

                    int ret = 0;
                    byte[] buf = new byte[MAX_RECV_BUF_SIZE + 1];
                    ret = mCommonApi.readComEx(mComFd, buf, MAX_RECV_BUF_SIZE,0, 0);

                    if (ret <= 0) {
                        Log.d("", "read failed!!!! ret:" + ret);

                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        continue; //mientras no haya informacion presente volvemos a reiniciar el while

                    } else {
                        // Log.e("", "1read success:");
                    }


                    recv = new byte[ret];
                    System.arraycopy(buf, 0, recv, 0, ret);

                    try {
                        strRead = new String(recv, "GBK");
                        Log.i("VALOR DE STRREAD",strRead);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    //https://www.aprenderaprogramar.com/index.php?option=com_content&view=article&id=961:stringbuffer-stringbuilder-java-ejemplo-diferencias-entre-clases-criterios-para-elegir-metodos-cu00914c&catid=58&Itemid=180
                    StringBuffer sb = new StringBuffer();

                    String str = byteToString(buf, ret);

                    if (str.contains("1C 00 0C 0F")) {
                        Intent mIntent = new Intent("NOPAPER");
                        instance.sendBroadcast(mIntent);
                        isCanprint = false;
                        return;
                    } else {
                        isCanprint = true;
                    }

                    for (int i = 0; i < recv.length; i++) {
                        if (recv[i] == 0x0D) {
                            sb.append("\n");
                        } else {
                            sb.append((char) recv[i]);
                        }
                    }

                    String s = sb.toString();
                    if (strRead != null) {
                        Message msg = handler.obtainMessage(SHOW_RECV_DATA);
                        msg.obj = s;
                        msg.sendToTarget();
                    }
                }//fin de while
            }
        }.start();
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_RECV_DATA:
                    String barCodeStr1 = (String) msg.obj;
                    Log.e("", "1read success:" + barCodeStr1);
                    if (barCodeStr1.trim() != "") {
                        if (isOpen) {
                            if (!barCodeStr1.trim().contains("")) {
                                if (!barCodeStr1.trim().contains("##55")) {
                                    if (!barCodeStr1.trim().equals("start")) {
                                        if (barCodeStr1.trim().length() != 0) {

                                            Message m = new Message();
                                            m.what = 0x123;
                                            m.obj = barCodeStr1;
                                            h.sendMessage(m);
                                        }

                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        };
    };






    int num = 1;
    Handler mHanlder = new Handler();
    Runnable run_getData = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (num > 1) {
                num = 1;
                mHanlder.removeCallbacks(run_getData);
                Message m = new Message();
                m.what = 0x123;
                Log.e("iiiiiii", "发送GET请求 Enviar una solicitud GET");
                try {
                    m.obj = sb1.toString();
                    Log.e("返回信息：Info de retorno", "" + m.obj);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                h.sendMessage(m);
            } else {
                num++;
                mHanlder.postDelayed(run_getData, 100);
            }
        }
    };





    // 执行扫描，即拉低74、75脚后再拉高 Realice un escaneo, es decir, jale los 74, 75 pies hacia abajo y luego jale hacia arriba
    public static void openScan() {
        /**
         * 3、拉低55脚电平(APP->Printer) 1B 23 23 XXXX 其中XXXX为ASCII码:55DN 即1B 23 23 35
         * Baje el nivel de 55 pines (APP-> Impresora) 1B 23 23 XXXX donde XXXX es el código ASCII: 55DN es 1B 23 23 35
         * 35 44 4E
         */
        // 发送扫描指令 Enviar comando de escaneo
        App.send(new byte[] { 0x1B, 0x23, 0x23, 0x35, 0x35, 0x44, 0x4E });

        if(pin==pin_L){
            // 拉低GPIO口 Tire hacia abajo el puerto GPIO
            mCommonApi.setGpioDir(74, 1);
            mCommonApi.setGpioOut(74, 0);
            mCommonApi.setGpioDir(75, 1);
            mCommonApi.setGpioOut(75, 0);

            handler1.postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    // 拉低GPIO口，点亮扫描头 Baje el puerto GPIO y encienda el cabezal de escaneo
                    mCommonApi.setGpioDir(74, 1);
                    mCommonApi.setGpioOut(74, 1);
                    mCommonApi.setGpioDir(75, 1);
                    mCommonApi.setGpioOut(75, 1);

                }
            }, 50);
        }
    }




    public String byteToString(byte[] b, int size) {
        byte high, low;
        byte maskHigh = (byte) 0xf0;
        byte maskLow = 0x0f;

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < size; i++) {
            high = (byte) ((b[i] & maskHigh) >> 4);
            low = (byte) (b[i] & maskLow);
            buf.append(findHex(high));
            buf.append(findHex(low));
            buf.append(" ");
        }
        return buf.toString();
    }

    private char findHex(byte b) {
        int t = new Byte(b).intValue();
        t = t < 0 ? t + 16 : t;
        if ((0 <= t) && (t <= 9)) {
            return (char) (t + '0');
        }
        return (char) (t - 10 + 'A');
    }



    public static Bitmap createQRImage(String url, int width, int height) {
        //USA LAS LIBRERIAS DE ZXING
        //TUVE QUE ALADIR UNA NUEVA LIBRERIA "core.jar" en Buield edit Libraries and dependencies,
        //esa libreria la copie del proyecto de ejemplo HandHeld enviado desde china y entonces
        //copie el codigo y marco los objetos que no se podian encontrar, al añadir la libreria
        //me permitio usar el Alt + intro y se ocmenzaron a importar de zxing

        //la otra opcion tambien es añadior la libreria zxing en el gradle module app
        //implementation 'com.journeyapps:zxing-android-embedded:3.5.0'

        try {
            //Juzgando la legalidad de la URL
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "GBK");
            //Conversión de datos de imagen usando conversión de matriz
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);
            // bitMatrix = deleteWhite(bitMatrix);//  Quitar el borde blanco
            bitMatrix = deleteWhite(bitMatrix);//  Quitar el borde blanco
            width = bitMatrix.getWidth();
            height = bitMatrix.getHeight();
            int[] pixels = new int[width * height];
            // Aquí, de acuerdo con el algoritmo del código bidimensional, las imágenes del código bidimensional se generan una por una.
            // Dos bucles for son el resultado de un escaneo de imagen
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888 Generar un formato de imagen QR usando ARGB_8888
            Bitmap bitmap = Bitmap
                    .createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }




    private static BitMatrix deleteWhite(BitMatrix matrix) {
        //COPIADO DE HAND HELD CHINA
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }



    public static void closeCommonApi() {

        if (mComFd > 0) {
            mCommonApi.setGpioMode(84, 0);
            mCommonApi.setGpioDir(84, 0);
            mCommonApi.setGpioOut(84, 0);
            mCommonApi.closeCom(mComFd);
        }

    }


    // SCAN按键的监听 Botón de escaneo de monitoreo
    class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            openScan();
        }
    }












}
