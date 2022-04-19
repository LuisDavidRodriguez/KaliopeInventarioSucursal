package android.zyapi;

/** esta clase es muy importante saber que se debe de crear en ese paquete
 * android.zyapi
 * porque los metodos nativos utilizados aqui inician por el nombre del paquete, no se en que parte estan declarados esos metodos
 * si en la libresia so que esta en jniLibs aun no lo encuentro, pero si tu creas esta clase en una nueva aplicacion pero la creas en el nombre
 * del paquete de la aplicacion no encontrara los metodos porque el nombre del paquete es diferente,para que encuentre los metodos nativos debe de
 * crearse un nuevo paquete en main con este nombre de paquete, y asi cuando invoque a los metodos
 * los encontrara porque los metodos estan nombrados con este paquete en inicio y luego el nombre del metodo
 *
 * dejo el link donde encontre la info
 * https://developer.vuforia.com/forum/qcar-api/library-loaded-successfully-no-implementation-found
 *
 * no es muy relevante pero con eso me di una idea de lo que podia estar mal
 * */




public class CommonApi {
	private static CommonApi mMe = null;
	
	public CommonApi() {
	}
	
	// gpio
	public native int setGpioMode(int pin, int mode);
	public native int setGpioDir(int pin, int dir);
	public native int setGpioPullEnable(int pin, int enable);
	public native int setGpioPullSelect(int pin, int select);
	public native int setGpioOut(int pin, int out);
	public native int getGpioIn(int pin);
	//serialport  
	public native int openCom(String port, int baudrate, int bits, char event, int stop);
	public native int openComEx(String port, int baudrate, int bits, char event, int stop, int flags);
	public native int writeCom(int fd, byte[] buf, int sizes);
	public native int readCom(int fd, byte[] buf, int sizes);
	public native int readComEx(int fd, byte[] buf, int sizes, int sec, int usec);
	public native void closeCom(int fd);





	static {


		try {
			System.load("C:/Users/david/Documents/ejemploHandHeldChina/QS5501L_5501HDEMO_studio/QS5501DEMO1/app/src/main/jniLibs/armeabi/libzyapi_common.so");
			//System.loadLibrary("zyapi_common");
		} catch (UnsatisfiedLinkError e) {

			System.err.println("Native code library failed to load.\n" + e);
			System.exit(1);
		}

		//C:\Users\david\Documents\ejemploHandHeldChina\QS5501L_5501HDEMO_studio\QS5501DEMO1\app\src\main\jniLibs\armeabi\libzyapi_common.so

		//System.loadLibrary("zyapi_common");
	}
	
}
