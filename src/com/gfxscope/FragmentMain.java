package com.gfxscope;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
//import android.media.AudioManager;
//import android.media.ToneGenerator;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import com.TCP_Client.TCPListener;
//import com.TCP_Client.TcpClient;
import com.TCP_Client.TCPCommunicator;
import com.gfxscope.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.widget.VerticalSeekBar;


@SuppressLint({ "ResourceAsColor", "NewApi" }) public class FragmentMain extends Fragment implements TCPListener{

	protected static final int INVALID_POINTER_ID = 0;
    static final String PREF_IPadress = "IPadress";
    static final String PREF_MINMAX = "MINMAX";
    static final int DELTA = 50;
private final static Handler mHandler = new Handler();
    private final static int OTSTUP_SLEVA = 11;
    private final static int OTSTUP_SPRAVA = 10;
    private final static int ILI9341_HEIGHT = 480;
    private final static int KOL_KLETOK_U = 10;
    private final static int ADC_9288_diapason =256;
    private final static double ADC9288_pol_diapasona =128;//(ADC_9288_diapason/2);
    private final static double  ADC_9288_Ref= 1.024;
    private final static double  AMP2_gain =10.0;
    private final static int freq0= 54000000;
    //private SharedPreferences.Editor prefEditor;
	// Логгирование внутри IDE
	private static final String LOG_TAG = "FlankLOGS";
    public static int Xmax_scale_x_max = ((ILI9341_HEIGHT - (OTSTUP_SLEVA + OTSTUP_SPRAVA)));
    public static int Connecting;
    //private static TcpClient mTcpClient;
	public static TCPCommunicator tcpClient;
	//private ProgressDialog dialog;
	public static String currentUserName;
	public static Editor prefEditor;
    public static int SIZE_BUF_SETTINGS = 64;
    static float CH1_correct=1;
    static float CH2_correct=1;
    static SharedPreferences settings;
    static SharedPreferences settings2;
private static GraphView graph;
private static Runnable mTimerScrollLeft;
private static Runnable mTimerScrollRighr;
private static Runnable mTimerRefreshRMS;
private static Runnable mTimerDont_read_settings;
private static Runnable mTimerGrafik_refresh;
private static Runnable mTimerSettings_refresh;
private static Runnable mTimerBitrate_refresh;
private static Runnable mTimer_scrBt;
private static int pause=1;
private static int bufpoz;
private static int Xpoz_for_RMS;
private static int Xpoz;
 private static SeekBar seekBar = null;
 private static com.jjoe64.graphview.GraphView grafik= null;
 private static Button screen_bt_pause = null;
 private static Button screen_bt_plus = null;
 private static Button screen_bt_minus = null;
 private static Button screen_bt_free = null;
 private static Button screen_bt_flow = null;
 private static Button screen_bt_ch1plus = null;
 private static Button screen_bt_ch1minus = null;
 private static Button screen_bt_ch2plus = null;
 private static Button screen_bt_ch2minus = null;
 private static Button screen_bt_Tplus = null;
 private static Button screen_bt_Tminus = null;
 private static Button send = null;
    private static int rxCount;
    private static int rxCount_old;
    private static int scale_x=1;
    private volatile static int Xmax = Xmax_scale_x_max;
    private static int SIZE_BUF_RX_for_MK = 100*1024;
    private static int SIZE_BUF_RX = 20*10*(SIZE_BUF_RX_for_MK+SIZE_BUF_SETTINGS);
    private static byte[] ADC_Buff = new byte[SIZE_BUF_RX];
    private static int scale_t=1;
    private static int scale_t_old=1;
    private static int size_buff=(Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS ;//100000;
    private static TUsinhros_type Usinhros_type=TUsinhros_type.FRONT;
    private static Tgrafic_type grafic_type = Tgrafic_type.AVERAGE;
    private static int KOL_KLETOK_T = 17;
    private static int KOL_KLETOK_T_old = 17;
    private static byte ADC_Interleaved_mode;
    private static byte stream_viev;
    private static byte play = 0;
    private static byte dont_read_settings;
    private static byte ADC_CH1_ON = 1;
    private static byte ADC_CH2_ON = 1;
    private static long ADC_freq=freq0;
    private static int CH1_n_delitel=6;
    private static int CH2_n_delitel=6;
    private static int CH1_dc_ac;
    private static int CH2_dc_ac;
    private static int Usinhros=132;
    private static int Usinhros_for_CH=1;
    private static int Uoffset_Chanal1;
    private static int Uoffset_Chanal2;
    private static double CH1_delitel=0;
    private static double CH2_delitel=0;
    private static int auto_time;
    private static int auto_sinhros;
    private static int auto_CH1_Udel;
    private static int auto_CH2_Udel;
    private static byte disable_set_comands = 0;
    private static int settings_read_ok;
    private static int max_scale_t=32;
	private static Handler UIHandler = new Handler();
private final int     adres_KOL_KLETOK_T=6;
private final int     adres_ADC_freqH=7;
private final int     adres_ADC_freqL=8;
private final int     adres_screeen_osc_piksels=11;
private final int     adres_ADC_Interleaved_mode=12;
private final int     adres_CH1_n_delitel=13	;
private final int     adres_CH2_n_delitel=14;
private final int     adres_CH1_dc_ac=15	;
private final int     adres_CH2_dc_ac=16	;
private final int     adres_Usinhros=17	;
private final int     adres_Usinhros_for_CH=18;
private final int     adres_Usinhros_type=19	;
private final int     adres_Uoffset_Chanal1=20;
private final int     adres_Uoffset_Chanal2=21;
private final int     adres_scale_t=22;
private final int     adres_scale_x=23	;
private final int     adres_auto_time=24	;
private final int     adres_auto_sinhros=25;
private final int     adres_auto_CH1_Udel=26;
private final int     adres_auto_CH2_Udel=27	;
private final int     adres_CH1_correctH=28;
private final int     adres_CH1_correctL=29;
private final int     adres_CH2_correctH=30;
private final int     adres_CH2_correctL=31;
private final int     TRACK_BAR_COEFF = 1000;
    private final int t0=16;
    private final int t1=32;
    private final int t2=100;
    private final int t3=200;
    private final int t4=350;
    private final int t5=500;
    private final int t6=1000;
    private final int t7=2000;
    private final int t8=5000;
    private final int t9=10000;
    private final int t10=20000;
    private final int t11=50000;
    private final int t12=100000;
    private final int t13=200000;
    private final int t14=500000;
    private final int t15=1000000;
    private final int t16=5000000;
    private final int t17=10000000;
    private final int t18=30000000;
    private final int t19=60000000;
    private final int freq1= (int)((float)freq0*((float)t0/(float)t1));
    private final int freq2= (int)((float)freq0*((float)t0/(float)t2));
    private final int freq3= (int)((float)freq0*((float)t0/(float)t3));
    private final int freq4= (int)((float)freq0*((float)t0/(float)t4));
    private final int freq5= (int)((float)freq0*((float)t0/(float)t5));
    private final int freq6= (int)((float)freq0*((float)t0/(float)t6));
    private final int freq7= (int)((float)freq0*((float)t0/(float)t7));
    private final int freq8= (int)((float)freq0*((float)t0/(float)t8));
    private final int freq9= (int)((float)freq0*((float)t0/(float)t9));
    private final int freq10 = (int)((float)freq0 * ((float)t0 / (float)t10));
    private final int freq11= (int)((float)freq0*((float)t0/(float)t11));
    private final int freq12= (int)((float)freq0*((float)t0/(float)t12));
    private final int freq13= (int)((float)freq0*((float)t0/(float)t13));
    private final int freq14= (int)((float)freq0*((float)t0/(float)t14));
    private final int freq15= (int)((float)freq0*((float)t0/(float)t15));
    private final int freq16= (int)((float)freq0*((float)t0/(float)t16));
    private final int freq17= (int)((float)freq0*((float)t0/(float)t17));
    private final int freq18= (int)((float)freq0*((float)t0/(float)t18));
    private final int freq19 = (int)((float)freq0 * ((float)t0 / (float)t19));
    byte[] wav_buff = new byte[(SIZE_BUF_RX_for_MK+SIZE_BUF_SETTINGS)];
    int wav_buff_poz_read=0;
    float historicX = Float.NaN, historicY = Float.NaN;
    DataPoint[] ch1_values = new DataPoint[(int)(Xmax)];
    DataPoint[] ch2_values = new DataPoint[(int)(Xmax)];
    Thread thread_play =null;
private LineGraphSeries<DataPoint> series1;
private LineGraphSeries<DataPoint> series2;
private LineGraphSeries<DataPoint> series3;
private int dont_use_bufpoz=0;
private byte count_CH1_freq=1;
private byte count_CH2_freq=1;
private int CH1_freq;
private int CH1_freq_temp;
private int CH2_freq;
private int CH2_freq_temp;
private byte CH1_duty_cycl;
private byte CH2_duty_cycl;
private int CH1_duty_cycl_temp;
private int CH2_duty_cycl_temp;
private byte count_CH1_duty_cycl=1;
private byte count_CH2_duty_cycl=1;
private byte minmax = 0;
private int temp_rxCount=0;
private int Xpoz_old = 1;
private int bufpoz_old;
private double U1;
private double U2;
//public static int     rxCount_vivod;
private int count_bar;
 private VerticalSeekBar verticalSeekBar_usync=null;
 private TextView textView_CH1avr = null;
 private TextView textView_CH1rms = null;
 private TextView textView_CH1pp = null;
 private TextView textView_CH1freq = null;
 private TextView textView_CH1dutyc = null;
 private TextView textView_CH2avr = null;
 private TextView textView_CH2rms = null;
 private TextView textView_CH2pp = null;
 private TextView textView_CH2freq = null;
 private TextView textView_CH2dutyc = null;
 private TextView text_bitrate = null;
 private int view_scrbt=0;
 private int first_load=0;
 private Button CH1_ACDC = null;
 private Button CH2_ACDC = null;
 private Button t_auto = null;
 private Button sync_type = null;
 private Button sync_auto = null;
 private Button ch1_auto = null;
 private Button ch2_auto = null;
 private Button ExitBtn = null;
 private Button MenuBtn = null;
 private Button SaveBtn = null;
 private Button OpenBtn = null;
    private int AlternativVoltDiapason;
    private int bitrate;
	private int error_bitrate=0;
    private float mLastTouchX;
    private float mLastTouchY;
    private  int tfree;
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
	    	if ((pause==1)&&(play==0)&&(fromUser==true)){
	          bufpoz = (int)(seekBar.getProgress() * TRACK_BAR_COEFF);
	          //if ((bufpoz % 2) != 0) bufpoz++;
	        	 Grafik_refresh(bufpoz, true);
	    	}
	    }
	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {

	    }
	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
            getRMS();
	    }

		};
    private AudioTrack aTrack=null;
	//======================================================================================================
	private String CurrentFragmentTag = "fragment_main";

    static int get_grafic_type(  ) {
    	if (grafic_type == Tgrafic_type.AVERAGE) return 0;
    	else return 1;
    }
        
    static void set_grafic_type( int type ) {
    	if (type==0)grafic_type = Tgrafic_type.AVERAGE;
    	else grafic_type = Tgrafic_type.MINMAX;
    }
    
   //======================================================================================================
     public static  void osc_set_param(String param) {
  	   if (disable_set_comands == 1) return;

          TCPCommunicator.writeToSocket(param + "\r\n", UIHandler);

  }

  	// Метод выводит переданный в параметрах фрагмент
	private void LoadFragment(Fragment NewFragment, String NewFragmentTag) {
  	    FragmentManager fm = getFragmentManager();
  	    Fragment currFragment  =  fm.findFragmentByTag(CurrentFragmentTag);
  	    Fragment newFragment   =  fm.findFragmentByTag(NewFragmentTag);
  	    FragmentTransaction ft =  fm.beginTransaction();
  	    if (currFragment != null) {
  	        ft.hide(currFragment);
  	    }

  	    if (newFragment == null) {
  	    	// Создаём фрагменты впервые (затем будем просто показывать/прятать нужный фрагмент)
  	    	if (NewFragmentTag.equals(getString(R.string.fragment_main)))  	  {
  	    	    newFragment  = new FragmentMain();
  	    	} else if (NewFragmentTag.equals(getString(R.string.fragment_settings))) {
  	    	    newFragment  = new FragmentSettings();
  	    	}
  	    	// Добавляем фрагмент в менеджер транзакций
  	        ft.add(R.id.FragmentContainer, newFragment, NewFragmentTag);
  	    } else {
  		    // Остальные фрагменты статичные, просто прячем текущий и показываем нужный
  		    // Проверка для начального запуска, когда NewFragmentTag == CurrentFragmentTag == "fragment_main"
  		    if (NewFragmentTag != CurrentFragmentTag)
  		    {
  			    ft.show(newFragment);
  		    }
  	    }
  	    ft.commit();
  	}

  //======================================================================================================
 @Override
 public void onPrepareOptionsMenu(Menu menu) {
//     if (mTcpClient != null) {
//         // if the client is connected, enable the connect button and disable the disconnect one
//         menu.getItem(1).setEnabled(true);
//         menu.getItem(0).setEnabled(false);
//     } else {
//         // if the client is disconnected, enable the disconnect button and disable the connect one
//         menu.getItem(1).setEnabled(false);
//         menu.getItem(0).setEnabled(true);
//     }
     super.onPrepareOptionsMenu(menu);
 }
  
//======================================================================================================
// Обработчик пунктов меню (НЕ ActionBar)
// return true означает, что клик будет обработан данным фрагментом, return false - что клик будет обработан MainActivity
 @Override
 public boolean onOptionsItemSelected(MenuItem item) {
     // Handle item selection
     switch (item.getItemId()) {
		case 1:
//			// ПОДКЛЮЧИТЬСЯ
//             new ConnectTask().execute("");
//Log.d(LOG_TAG, "ПОДКЛЮЧИТЬСЯ");
			return true;
		case 2:
//			// ОТКЛЮЧИТЬСЯ
//            if (mTcpClient == null) {
//                return true;
//            }
//            // disconnect
//            mTcpClient.stopClient();
//            mTcpClient = null;
//Log.d(LOG_TAG, "ОТКЛЮЧИТЬСЯ");
			return true;
			case 3:
				// Открыть файл
				setRetainInstance(true);
				  Intent intent = new Intent()
			        .setType("*/*")
			        .setAction(Intent.ACTION_GET_CONTENT);
			        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
				return true;
			case 4:
				// Сохранить файл
				// (1) get today's date
			    Date today = Calendar.getInstance().getTime();
			    // (2) create a date "formatter" (the date format we want)
			    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss", Locale.US);
			    // (3) create a new String using the date format we want
			    String Name = formatter.format(today);
				//Toast.makeText(getActivity(),"save to " + extStore+dateTime+".OSC", Toast.LENGTH_LONG).show();
				SaveWurfs(Name+".OSC");
				return true;
			case 5:
				// НАСТРОЙКИ
				return false;
			case 6:
				// Скрин
				return false;
			case 7:
				// ВЫХОД. Завершаем работу приложения
				return false;
			default: return super.onOptionsItemSelected(item);
     }
 }

	@Override
    public void onTCPMessageRecieved( byte[] bs) {

				// TODO Auto-generated method stub

                for (int i=0; i<bs.length; i++){

                    if (rxCount<ADC_Buff.length) 	{
                        	ADC_Buff[rxCount]=bs[i];
                            rxCount++;
                    }
                    else {
                    	while (dont_use_bufpoz==1);
                        rxCount=0;
                        //Xpoz=0;
                        bufpoz=0;
                        }
                    }
     }
 
	@Override
	public void onTCPConnectionStatusChanged(boolean isConnectedNow) {
		// TODO Auto-generated method stub


		if(isConnectedNow)
		{
			Connecting=1;

            int bufferSize = AudioTrack.getMinBufferSize(48000,
                    AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_8BIT);
             aTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_8BIT,
                    bufferSize, AudioTrack.MODE_STREAM);
            aTrack.play();

//			Activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					//dialog.hide();
//					Toast.makeText(getActivity(), "Connected to server", Toast.LENGTH_SHORT).show();
//				}
//			});

		}
		else {
		    Connecting=0;
            if (aTrack!=null) aTrack.stop();
        }
	}

 //======================================================================================================
	public void SaveWurfs(String path)
	{
		        File extStore = Environment.getExternalStorageDirectory();
		        File dir = new File(extStore+"/GFXscope");
		        boolean ok=false;
	         	if(!dir.exists()){
			          ok = dir.mkdir();
	         	}
		         File OSCfile = new File(extStore+"/GFXscope", path);
		         if(!OSCfile.exists()){
		        	  try {
		        	    ok =OSCfile.createNewFile();
		        	  }
		              catch(IOException ex){
		                  System.out.println(ex.getMessage());
		                  Toast.makeText(getActivity(),ex.toString(), Toast.LENGTH_LONG).show();
		              }
		         }
				if(ok==true)
				{
			        DataOutputStream dis = null;
			        try {
			            dis = new DataOutputStream(new FileOutputStream(OSCfile));
			        } catch (FileNotFoundException e) {
			            e.printStackTrace();
			            Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_LONG).show();
			        }
			        try {
			            dis.write(ADC_Buff, 0, rxCount);
				        } catch (IOException e) {
				        	e.printStackTrace();
				        	Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_LONG).show();
				        } finally {
			        }
			            if (OSCfile.length()<1000000)Toast.makeText(getActivity(),"save to"+ extStore+"/GFXscope, " + Long.toString(OSCfile.length()/1024)+"kbyte", Toast.LENGTH_LONG).show();
			            else Toast.makeText(getActivity(),"save to"+extStore+"/GFXscope, " + Long.toString(OSCfile.length()/(1024*1024))+"Mbyte", Toast.LENGTH_LONG).show();
			        try {
			            dis.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
				}
				else
				{
					// Файл не найден
					//Toast.makeText(getActivity(),"Error", Toast.LENGTH_LONG).show();
				}
	  }

	   //======================================================================================================
	private void pause() {
    	 if (mTimerGrafik_refresh!=null) {
    		 mHandler.removeCallbacks(mTimerGrafik_refresh);
    		 mTimerGrafik_refresh=null;
    	 }
    	 screen_bt_pause.setText("Play");
    	 pause=1;
    	 seekBar.setEnabled(true);
    	 seekBar.setMax( (int)(rxCount / TRACK_BAR_COEFF));
    	 seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
     }

	   //======================================================================================================
    private void start_refresh() {
      	 if (mTimerGrafik_refresh==null){
            mTimerGrafik_refresh = new Runnable() {

                @Override
                public void run() {
                	if (play==1) {

                    if (ADC_freq >= freq11)
                    {
                        bufpoz = rxCount - size_buff * 2;
                        Grafik_refresh(bufpoz, true);
                        temp_rxCount = rxCount;
                    }
                    else
                    {
                    	bufpoz = Grafik_refresh(bufpoz, true);
                    }
                   }
                   mHandler.postDelayed(this, 0);
                }
            };
            mHandler.postDelayed(mTimerGrafik_refresh, 0);
      	 }
    }

	   //======================================================================================================
	private void start() {
    	 pause=0;
    	 seekBar.setEnabled(false);

      	 start_refresh();
      	 if (tfree==1){
		  tfree=0;
		  scale_t=scale_t_old;
		  screen_bt_free.setTextColor(Color.WHITE) ;
      	 }
         if (mTimerScrollLeft!=null) {
            	mHandler.removeCallbacks(mTimerScrollLeft);
            	mTimerScrollLeft=null;

         }

         if (mTimerScrollRighr!=null) {
            	mHandler.removeCallbacks(mTimerScrollRighr);
            	mTimerScrollRighr=null;
         }
         screen_bt_minus.setTextColor(Color.WHITE) ;
         screen_bt_plus.setTextColor(Color.WHITE) ;
         screen_bt_pause.setText("Пауза");
     }

    //======================================================================================================
    private int conect_begin() {

//       if (mTcpClient == null) {
//            new ConnectTask().execute("");
       if (Connecting==1) TCPCommunicator.closeStreams();
       Connecting=0;

       //if (tcpClient!=null) tcpClient.cancelTask();
       TCPCommunicator.cancelTask();

       ConnectToServer();

       long t1=Calendar.getInstance().getTimeInMillis();
       long t2;
       while(Connecting==0) {
     	  t2=Calendar.getInstance().getTimeInMillis();
     	  if ((t2-t1)>1000) {
//				  // ОТКЛЮЧИТЬСЯ
//	              if (mTcpClient != null) {
//	            	 mTcpClient.stopClient();
//	                 mTcpClient = null;
//               }
	          return 0;
     		 //break;
     	  }
       }
//      }
       return 1;
    }

    //======================================================================================================
    private void conect_start() {
        String message;

        if (conect_begin()==1){
          play = 1;
          rxCount = 0;
          rxCount_old = 0;
          bufpoz=0;

          if (stream_viev==0) message = "get_buff 1";
          else  message = "get_buff 2";

          if (send!=null)send.setText("Стоп");

          osc_set_param(message);
          start();
        }

      }

    //======================================================================================================
    private void conect_stop() {
    	String message;
        play = 0;
        message = "get_buff 0";
        if (send!=null)send.setText("Старт");
        stream_viev=0;
        osc_set_param(message);
        osc_set_param(message);
        osc_set_param(message);
        pause();
    }

    //======================================================================================================
     private void dont_read_settings_waite() {
    	 dont_read_settings=1;

     	if (mTimerDont_read_settings==null){
     		mTimerDont_read_settings = new Runnable() {
                @Override
                public void run() {
                  dont_read_settings=0;
                  mHandler.removeCallbacks(mTimerDont_read_settings);
                  mTimerDont_read_settings=null;
                   //mHandler.postDelayed(this, 200);
                }
            };
            mHandler.postDelayed(mTimerDont_read_settings, 2000);
        	}
     	else {
            mHandler.removeCallbacks(mTimerDont_read_settings);
            mTimerDont_read_settings=null;
      		mTimerDont_read_settings = new Runnable() {
                @Override
                public void run() {
                  dont_read_settings=0;
                  mHandler.removeCallbacks(mTimerDont_read_settings);
                  mTimerDont_read_settings=null;
                   //mHandler.postDelayed(this, 200);
                }
            };
            mHandler.postDelayed(mTimerDont_read_settings, 2000);
     	}
     }

    @Override
    public void onResume() {
        super.onResume();

    	if (mTimerRefreshRMS==null){
            mTimerRefreshRMS = new Runnable() {
                @Override
                public void run() {
                   getRMS();
       	           // update display state
                   //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                   if (count_bar<5)count_bar++;
                   else{
                	   count_bar=0;
                   if ((play==1)&&(pause==0)){
              	     seekBar.setMax((int)(SIZE_BUF_RX / TRACK_BAR_COEFF));
            	     seekBar.setProgress((int)(rxCount / TRACK_BAR_COEFF));
                   }
                   else{
              	     seekBar.setMax( (int)(rxCount / TRACK_BAR_COEFF));
            	     seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
                   }
                   }
                   mHandler.postDelayed(this, 200);
                }
            };
            mHandler.postDelayed(mTimerRefreshRMS, 200);
        	}


     	if (mTimerSettings_refresh==null){
            mTimerSettings_refresh = new Runnable() {
                @Override
                public void run() {
               	   refresh();
                   mHandler.postDelayed(this, 100);
                }
            };
            mHandler.postDelayed(mTimerSettings_refresh, 100);
        }


    	if (mTimerBitrate_refresh==null){
            mTimerBitrate_refresh = new Runnable() {
                @Override
                public void run() {
               	 if (rxCount>rxCount_old) bitrate = (int)((rxCount - rxCount_old)*8)/(1024);
               	 else bitrate = 0;
               	 rxCount_old=rxCount;
               	 text_bitrate.setText(String.valueOf(bitrate)+" kbps");

               	 if ((play==1)&&(bitrate==0)&& (Usinhros_type!=TUsinhros_type.FALL_WAIT) && (Usinhros_type!=TUsinhros_type.FRONT_WAIT)){
               		 error_bitrate++;
               		 if (error_bitrate>2){
               			 error_bitrate=0;

             	         if (conect_begin()==1){
             	           osc_set_param("get_buff 1");
               	           Log.d(LOG_TAG, "ПОДКЛЮЧИЛИСЬ");
             	         }
               		 }
               	 }
               	 else{
               		error_bitrate=0;
               	 }
                   mHandler.postDelayed(this, 1000);
                }
            };
            mHandler.postDelayed(mTimerBitrate_refresh, 1000);
        	}

 	       conect_begin();

	       if ((play==1)&&(pause==0)){
	            start_refresh();
           }
    }

    @Override
    public void onPause() {
        mHandler.removeCallbacks(mTimerScrollLeft);
        mHandler.removeCallbacks(mTimerScrollRighr);
        screen_bt_minus.setTextColor(Color.WHITE) ;
        screen_bt_plus.setTextColor(Color.WHITE) ;

        mHandler.removeCallbacks(mTimerGrafik_refresh);

        mHandler.removeCallbacks(mTimerRefreshRMS);
        mHandler.removeCallbacks(mTimerSettings_refresh);
        mHandler.removeCallbacks(mTimerBitrate_refresh);

        mTimerScrollLeft=null;
        mTimerScrollRighr=null;
        mTimerRefreshRMS=null;
        mTimerGrafik_refresh=null;
        mTimerSettings_refresh=null;
        mTimerBitrate_refresh=null;

        super.onPause();
    }
    
    private void сh1_uplus(){
      	String message;
        dont_read_settings_waite();
        if (CH1_n_delitel < 12) CH1_n_delitel++;
        message=("CH1_n_delitel " +  String.valueOf(CH1_n_delitel));
        set_u_ch1();
        osc_set_param(message);
         //dont_read_settings = 0;
    }

    private void сh1_uminus(){
     	String message;
        dont_read_settings_waite();
        if (CH1_n_delitel > 0) CH1_n_delitel--;
        message=("CH1_n_delitel " +  String.valueOf(CH1_n_delitel));
        set_u_ch1();
        osc_set_param(message);
         //dont_read_settings = 0;
    }
    
    private void сh2_uplus(){
    	String message;
        dont_read_settings_waite();
        if (CH2_n_delitel < 12) CH2_n_delitel++;
        message=("CH2_n_delitel " +  String.valueOf(CH2_n_delitel));
        set_u_ch2();
        osc_set_param(message);
         //dont_read_settings = 0;
   }
    
    private void сh2_uminus(){
      	String message;
        dont_read_settings_waite();
        if (CH2_n_delitel > 0) CH2_n_delitel--;
        message=("CH2_n_delitel " +  String.valueOf(CH2_n_delitel));
        set_u_ch2();
        osc_set_param(message);
         //dont_read_settings = 0;
    }
    
	private void time_del_plus() {
//    	if (Usinhros_type==TUsinhros_type.OFF)max_scale_t = 64;//ADC1_MAX/Xmax_scale_x_max;
//  	    else  max_scale_t=16;

		// TODO Auto-generated method stub
	     if (scale_x >= 2)
	     {
	         scale_x /= 2;
	         Xmax = (Xmax_scale_x_max / scale_x);
	         if ((Xmax % 2) != 0) Xmax++;
	         osc_set_param("scale_x " +  String.valueOf(scale_x));
	         Grafik_refresh(bufpoz,  false);

	     }
         else if (stream_viev == 1)
         {
             if (scale_t < 2048)
             {
                 scale_t *= 2;
                 if (scale_t > 4096) scale_t = 4096;
             }
             Grafik_refresh(bufpoz,  false);
             return;
         }
	     else if (ADC_Interleaved_mode == 1)
	     {
	         ADC_Interleaved_mode = 0;
	         osc_set_param("ADC_Interleaved_mode 0");
	     }
	     else if ((play == 1) && (ADC_freq == freq0)) ADC_freq = freq1;
	     else if (scale_t == 1) {
	    	 scale_t = 2;
             osc_set_param("ADC_freq " + String.valueOf(ADC_freq));
             osc_set_param("scale_t " + String.valueOf(scale_t) );
	    	 Grafik_refresh(bufpoz,  false);
	     }
	     else
	     {
	         if ((scale_t < max_scale_t)||(pause==1)) {
	        	   scale_t *= 2;
                   osc_set_param("ADC_freq " + String.valueOf(ADC_freq));
                   osc_set_param("scale_t " + String.valueOf(scale_t) );
	        	   Grafik_refresh(bufpoz,  false);
	        	 }
	         else
	         {
	             if (play == 1)
	             {
	                 if (ADC_freq >= freq1) ADC_freq = freq2;
	                 else if (ADC_freq >= freq2) ADC_freq = freq3;
	                 else if (ADC_freq >= freq3) ADC_freq = freq4;
	                 else if (ADC_freq >= freq4) ADC_freq = freq5;
	                 else if (ADC_freq >= freq5) ADC_freq = freq6;
	                 else if (ADC_freq >= freq6) ADC_freq = freq7;
	                 else if (ADC_freq >= freq7) ADC_freq = freq8;
	                 else if (ADC_freq >= freq8) ADC_freq = freq9;
	                 else if (ADC_freq >= freq9) ADC_freq = freq10;
	                 else if (ADC_freq >= freq10) ADC_freq = freq11;
	                 else if ((ADC_freq >= freq11) && (auto_time == 0)) ADC_freq = freq12;
	                 else if ((ADC_freq >= freq12) && (auto_time == 0)) ADC_freq = freq13;
	                 else if ((ADC_freq >= freq13) && (auto_time == 0)) ADC_freq = freq14;
	                 else if ((ADC_freq >= freq14) && (auto_time == 0)) ADC_freq = freq15;
	                 else if ((ADC_freq >= freq15) && (auto_time == 0)) ADC_freq = freq16;
	                 else if ((ADC_freq >= freq16) && (auto_time == 0)) ADC_freq = freq17;
	                 else if ((ADC_freq >= freq17) && (auto_time == 0)) ADC_freq = freq18;
	                 else if ((ADC_freq >= freq18) && (auto_time == 0)) ADC_freq = freq19;
	                 else
	                 {
	                    osc_set_param("ADC_freq " + String.valueOf(ADC_freq));
	                    osc_set_param("scale_t " + String.valueOf(scale_t) );
	                     return;
	                 }
	             }
	         }
	     }
	                    osc_set_param("ADC_freq " + String.valueOf(ADC_freq));
	                    osc_set_param("scale_t " + String.valueOf(scale_t));
						return ;
	}
   
	private void t_plus(){
     	String message;
        dont_read_settings_waite();
        time_del_plus();
        double vremya;
        if (ADC_Interleaved_mode == 0) vremya = ((double)(Xmax * scale_t) / (ADC_freq)) / KOL_KLETOK_T;
        else vremya = ((double)(Xmax * scale_t) / (ADC_freq * 2)) / KOL_KLETOK_T;
        if (vremya < 1)
        {
            vremya = vremya * 1000;
            if (vremya >= 1)  message= String.format("%.1f", vremya) + " мc";
            else
            {
                vremya = vremya * 1000;
                if (vremya >= 1) message = String.format("%.0f", vremya) + " мкc";
                else message = String.format("%.0f", vremya*1000) + " нc";
            }
        }
        else message = String.format("%.1f", vremya) + " c";
        t_auto.setText(message);
         //dont_read_settings = 0;

    }
    
	private void time_del_minus() {
//    	if (Usinhros_type==TUsinhros_type.OFF)max_scale_t = 64;//ADC1_MAX/Xmax_scale_x_max;
//  	    else  max_scale_t=16;
		// TODO Auto-generated method stub
        if (stream_viev == 1)
        {
            if (scale_t >= 4) scale_t /= 2;
            else if (scale_t > 1) scale_t = 1;
            else if (scale_x <= 8)
            {
                scale_x *= 2;
                Xmax = (Xmax_scale_x_max / scale_x);
                osc_set_param("scale_x " +  String.valueOf(scale_x) );
            }
            Grafik_refresh(bufpoz,  false);
            return;
        }
        else if  ((play==1) && (ADC_freq<freq1)){
	        if (ADC_freq<=freq19) ADC_freq=freq18;
	        else  if (ADC_freq<=freq18) ADC_freq=freq17;
			else  if (ADC_freq<=freq17) ADC_freq=freq16;
	        else  if (ADC_freq<=freq16) ADC_freq=freq15;
			else  if (ADC_freq<=freq15) ADC_freq=freq14;
	        else  if (ADC_freq<=freq14) ADC_freq=freq13;
			else  if (ADC_freq<=freq13) ADC_freq=freq12;
			else  if (ADC_freq<=freq12) ADC_freq=freq11;
			else  if (ADC_freq<=freq11) ADC_freq=freq10;
			else  if (ADC_freq<=freq10) ADC_freq=freq9;
			else  if (ADC_freq<=freq9) ADC_freq=freq8;
			else  if (ADC_freq<=freq8) ADC_freq=freq7;
			else  if (ADC_freq<=freq7) ADC_freq=freq6;
			else  if (ADC_freq<=freq6) ADC_freq=freq5;
			else  if (ADC_freq<=freq5) ADC_freq=freq4;
			else  if (ADC_freq<=freq4) ADC_freq=freq3;
			else  if (ADC_freq<=freq3) ADC_freq=freq2;
			else  if (ADC_freq<=freq2) ADC_freq=freq1;
        }
        else {
	           if (scale_t >= 4) {
	        	   scale_t /= 2;
                   osc_set_param("ADC_freq " + String.valueOf(ADC_freq));
                   osc_set_param("scale_t " + String.valueOf(scale_t) );
	        	   Grafik_refresh(bufpoz, false);
	           }
		           else if (scale_t > 1) {
		        	   scale_t = 1;
	                   osc_set_param("ADC_freq " + String.valueOf(ADC_freq));
	                   osc_set_param("scale_t " + String.valueOf(scale_t) );
		        	   Grafik_refresh(bufpoz, false);
		           }
			       else if ((play == 1) && (ADC_freq <= freq1)) ADC_freq = freq0;
				   else if ((ADC_CH2_ON == 0) && (ADC_Interleaved_mode == 0) && (play == 1))
				   {
					ADC_Interleaved_mode = 1;
					osc_set_param("ADC_Interleaved_mode 1");
				   }
				   else if (scale_x <=8)
				   {
					scale_x *= 2;
					Xmax = (Xmax_scale_x_max / scale_x);
					if ((Xmax % 2) != 0) Xmax++;
					osc_set_param("scale_x " +  String.valueOf(scale_x) );
					Grafik_refresh(bufpoz, false);

				   }
					else{
						 osc_set_param("ADC_freq " +  String.valueOf(ADC_freq) );
						 osc_set_param("scale_t " + String.valueOf(scale_t) );
						 return;
					}
		        }
if (ADC_freq>freq0)ADC_freq=freq0;
osc_set_param("ADC_freq " +  String.valueOf(ADC_freq));
osc_set_param("scale_t " +  String.valueOf(scale_t) );
return;
}
    
	private void t_minus(){
      	String message;
        dont_read_settings_waite();

        //timer_dont_read_settings.Enabled = true;
        time_del_minus();
        double vremya;
        if (ADC_Interleaved_mode == 0) vremya = ((double)(Xmax * scale_t) / (ADC_freq)) / KOL_KLETOK_T;
        else vremya = ((double)(Xmax * scale_t) / (ADC_freq * 2)) / KOL_KLETOK_T;
        if (vremya < 1)
        {
            vremya = vremya * 1000;
            if (vremya >= 1)  message= String.format("%.1f", vremya) + " мc";
            else
            {
                vremya = vremya * 1000;
                if (vremya >= 1) message = String.format("%.0f", vremya) + " мкc";
                else message = String.format("%.0f", vremya*1000) + " нc";
            }
        }
        else message = String.format("%.1f", vremya) + " c";
        t_auto.setText(message);

         //dont_read_settings = 0;
    }
    
	private void ConnectToServer() {
		//setupDialog();
		tcpClient = TCPCommunicator.getInstance();
		TCPCommunicator.addListener(this);
//		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//		tcpClient.init(settings.getString(EnumsAndStatics.SERVER_IP_PREF, "192.168.1.67"),
//				Integer.parseInt(settings.getString(EnumsAndStatics.SERVER_PORT_PREF, "12345")));

        settings = getActivity().getSharedPreferences(PREF_IPadress, Context.MODE_MULTI_PROCESS);
        String name = settings.getString(PREF_IPadress,"sgfxscope");
        //TcpClient.SERVER_IP=name;
		tcpClient.init(name, 12345);

	}
	
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Отображаем содержимое фрагмента fragment_top10
		View view =  inflater.inflate(R.layout.fragment_main, null);
		setHasOptionsMenu(true);

        //ConnectToServer();

        settings2 = getActivity().getSharedPreferences(PREF_MINMAX, Context.MODE_MULTI_PROCESS);
        int temp = settings2.getInt(PREF_MINMAX, 0);
   	    if (temp==1) {
		   FragmentMain.set_grafic_type(1);
	    }
	    else {
		   FragmentMain.set_grafic_type(0);
	    }


		return view;
    }
    
//	   private ScaleGestureDetector mScaleDetector;
//	   private float mScaleFactor = 1.f;
//	   private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//	       @Override
//	       public boolean onScale(ScaleGestureDetector detector) {
//	       mScaleFactor *= detector.getScaleFactor();
//
//	       // Don't let the object get too small or too large.
//	       mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
//
//	       //invalidate();
//	       return true;
//	     }
//	   }
	
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==123 ) && (resultCode==-1)) {
            Uri selectedfile = data.getData(); //The uri with the location of the file
            LoadAndDisplayWurfs(selectedfile.getPath());
        }
    }

	

//	private void setupDialog() {
//		dialog = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
//		dialog.setTitle("Loading");
//		dialog.setMessage("Please wait...");
//		dialog.setIndeterminate(true);
//		dialog.show();
//	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
	    super.onViewCreated(view, savedInstanceState);

	  getActivity().getWindow().setFlags(
	    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
	    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

	  show_graf(bufpoz);

	  text_bitrate = (TextView) getView().findViewById(R.id.Text_bitrate);
  	  textView_CH1avr = (TextView) getView().findViewById(R.id.textView_CH1avr);
  	  textView_CH1rms = (TextView) getView().findViewById(R.id.textView_CH1rms);
  	  textView_CH1pp = (TextView) getView().findViewById(R.id.textView_CH1pp);
  	  textView_CH1freq = (TextView) getView().findViewById(R.id.textView_CH1freq);
  	  textView_CH1dutyc = (TextView) getView().findViewById(R.id.textView_CH1dutyc);
  	  textView_CH2avr = (TextView) getView().findViewById(R.id.textView_CH2avr);
  	  textView_CH2rms = (TextView) getView().findViewById(R.id.textView_CH2rms);
  	  textView_CH2pp = (TextView) getView().findViewById(R.id.textView_CH2pp);
  	  textView_CH2freq = (TextView) getView().findViewById(R.id.textView_CH2freq);
  	  textView_CH2dutyc = (TextView) getView().findViewById(R.id.textView_CH2dutyc);
      seekBar = (SeekBar)getView().findViewById(R.id.seekBar_scroll);
      send = (Button) getView().findViewById(R.id.SendBtn);
	  CH1_ACDC = (Button) getView().findViewById(R.id.CH1_ACDC);
	  CH2_ACDC = (Button) getView().findViewById(R.id.CH2_ACDC);
      t_auto = (Button) getView().findViewById(R.id.T_auto);
      sync_type = (Button) getView().findViewById(R.id.Sync_type);
      sync_auto = (Button) getView().findViewById(R.id.Sync_auto);
      ch1_auto = (Button) getView().findViewById(R.id.CH1_auto);
      ch2_auto = (Button) getView().findViewById(R.id.CH2_auto);
      ExitBtn = (Button) getView().findViewById(R.id.ExitBtn);
      MenuBtn = (Button) getView().findViewById(R.id.MenuBtn);
      SaveBtn = (Button) getView().findViewById(R.id.SaveBtn);
      OpenBtn = (Button) getView().findViewById(R.id.OpenBtn);
      grafik= (com.jjoe64.graphview.GraphView)  getView().findViewById(R.id.OscillPlot);
      screen_bt_pause = (Button) getView().findViewById(R.id.screen_bt_pause);
  	  screen_bt_plus = (Button) getView().findViewById(R.id.screen_bt_plus);
      screen_bt_minus = (Button) getView().findViewById(R.id.screen_bt_minus);
      screen_bt_free = (Button) getView().findViewById(R.id.screen_bt_Tfree);
      screen_bt_flow = (Button) getView().findViewById(R.id.screen_bt_Tflow);
      screen_bt_ch1plus = (Button) getView().findViewById(R.id.screen_bt_CH1plus);
      screen_bt_ch1minus = (Button) getView().findViewById(R.id.screen_bt_CH1minus);
  	  screen_bt_ch2plus = (Button) getView().findViewById(R.id.screen_bt_CH2plus);
      screen_bt_ch2minus = (Button) getView().findViewById(R.id.screen_bt_CH2minus);
  	  screen_bt_Tplus = (Button) getView().findViewById(R.id.screen_bt_Tplus);
      screen_bt_Tminus = (Button) getView().findViewById(R.id.screen_bt_Tminus);

      int j=0;
      while (j < Xmax)
      {
           ch1_values[j] =  new DataPoint(j, 0);
           ch2_values[j] =  new DataPoint(j, 0);
           if (j<Xmax) j++;
           else break;
      }

      //mScaleDetector = new ScaleGestureDetector(App.getContext(), new ScaleListener());

      screen_bt_pause.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

        	 if  (play==0){
        		 conect_start();
        	 }
        	 else{
        		 if (pause==0){
        			 pause();
        		 }
        		 else {
        			 start();
        		 }
        	 }
            seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
            view_scr_bt(1);
          }
      });
      screen_bt_plus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          	pause();
          	if (mTimerScrollLeft!=null){
          		mHandler.removeCallbacks(mTimerScrollLeft);
          		mTimerScrollLeft=null;
          		screen_bt_minus.setTextColor(Color.WHITE) ;
          	}
          	if (mTimerScrollRighr==null){
          	  screen_bt_plus.setTextColor(Color.RED) ;
              mTimerScrollRighr = new Runnable() {
                  @Override
                  public void run() {
                	if (bufpoz + ((Xmax * scale_t) / 25)*2< (rxCount )){
                  	////if (bufpoz<(rxCount-size_buff*1.5))
                  		  bufpoz += ((Xmax * scale_t) / 25)*2;
                  		////  bufpoz +=size_buff*1.5;
                          Grafik_refresh(bufpoz, false);
                          mHandler.postDelayed(this, 40);
                  	}
                  	else{
          		      mHandler.removeCallbacks(mTimerScrollRighr);
          		      mTimerScrollRighr=null;
          		      screen_bt_plus.setTextColor(Color.WHITE) ;
          		      screen_bt_pause.setText("Play");
  			          seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
          		      getRMS();
                  	}
                  }
              };
              mHandler.postDelayed(mTimerScrollRighr, 40);
          	}
          	else {
          		mHandler.removeCallbacks(mTimerScrollRighr);
          		mTimerScrollRighr=null;
          		screen_bt_plus.setTextColor(Color.WHITE) ;
          		screen_bt_pause.setText("Play");
          		seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
          		getRMS();
          	}
          	view_scr_bt(1);
          }
      });
      screen_bt_minus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          	pause();
          	if (mTimerScrollRighr!=null){
          		mHandler.removeCallbacks(mTimerScrollRighr);
          		mTimerScrollRighr=null;
          		screen_bt_plus.setTextColor(Color.WHITE) ;
          	}
          	if (mTimerScrollLeft==null){
          	  screen_bt_minus.setTextColor(Color.RED) ;
              mTimerScrollLeft = new Runnable() {
                  @Override
                  public void run() {
                	  if (bufpoz > ((Xmax * scale_t) / 25)*2){
                  	//if (bufpoz>size_buff*1.5){//SIZE_BUF_SETTINGS+(Xmax * scale_t) / 4
                         bufpoz-=((Xmax * scale_t) / 25)*2;
                          //	bufpoz -=size_buff*1.5;// (Xmax * scale_t) / 4;
                          Grafik_refresh(bufpoz, false);
                          mHandler.postDelayed(this, 40);
                  	}
                  	else {
                  		bufpoz=SIZE_BUF_SETTINGS;
                		    mHandler.removeCallbacks(mTimerScrollLeft);
                		    mTimerScrollLeft=null;
                		    screen_bt_minus.setTextColor(Color.WHITE) ;
                		    screen_bt_pause.setText("Play");
                		    seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
                		    getRMS();
                  	}
                  }
              };
              mHandler.postDelayed(mTimerScrollLeft, 40);
          	}
          	else {
          		mHandler.removeCallbacks(mTimerScrollLeft);
          		mTimerScrollLeft=null;
          		screen_bt_minus.setTextColor(Color.WHITE) ;
          		screen_bt_pause.setText("Play");
          		seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
          		getRMS();
          	}
          	view_scr_bt(1);
          }
      });


      screen_bt_free.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  if (tfree==0) {
        		  tfree=1;
        		  scale_t_old=scale_t;
        		  screen_bt_free.setTextColor(Color.RED) ;
        	  }
        	  else {
        		  tfree=0;
        		  scale_t=scale_t_old;
        		  screen_bt_free.setTextColor(Color.WHITE) ;
        	  }
          }
      });

      screen_bt_flow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dont_read_settings_waite();

                if ((stream_viev==0) || (play==0)) {
                    stream_viev=1;
                    osc_set_param("stream_viev 1");
                    screen_bt_flow.setTextColor(Color.RED) ;
                    if (play==0) {
                        conect_start();
                        osc_set_param("stream_viev 1");
                    }

                    Runnable  Runnable_play = new Runnable() {
                       public void run() {
                            //do time consuming operations
                       while((stream_viev==1)&& (play==1)) {
                           if (rxCount > SIZE_BUF_RX_for_MK * 2) {
                               int count = SIZE_BUF_RX_for_MK - 1;
                               int i = 0;
                               if (wav_buff_poz_read>rxCount)wav_buff_poz_read=rxCount;
                               int wav_buff_poz_read_temp = wav_buff_poz_read;

                               while (wav_buff_poz_read < rxCount) {
                                   if ((byte) ADC_Buff[wav_buff_poz_read] == (byte) 'o') {
                                       if (((byte) ADC_Buff[(wav_buff_poz_read)] == (byte) 'o') &&
                                               ((byte) ADC_Buff[(wav_buff_poz_read) + 1] == (byte) 's') &&
                                               ((byte) ADC_Buff[(wav_buff_poz_read) + 2] == (byte) 'c') &&
                                               ((byte) ADC_Buff[(wav_buff_poz_read) + 3] == (byte) ' ') &&
                                               ((byte) ADC_Buff[(wav_buff_poz_read) + 4] == (byte) 'v') &&
                                               ((byte) ADC_Buff[(wav_buff_poz_read) + 5] == (byte) '3')) {
                                           wav_buff_poz_read += SIZE_BUF_SETTINGS;
                                           break;
                                       }
                                   }
                                   wav_buff_poz_read++;
                               }

                               if (wav_buff_poz_read + count < rxCount) {
                                   while (i < count) {
                                       wav_buff[i] = ADC_Buff[wav_buff_poz_read];
                                       i++;
                                       wav_buff_poz_read++;
                                   }
                                   aTrack.write(wav_buff, 0, i);
                               } else wav_buff_poz_read = wav_buff_poz_read_temp;
                           }
                       }

                        }

                    };

                    thread_play = new Thread(Runnable_play);
                    thread_play.start();


                }
                else {
                    stream_viev=0;
                    osc_set_param("stream_viev 0");
                    screen_bt_flow.setTextColor(Color.WHITE) ;

                    if (thread_play != null) {
                        Thread dummy = thread_play;
                        thread_play = null;
                        dummy.interrupt();
                    }

                }
            }
      });
        screen_bt_ch1plus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  сh1_uplus();
        	  view_scr_bt(1);
          }
      });
      screen_bt_ch1minus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  сh1_uminus();
        	  view_scr_bt(1);
          }
      });
      screen_bt_ch2plus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  сh2_uplus();
        	  view_scr_bt(1);
          }
      });
      screen_bt_ch2minus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  сh2_uminus();
        	  view_scr_bt(1);
          }
      });
      screen_bt_Tplus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  t_plus();
        	  view_scr_bt(1);
          }
      });
      screen_bt_Tminus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  t_minus();
        	  view_scr_bt(1);
          }
      });




      grafik.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          }
      });

      grafik.setOnTouchListener(new View.OnTouchListener() {
    	// The ‘active pointer’ is the one currently moving our object.
    	  private int mActivePointerId = INVALID_POINTER_ID;
    	@Override
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View v, MotionEvent ev) {
    	      final int action = MotionEventCompat.getActionMasked(ev);
    	      // Let the ScaleGestureDetector inspect all events.
    	      //mScaleDetector.onTouchEvent(ev);

    	      switch (action) {
    	      case MotionEvent.ACTION_DOWN: {
    	          final int pointerIndex = MotionEventCompat.getActionIndex(ev);
    	          final float x = MotionEventCompat.getX(ev, pointerIndex);
    	          final float y = MotionEventCompat.getY(ev, pointerIndex);
    	          // Remember where we started (for dragging)
    	          mLastTouchX = x;
    	          mLastTouchY = y;
    	          // Save the ID of this pointer (for dragging)
    	          mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
    	          break;
    	      }
    	      case MotionEvent.ACTION_UP: {
    	          // Find the index of the active pointer and fetch its position
    	          final int pointerIndex =
    	                  MotionEventCompat.findPointerIndex(ev, mActivePointerId);
    	          final float x = MotionEventCompat.getX(ev, pointerIndex);
    	          final float y = MotionEventCompat.getY(ev, pointerIndex);
    	          // Calculate the distance moved
    	          final float moveX = x - mLastTouchX;
    	          final float moveY = y - mLastTouchY;
    	          //mPosX += moveX;
    	          //mPosY += moveY;
                if (Math.abs(moveX)>Math.abs(moveY)){
                if (moveX < -DELTA)
                {
                	if(pause==1){
                		int delta=(int)(moveX * ((float)Xmax/grafik.getWidth()) * scale_t * 2);
                		if ((delta % 2)!=0)delta++;
                	    bufpoz-= delta ;
                	    Grafik_refresh(bufpoz, false);
                	}
                	else{
                		t_plus();
                	}
                	//Toast.makeText(getActivity(),"Left", Toast.LENGTH_LONG).show();
                    return true;
                }
                else if (moveX > DELTA)
                {
                	if(pause==1){
                		int delta=(int)(moveX * ((float)Xmax/grafik.getWidth()) * scale_t * 2);
                		if ((delta % 2)!=0)delta++;

                	    if(bufpoz > delta)
                		    bufpoz-=delta;
                	    else bufpoz=0;
                	    Grafik_refresh(bufpoz, false);
                	}
                	else{
                		t_minus();
                	}
                	//Toast.makeText(getActivity(),"Right", Toast.LENGTH_LONG).show();
                    return true;
                }
                else view_scr_bt(0);
                }
                else {
                    if (moveY < -DELTA)
                    {
                    	if(pause==1){

                    	}
                    	else{
                    		if (x>grafik.getWidth()/2){
                    			сh2_uplus();
                    		}
                    		else{
                    			сh1_uplus();
                    		}
                    	}

                    	//Toast.makeText(getActivity(),"up", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    else if (moveY > DELTA)
                    {
                    	if(pause==1){

                    	}
                    	else{
                    		if (x>grafik.getWidth()/2){
                    			сh2_uminus();
                    		}
                    		else{
                    			сh1_uminus();
                    		}
                    	}

                    	//Toast.makeText(getActivity(),"down", Toast.LENGTH_LONG).show();
                        return true;
                    }
                    else view_scr_bt(0);
                }
    	          // Remember this touch position for the next move event
    	          mLastTouchX = x;
    	          mLastTouchY = y;
    	          break;
    	      }
    	      //case MotionEvent.ACTION_UP: {
    	      //    mActivePointerId = INVALID_POINTER_ID;
    	      //    break;
    	      //}
    	      case MotionEvent.ACTION_CANCEL: {
    	          mActivePointerId = INVALID_POINTER_ID;
    	          break;
    	      }
    	      case MotionEvent.ACTION_POINTER_UP: {
    	          final int pointerIndex = MotionEventCompat.getActionIndex(ev);
    	          final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
    	          if (pointerId == mActivePointerId) {
    	              // This was our active pointer going up. Choose a new
    	              // active pointer and adjust accordingly.
    	              final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
    	              mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
    	              mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
    	              mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
    	          }
    	          break;
    	      }
    	      }
    	      return true;
    	  }
      });

//      @Override
//      public boolean onTouch(View view, MotionEvent event) {
//        // событие
//        int actionMask = event.getActionMasked();
//        // индекс касания
//        int pointerIndex = event.getActionIndex();
//        // число касаний
//        int pointerCount = event.getPointerCount();
//
//        switch (actionMask) {
//        case MotionEvent.ACTION_DOWN: // первое касание
//          inTouch = true;
//        case MotionEvent.ACTION_POINTER_DOWN: // последующие касания
//          downPI = pointerIndex;
//          break;
//
//        case MotionEvent.ACTION_UP: // прерывание последнего касания
//          inTouch = false;
//          sb.setLength(0);
//        case MotionEvent.ACTION_POINTER_UP: // прерывания касаний
//          upPI = pointerIndex;
//          break;
//
//        case MotionEvent.ACTION_MOVE: // движение
//          sb.setLength(0);
//
//          for (int i = 0; i < 10; i++) {
//            sb.append("Index = " + i);
//            if (i < pointerCount) {
//              sb.append(", ID = " + event.getPointerId(i));
//              sb.append(", X = " + event.getX(i));
//              sb.append(", Y = " + event.getY(i));
//            } else {
//              sb.append(", ID = ");
//              sb.append(", X = ");
//              sb.append(", Y = ");
//            }
//            sb.append("\r\n");
//          }
//          break;
//        }
//        result = "down: " + downPI + "\n" + "up: " + upPI + "\n";
//
//        if (inTouch) {
//          result += "pointerCount = " + pointerCount + "\n" + sb.toString();
//        }
//        tv.setText(result);
//        return true;
//      }
//    }

      ExitBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  osc_set_param("POWER_OFF 1");
              long t1=Calendar.getInstance().getTimeInMillis();
              long t2=Calendar.getInstance().getTimeInMillis();
              while((t2-t1)<500) {
            	  t2=Calendar.getInstance().getTimeInMillis();

            	  }
              //view.finish();
              android.os.Process.killProcess(android.os.Process.myPid());
              //view.finishAffinity();
          }
      });

      MenuBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
        	  LoadFragment(MainActivity.frag_settings, getString(R.string.fragment_settings));
          }
      });

      SaveBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
				// Сохранить файл
				// (1) get today's date
			    Date today = Calendar.getInstance().getTime();
			    // (2) create a date "formatter" (the date format we want)
			    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss", Locale.US);
			    // (3) create a new String using the date format we want
			    String Name = formatter.format(today);
				//Toast.makeText(getActivity(),"save to " + extStore+dateTime+".OSC", Toast.LENGTH_LONG).show();
				SaveWurfs(Name+".OSC");
          }
      });

      OpenBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
				// Открыть файл
				setRetainInstance(true);
				  Intent intent = new Intent()
			        .setType("*/*")
			        .setAction(Intent.ACTION_GET_CONTENT);
			        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
          }
      });


      send.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

              if (play==0){
            	  conect_start();
              }
              else {
            	  conect_stop();

              }

          }
      });

      sync_auto.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {

        	           if (auto_sinhros == 1) {
        	               Usinhros_for_CH = 1;
        	               osc_set_param("Usinhros_for_CH 1");
        	               auto_sinhros = 0;
        	               sync_auto.setText("CH1");
        	               sync_auto.setTextColor(Color.parseColor("#00FF00"));
        	           } else if (Usinhros_for_CH == 1) {
        	               Usinhros_for_CH = 2;
        	               osc_set_param("Usinhros_for_CH 2");
        	               auto_sinhros = 0;
        	               sync_auto.setText("CH2");
        	               sync_auto.setTextColor(Color.parseColor("#00FFFF"));
        	           }
        	           else {
        	                 auto_sinhros = 1;
        	                 osc_set_param("auto_sinhros 1");
        	                 sync_auto.setText("auto");
        	                 sync_auto.setTextColor(Color.parseColor("#FFFFFF"));
        	           }
        	   }

      });

      sync_type.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (Usinhros_type == TUsinhros_type.FRONT)
            {
         	 Usinhros_type = TUsinhros_type.FALL;
         	 osc_set_param("Usinhros_type 1");
            }
            else if (Usinhros_type == TUsinhros_type.FALL)
            {
         	  Usinhros_type = TUsinhros_type.FRONT_WAIT;
         	  osc_set_param("Usinhros_type 2");
            }
            else if (Usinhros_type == TUsinhros_type.FRONT_WAIT)
            {
         	 Usinhros_type = TUsinhros_type.FALL_WAIT;
         	 osc_set_param("Usinhros_type 3");
            }
            else if (Usinhros_type == TUsinhros_type.FALL_WAIT)
            {
         	 Usinhros_type = TUsinhros_type.OFF;
         	 osc_set_param("Usinhros_type 4");
            }
            else if (Usinhros_type == TUsinhros_type.OFF)
            {
         	 Usinhros_type = TUsinhros_type.FRONT;
         	 osc_set_param("Usinhros_type 0");
            }
          }
      });

      t_auto.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          	String message;

              if (auto_time == 0)
              {
                  auto_time = 1;
                   message =("auto_time 1");
              }
              else
              {
                  auto_time = 0;
                   message =("auto_time 0");
              }

              osc_set_param(message);
          }
      });

      ch1_auto.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          	String message;
              if (auto_CH1_Udel == 0)
              {
                  auto_CH1_Udel = 1;
                  message=("auto_CH1_Udel 1");
              }
              else
              {
                  auto_CH1_Udel = 0;
                  message=("auto_CH1_Udel 0");
              }
              osc_set_param(message);
          }
      });

      ch2_auto.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          	String message;
              if (auto_CH2_Udel == 0)
              {
                  auto_CH2_Udel = 1;
                  message=("auto_CH2_Udel 1");
              }
              else
              {
                  auto_CH2_Udel = 0;
                  message=("auto_CH2_Udel 0");
              }
              osc_set_param(message);
          }
      });

      final Button ch1_acdc = (Button) getView().findViewById(R.id.CH1_ACDC);
      ch1_acdc.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          	String message;
              if (CH1_dc_ac == 0)
              {
                  CH1_dc_ac = 1;
                  message=("CH1_dc_ac 1");
                  ch1_acdc.setText ( "DC");
              }
              else {
                  CH1_dc_ac = 0;
                  message=("CH1_dc_ac 0");
                  ch1_acdc.setText ( "AC");
              }
              osc_set_param(message);
          }
      });

      final Button ch2_acdc = (Button) getView().findViewById(R.id.CH2_ACDC);
      ch2_acdc.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          	String message;
              if (CH2_dc_ac == 0)
              {
                  CH2_dc_ac = 1;
                  message=("CH2_dc_ac 1");
                  ch2_acdc.setText ( "DC");
              }
              else {
                  CH2_dc_ac = 0;
                  message=("CH2_dc_ac 0");
                  ch2_acdc.setText ( "AC");
              }
              osc_set_param(message);
          }
      });

    seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    verticalSeekBar_usync=(VerticalSeekBar)getView().findViewById(R.id.seekBar_usync);
    verticalSeekBar_usync.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (disable_set_comands==0){
	    	  Usinhros = seekBar.getProgress();
	    	  dont_read_settings_waite();
	    	  osc_set_param("Usinhros " + String.valueOf(Usinhros));
	    	   //dont_read_settings = 0;
            }
		}
	});
	}
            
	private void view_scr_bt(int show){
        if (first_load==0){
    	first_load=1;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screen_bt_pause.getHeight(), screen_bt_pause.getWidth());
               // RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        int centr_x=(-25+grafik.getWidth()/2);
        int y = (grafik.getHeight()/2)-screen_bt_pause.getHeight()/2;
        int x = centr_x-screen_bt_pause.getWidth()/2;
        layoutParams.setMargins(x, y, 0, 0);
        screen_bt_pause.setLayoutParams(layoutParams);


        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(screen_bt_plus.getHeight(), screen_bt_plus.getWidth());
        y = (grafik.getHeight()/2)-screen_bt_plus.getHeight()/2;
        x = centr_x+(int)(screen_bt_plus.getHeight()*0.67);
        layoutParams2.setMargins(x, y, 0, 0);
        screen_bt_plus.setLayoutParams(layoutParams2);


        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(screen_bt_minus.getHeight(), screen_bt_minus.getWidth());
        y = (grafik.getHeight()/2)-screen_bt_minus.getHeight()/2;
        x = centr_x-(int)(screen_bt_pause.getHeight()*1.25);
        layoutParams3.setMargins(x, y, 0, 0);
        screen_bt_minus.setLayoutParams(layoutParams3);


        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(screen_bt_ch1plus.getHeight(), screen_bt_ch1plus.getWidth());
        y = 25;
        x = 24;
        layoutParams4.setMargins(x, y, 0, 0);
        screen_bt_ch1plus.setLayoutParams(layoutParams4);


        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(screen_bt_ch1minus.getHeight(), screen_bt_ch1minus.getWidth());
        y = (grafik.getHeight())-(int)(screen_bt_ch1minus.getHeight()+25);
        x = 24;
        layoutParams5.setMargins(x, y, 0, 0);
        screen_bt_ch1minus.setLayoutParams(layoutParams5);


        RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(screen_bt_ch2plus.getHeight(), screen_bt_ch2plus.getWidth());
        y = 25;
        x = 25+screen_bt_ch2plus.getHeight();
        layoutParams6.setMargins(x, y, 0, 0);
        screen_bt_ch2plus.setLayoutParams(layoutParams6);


        RelativeLayout.LayoutParams layoutParams7 = new RelativeLayout.LayoutParams(screen_bt_ch2minus.getHeight(), screen_bt_ch2minus.getWidth());
        y = (grafik.getHeight())-(int)(screen_bt_ch1minus.getHeight()+25);
        x = 25+screen_bt_ch2minus.getHeight();
        layoutParams7.setMargins(x, y, 0, 0);
        screen_bt_ch2minus.setLayoutParams(layoutParams7);


        RelativeLayout.LayoutParams layoutParams8 = new RelativeLayout.LayoutParams(screen_bt_Tplus.getHeight(), screen_bt_Tplus.getWidth());
        y = (grafik.getHeight())-(int)(screen_bt_Tminus.getHeight()+25);
        x = grafik.getWidth()- (int)(1.27*screen_bt_Tplus.getHeight());
        layoutParams8.setMargins(x, y, 0, 0);
        screen_bt_Tplus.setLayoutParams(layoutParams8);


        RelativeLayout.LayoutParams layoutParams9 = new RelativeLayout.LayoutParams(screen_bt_Tminus.getHeight(), screen_bt_Tminus.getWidth());
        y = 25;
        x = grafik.getWidth()- (int)(1.27*screen_bt_ch2minus.getHeight());
        layoutParams9.setMargins(x, y, 0, 0);
        screen_bt_Tminus.setLayoutParams(layoutParams9);


        RelativeLayout.LayoutParams layoutParams10 = new RelativeLayout.LayoutParams(screen_bt_free.getHeight(), screen_bt_free.getWidth());
        y = grafik.getHeight()/2-screen_bt_free.getHeight()/2;
        x = grafik.getWidth()- (int)(1.27*screen_bt_free.getHeight());
        layoutParams10.setMargins(x, y, 0, 0);
        screen_bt_free.setLayoutParams(layoutParams10);


        RelativeLayout.LayoutParams layoutParams11 = new RelativeLayout.LayoutParams(screen_bt_flow.getHeight(), screen_bt_flow.getWidth());
        y = (grafik.getHeight())-(int)(screen_bt_flow.getHeight()+25);
        x = centr_x-(int)(screen_bt_flow.getHeight()/2);
        layoutParams11.setMargins(x, y, 0, 0);
        screen_bt_flow.setLayoutParams(layoutParams11);
        }

    	if ((view_scrbt==0)||(show==1)){
    		view_scrbt=1;
    		screen_bt_pause.setVisibility(View.VISIBLE);
    		if (pause==0){
    		  screen_bt_ch1plus.setVisibility(View.VISIBLE);
    		  screen_bt_ch1minus.setVisibility(View.VISIBLE);
    		  screen_bt_ch2plus.setVisibility(View.VISIBLE);
    		  screen_bt_ch2minus.setVisibility(View.VISIBLE);
    		  screen_bt_free.setVisibility(View.INVISIBLE);
    		  screen_bt_plus.setVisibility(View.INVISIBLE);
    		  screen_bt_minus.setVisibility(View.INVISIBLE);
              screen_bt_flow.setVisibility(View.VISIBLE);
    		}
    		else {
    		  screen_bt_plus.setVisibility(View.VISIBLE);
    		  screen_bt_minus.setVisibility(View.VISIBLE);
    		  screen_bt_free.setVisibility(View.VISIBLE);
    		  screen_bt_ch1plus.setVisibility(View.INVISIBLE);
    		  screen_bt_ch1minus.setVisibility(View.INVISIBLE);
    		  screen_bt_ch2plus.setVisibility(View.INVISIBLE);
    		  screen_bt_ch2minus.setVisibility(View.INVISIBLE);
              screen_bt_flow.setVisibility(View.VISIBLE);
    		}
    		screen_bt_Tplus.setVisibility(View.VISIBLE);
    		screen_bt_Tminus.setVisibility(View.VISIBLE);

    		mHandler.removeCallbacks(mTimer_scrBt);
    		mTimer_scrBt=null;

         	if (mTimer_scrBt==null){
          		mTimer_scrBt = new Runnable() {
                    @Override
                    public void run() {
    		        view_scrbt=0;
    		        screen_bt_free.setVisibility(View.INVISIBLE);
    		        screen_bt_pause.setVisibility(View.INVISIBLE);
    		        screen_bt_plus.setVisibility(View.INVISIBLE);
    		        screen_bt_minus.setVisibility(View.INVISIBLE);
    		        screen_bt_ch1plus.setVisibility(View.INVISIBLE);
    		        screen_bt_ch1minus.setVisibility(View.INVISIBLE);
    		        screen_bt_ch2plus.setVisibility(View.INVISIBLE);
    		        screen_bt_ch2minus.setVisibility(View.INVISIBLE);
    		        screen_bt_Tplus.setVisibility(View.INVISIBLE);
    		        screen_bt_Tminus.setVisibility(View.INVISIBLE);
    		        screen_bt_flow.setVisibility(View.INVISIBLE);
            		mHandler.removeCallbacks(mTimer_scrBt);
            		mTimer_scrBt=null;
                    }
                };
                mHandler.postDelayed(mTimer_scrBt, 5000);
            	}
            	else {
            		mHandler.removeCallbacks(mTimer_scrBt);
            		mTimer_scrBt=null;
            	}
    	}
    	else {
	        view_scrbt=0;
	        screen_bt_free.setVisibility(View.INVISIBLE);
	        screen_bt_pause.setVisibility(View.INVISIBLE);
	        screen_bt_plus.setVisibility(View.INVISIBLE);
	        screen_bt_minus.setVisibility(View.INVISIBLE);
	        screen_bt_ch1plus.setVisibility(View.INVISIBLE);
	        screen_bt_ch1minus.setVisibility(View.INVISIBLE);
	        screen_bt_ch2plus.setVisibility(View.INVISIBLE);
	        screen_bt_ch2minus.setVisibility(View.INVISIBLE);
	        screen_bt_Tplus.setVisibility(View.INVISIBLE);
	        screen_bt_Tminus.setVisibility(View.INVISIBLE);
            screen_bt_flow.setVisibility(View.INVISIBLE);
    		mHandler.removeCallbacks(mTimer_scrBt);
    		mTimer_scrBt=null;
    	}
	}
        
	/** Метод подгружает и отображает вурфы, записанные построчно в файл WurfsTop10.DAT
	 *
	 */
	private  void LoadAndDisplayWurfs(String path)
	  {
		        File OSCfile = new File(path);
				if(OSCfile.exists())
				{
			        DataInputStream dis = null;
			        try {
			            dis = new DataInputStream(new FileInputStream(OSCfile));
			        } catch (FileNotFoundException e) {
			            e.printStackTrace();
			            Toast.makeText(getActivity(),e.toString(), Toast.LENGTH_LONG).show();
			        }
			        try {
			            bufpoz=0;//SIZE_BUF_SETTINGS
			            if ((int)OSCfile.length()<SIZE_BUF_RX)rxCount=(int)OSCfile.length();
			            else rxCount=SIZE_BUF_RX;
			            if (dis!=null) dis.read(ADC_Buff, 0, rxCount);
				        } catch (IOException e) {
				        	e.printStackTrace();
				        }
			        finally {
			        }
			            // Первые 64 байта (0-63 по счёту с 0) пропускаем, в них лежат настройки
			            if (OSCfile.length()<1000000)Toast.makeText(getActivity(),"load " + Long.toString(OSCfile.length()/1024)+"kbyte", Toast.LENGTH_LONG).show();
			            else Toast.makeText(getActivity(),"load " + Long.toString(OSCfile.length()/(1024*1024))+"Mbyte", Toast.LENGTH_LONG).show();
			            seekBar.setMax( (int)(rxCount / TRACK_BAR_COEFF));
			            adc_sinhros(bufpoz + (scale_t * Xmax), bufpoz + (scale_t * Xmax) + 50000);
			            getRMS();
			            Grafik_refresh(bufpoz, true);

			    	    seekBar.setEnabled(true);
			    	    seekBar.setMax( (int)(rxCount / TRACK_BAR_COEFF));
			    	    seekBar.setProgress( (int)(bufpoz / TRACK_BAR_COEFF));
			    	    pause();

			        try {
			           if (dis!=null) dis.close();
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
				}
				else
				{
					// Файл "SOSC.OSC" не найден? Вызываем соответствующий диалог
				}
	  }

	private void show_graf(int start) {
		graph = (GraphView) getActivity().findViewById(R.id.OscillPlot);
        series1 = new LineGraphSeries<>(getDataPoints(1000, start));
        series2 = new LineGraphSeries<>(getDataPoints(1000, start+1));
        series3 = new LineGraphSeries<>(getDataPoints(1000, 0));
        series1.setThickness(3);
        series2.setThickness(3);
        series3.setThickness(2);
        // [МАСШТАБИРОВАНИЕ]
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-125);
        graph.getViewport().setMaxY(125);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(Xmax);
        // set second scale
        graph.getSecondScale().addSeries(series2);
        // the y bounds are always manual for second scale
        graph.getSecondScale().setMinY(-125);
        graph.getSecondScale().setMaxY(125);
        graph.getGridLabelRenderer().setNumHorizontalLabels(KOL_KLETOK_T+1);
        graph.getGridLabelRenderer().setNumVerticalLabels(KOL_KLETOK_U+1);
        graph.getGridLabelRenderer().setHumanRounding(false);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
//        graph.getGridLabelRenderer().setGridColor(Color.DKGRAY);
//        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
//        graph.getGridLabelRenderer().setVerticalLabelsVisible(true);
//        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
//        graph.getViewport().setScrollable(false); // enables horizontal scrolling
//        graph.getViewport().setScrollableY(false); // enables vertical scrolling
//        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
//        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        graph.addSeries(series1);
        graph.addSeries(series3);
        graph.getLegendRenderer().setVisible(false);
        // Цвет точек
        series1.setColor(Color.parseColor("#00FF00"));
        series2.setColor(Color.parseColor("#00FFFF"));
        series3.setColor(Color.parseColor("#FFFFFF"));
        // Фон графика
        graph.setBackgroundColor(Color.parseColor("#1F1F1F"));//Color.BLACK
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.parseColor("#1F1F1F"));
        graph.setTitleColor(Color.WHITE);
        // Hide labels and title
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().reloadStyles();
        // Цвет сетки
        GridLabelRenderer gridLabelRenderer = graph.getGridLabelRenderer();
        gridLabelRenderer.setGridColor(Color.parseColor("#555555"));
        gridLabelRenderer.setHorizontalLabelsColor(Color.parseColor("#1F1F1F"));
        gridLabelRenderer.setVerticalLabelsColor(Color.parseColor("#FFFFFF"));
        gridLabelRenderer.reloadStyles();
	}
	
	private DataPoint[] getDataPoints(int n, int startNumber){
		int j = 0;
        DataPoint[] values = new DataPoint[(int)n/2];     //creating an object of type DataPoint[] of size 'n'
        // Формируем данные для графика
        for(int i = startNumber; i < startNumber + n; i += 2){
            DataPoint v = new DataPoint(j, 0);//(int)(ADC_Buff[i]& 0xff)
            values[j] = v;
            j++;
        }
        return values;
    }
	    				
    //================================================================================================
    //
    //================================================================================================
	private void set_u_ch1( ) {
        double u = 0;
        switch (CH1_n_delitel)
        {
            case 0: u = 1;
                break;
            case 1:
                if (AlternativVoltDiapason == 0) u = 50;
                else if (AlternativVoltDiapason == 1) u = 25;
                else if (AlternativVoltDiapason == 2) u = 20.0;
                else if (AlternativVoltDiapason == 3) u = 10.0;
                break;
            case 2:
                if (AlternativVoltDiapason == 0) u = 20;
                else if (AlternativVoltDiapason == 1) u = 10;
                else if (AlternativVoltDiapason == 2) u = 8.0;
                else if (AlternativVoltDiapason == 3) u = 4.0;
                break;
            case 3:
                if (AlternativVoltDiapason == 0) u = 10;
                else if (AlternativVoltDiapason == 1) u = 5;
                else if (AlternativVoltDiapason == 2) u = 4.0;
                else if (AlternativVoltDiapason == 3) u = 2.0;
                break;
            case 4:
                if (AlternativVoltDiapason == 0) u = 5;
                else if (AlternativVoltDiapason == 1) u = 2.5;
                else if (AlternativVoltDiapason == 2) u = 2.0;
                else if (AlternativVoltDiapason == 3) u = 1.0;
                break;
            case 5:
                if (AlternativVoltDiapason == 0) u = 2;
                else if (AlternativVoltDiapason == 1) u = 1;
                else if (AlternativVoltDiapason == 2) u = 0.8;
                else if (AlternativVoltDiapason == 3) u = 0.40;
                break;
            case 6:
                if (AlternativVoltDiapason == 0) u = 1;
                else if (AlternativVoltDiapason == 1) u = 0.5;
                else if (AlternativVoltDiapason == 2) u = 0.4;
                else if (AlternativVoltDiapason == 3) u = 0.20;
                break;
            case 7:
                if (AlternativVoltDiapason == 0) u = 0.5;
                else if (AlternativVoltDiapason == 1) u = 0.25;
                else if (AlternativVoltDiapason == 2) u = 0.2;
                else if (AlternativVoltDiapason == 3) u = 0.10;
                break;
            case 8:
                if (AlternativVoltDiapason == 0) u = 0.2;
                else if (AlternativVoltDiapason == 1) u = 0.1;
                else if (AlternativVoltDiapason == 2) u = 0.080;
                else if (AlternativVoltDiapason == 3) u = 0.040;
                break;
            case 9:
                if (AlternativVoltDiapason == 0) u = 0.10;
                else if (AlternativVoltDiapason == 1) u = 0.05;
                else if (AlternativVoltDiapason == 2) u = 0.040;
                else if (AlternativVoltDiapason == 3) u = 0.020;
                break;
            case 10:
                if (AlternativVoltDiapason == 0) u = 0.05;
                else if (AlternativVoltDiapason == 1) u = 0.025;
                else if (AlternativVoltDiapason == 2) u = 0.020;
                else if (AlternativVoltDiapason == 3) u = 0.010;
                break;
            case 11:
                if (AlternativVoltDiapason == 0) u = 0.02;
                else if (AlternativVoltDiapason == 1) u = 0.01;
                else if (AlternativVoltDiapason == 2) u = 0.008;
                else if (AlternativVoltDiapason == 3) u = 0.004;
                break;
            case 12:
                if (AlternativVoltDiapason == 0) u = 0.010;
                else if (AlternativVoltDiapason == 1) u = 0.005;
                else if (AlternativVoltDiapason == 2) u = 0.004;
                else if (AlternativVoltDiapason == 3) u = 0.002;
                break;
        }

        CH1_delitel= ((ADC_9288_Ref*u*100)/(ADC_9288_diapason*AMP2_gain))* CH1_correct;

        if (CH1_n_delitel == 0)
        {
        	ADC_CH1_ON = 0;
            if (ch1_auto!=null) ch1_auto.setText( "OFF");
        }
        else
        {
        	ADC_CH1_ON = 1;
            if (u >= 1) {
            	if (ch1_auto!=null) ch1_auto.setText(String.format("%.0f", u) + "В");
            }
            else {
            	if (ch1_auto!=null) ch1_auto.setText(String.format("%.0f", u*1000) + "мВ");
            }
        }

    }
	
    //================================================================================================
    //
    //================================================================================================
    private void set_u_ch2( ) {
        double u = 0;
        switch (CH2_n_delitel)
        {
            case 0: u = 1;
                break;
            case 1:
                if (AlternativVoltDiapason == 0) u = 50;
                else if (AlternativVoltDiapason == 1) u = 25;
                else if (AlternativVoltDiapason == 2) u = 20.0;
                else if (AlternativVoltDiapason == 3) u = 10.0;
                break;
            case 2:
                if (AlternativVoltDiapason == 0) u = 20;
                else if (AlternativVoltDiapason == 1) u = 10;
                else if (AlternativVoltDiapason == 2) u = 8.0;
                else if (AlternativVoltDiapason == 3) u = 4.0;
                break;
            case 3:
                if (AlternativVoltDiapason == 0) u = 10;
                else if (AlternativVoltDiapason == 1) u = 5;
                else if (AlternativVoltDiapason == 2) u = 4.0;
                else if (AlternativVoltDiapason == 3) u = 2.0;
                break;
            case 4:
                if (AlternativVoltDiapason == 0) u = 5;
                else if (AlternativVoltDiapason == 1) u = 2.5;
                else if (AlternativVoltDiapason == 2) u = 2.0;
                else if (AlternativVoltDiapason == 3) u = 1.0;
                break;
            case 5:
                if (AlternativVoltDiapason == 0) u = 2;
                else if (AlternativVoltDiapason == 1) u = 1;
                else if (AlternativVoltDiapason == 2) u = 0.8;
                else if (AlternativVoltDiapason == 3) u = 0.40;
                break;
            case 6:
                if (AlternativVoltDiapason == 0) u = 1;
                else if (AlternativVoltDiapason == 1) u = 0.5;
                else if (AlternativVoltDiapason == 2) u = 0.4;
                else if (AlternativVoltDiapason == 3) u = 0.20;
                break;
            case 7:
                if (AlternativVoltDiapason == 0) u = 0.5;
                else if (AlternativVoltDiapason == 1) u = 0.25;
                else if (AlternativVoltDiapason == 2) u = 0.2;
                else if (AlternativVoltDiapason == 3) u = 0.10;
                break;
            case 8:
                if (AlternativVoltDiapason == 0) u = 0.2;
                else if (AlternativVoltDiapason == 1) u = 0.1;
                else if (AlternativVoltDiapason == 2) u = 0.080;
                else if (AlternativVoltDiapason == 3) u = 0.040;
                break;
            case 9:
                if (AlternativVoltDiapason == 0) u = 0.10;
                else if (AlternativVoltDiapason == 1) u = 0.05;
                else if (AlternativVoltDiapason == 2) u = 0.040;
                else if (AlternativVoltDiapason == 3) u = 0.020;
                break;
            case 10:
                if (AlternativVoltDiapason == 0) u = 0.05;
                else if (AlternativVoltDiapason == 1) u = 0.025;
                else if (AlternativVoltDiapason == 2) u = 0.020;
                else if (AlternativVoltDiapason == 3) u = 0.010;
                break;
            case 11:
                if (AlternativVoltDiapason == 0) u = 0.02;
                else if (AlternativVoltDiapason == 1) u = 0.01;
                else if (AlternativVoltDiapason == 2) u = 0.008;
                else if (AlternativVoltDiapason == 3) u = 0.004;
                break;
            case 12:
                if (AlternativVoltDiapason == 0) u = 0.010;
                else if (AlternativVoltDiapason == 1) u = 0.005;
                else if (AlternativVoltDiapason == 2) u = 0.004;
                else if (AlternativVoltDiapason == 3) u = 0.002;
                break;
        }
        CH2_delitel= ((ADC_9288_Ref*u*100)/(ADC_9288_diapason*AMP2_gain))* CH2_correct;

        if (CH2_n_delitel == 0)
        {
        	ADC_CH2_ON = 0;
        	if (ch2_auto!=null) ch2_auto.setText( "OFF");
        }
        else
        {
        	ADC_CH2_ON = 1;
            if (u >= 1) {
            	if (ch2_auto!=null)ch2_auto.setText(String.format("%.0f", u) + "В");
            }
            else {
            	if (ch2_auto!=null)ch2_auto.setText(String.format("%.0f", u*1000) + "мВ");
            }
        }

    }

    //================================================================================================
    //
    //================================================================================================
    //чтение буфера
    private int readSettings(int bufpoz){
    	if (dont_read_settings == 1) return 1;
    	if (bufpoz_old == bufpoz) return 1;

    	disable_set_comands = 1;

    	short[] buff_16bit = new short[SIZE_BUF_SETTINGS/2];
        int j = 0;
        for (int i = 0; i < SIZE_BUF_SETTINGS; i += 2)
        {
            buff_16bit[j] = (short)((int)(((ADC_Buff[(bufpoz - SIZE_BUF_SETTINGS) + i+ 1 ])&0xff) << 8)
            		+ (int)(ADC_Buff[(bufpoz - SIZE_BUF_SETTINGS) + i]&0xff));
            j++;
        }
        	VerticalSeekBar SeekBar_usync=(VerticalSeekBar)getView().findViewById(R.id.seekBar_usync);
            int temp;
            int[] VarDataTab = new int[2];
            KOL_KLETOK_T=buff_16bit[adres_KOL_KLETOK_T];
            if (KOL_KLETOK_T<10) KOL_KLETOK_T=10;

            if  (KOL_KLETOK_T_old!=KOL_KLETOK_T){
                graph.getGridLabelRenderer().setNumHorizontalLabels(KOL_KLETOK_T+1);
                graph.getGridLabelRenderer().setNumVerticalLabels(KOL_KLETOK_U+1);
                graph.getGridLabelRenderer().setHumanRounding(false);
            }
            KOL_KLETOK_T_old=KOL_KLETOK_T;
            Xmax_scale_x_max=buff_16bit[adres_screeen_osc_piksels];
            if (Xmax_scale_x_max<300) Xmax_scale_x_max=300;
            VarDataTab[0] = buff_16bit[adres_ADC_freqH];
            VarDataTab[1] = buff_16bit[adres_ADC_freqL];
            ADC_freq = ((long)(VarDataTab[0]& 0xffff)<<16)+((long)(VarDataTab[1]& 0xffff));
            if ((buff_16bit[adres_ADC_Interleaved_mode]&0xFF) != 0)
            {
               ADC_Interleaved_mode = 1;
            }
            else
            {
               ADC_Interleaved_mode = 0;
            }

            if ((buff_16bit[adres_ADC_Interleaved_mode]>>8) == 1)
            {
                stream_viev = 1;
            }
            else if ((buff_16bit[adres_ADC_Interleaved_mode] >>8) == 0)
            {
               stream_viev = 0;
            }

            temp = buff_16bit[adres_scale_x];
            if ((temp <= 16) && (temp > 0)) scale_x = temp;
            Xmax = (Xmax_scale_x_max / scale_x);
            VarDataTab[0] = buff_16bit[adres_CH1_correctH];
            VarDataTab[1] = buff_16bit[adres_CH1_correctL];
            int o = (int)(((VarDataTab[0]) << 16) + (VarDataTab[1]));
            CH1_correct = Float.intBitsToFloat((int)o);
            if (CH1_correct<0.8) CH1_correct=1;
            VarDataTab[0] = buff_16bit[adres_CH2_correctH];
            VarDataTab[1] = buff_16bit[adres_CH2_correctL];
            o = (int)(((VarDataTab[0]) << 16) + (VarDataTab[1]));
            CH2_correct = Float.intBitsToFloat((int)o);
            if (CH2_correct<0.8) CH2_correct=1;
            temp = (buff_16bit[adres_CH1_n_delitel])&0xFF;
            if ((temp < 14) && (temp != 0x00))
            {
               CH1_n_delitel = (int)(temp - 1);
            }
            if (CH1_n_delitel > 0) { ADC_CH1_ON = 1; }
            else ADC_CH1_ON = 0;

            AlternativVoltDiapason=(buff_16bit[adres_CH1_n_delitel]>>8);
            set_u_ch1();
            temp = (buff_16bit[adres_CH2_n_delitel])&0xFF;
            if ((temp < 14) && (temp != 0x00))
            {
               CH2_n_delitel = (int)(temp - 1);
            }
            if (CH2_n_delitel > 0) { ADC_CH2_ON = 1; }
            else ADC_CH2_ON = 0;
            //if ((buff_16bit[adres_CH2_n_delitel]>>8)==1) AlternativVoltDiapason=1;
            //else  AlternativVoltDiapason=0;
            set_u_ch2();
            temp = buff_16bit[adres_CH1_dc_ac];
            if ((temp < 0xFF) && (temp != 0)) CH1_dc_ac = 1;
            else CH1_dc_ac = 0;
            temp = buff_16bit[adres_CH2_dc_ac];
            if ((temp < 0xFF) && (temp != 0)) CH2_dc_ac = 1;
            else CH2_dc_ac = 0;
            if (tfree==0){
              temp = (int)(buff_16bit[adres_Usinhros]&0xFF);
              if ((temp < 0xFF) && (temp != 0x00)) Usinhros = (int)temp;
              SeekBar_usync.setProgress((int)Usinhros);
              temp = buff_16bit[adres_Usinhros_for_CH];
              if (temp != 0x02) Usinhros_for_CH = 1;
              else Usinhros_for_CH = 2;
            }
            temp = buff_16bit[adres_Uoffset_Chanal1];
            if ((temp != 0xFFFF) && (temp != 0)) Uoffset_Chanal1 = (int)temp;
            if ((Uoffset_Chanal1 < -127) || (Uoffset_Chanal1 > 127)) Uoffset_Chanal1 = 0;

            temp = buff_16bit[adres_Uoffset_Chanal2];
            if ((temp != 0xFFFF) && (temp != 0)) Uoffset_Chanal2 = (int)temp;
            if ((Uoffset_Chanal2 < -127) || (Uoffset_Chanal2 > 127)) Uoffset_Chanal2 = 0;

            temp = buff_16bit[adres_scale_t];
            if ((temp != 0) && (temp < 128) && (tfree==0) && (stream_viev==0)) scale_t = (int)temp;
            temp = buff_16bit[adres_auto_time];
            if (temp != 0)
            {
            	auto_time = 1;
            }
            else
            {
            	auto_time = 0;
            }
            if (tfree==0){
            temp = buff_16bit[adres_auto_sinhros];
            if (temp != 0)
            {
            	auto_sinhros = 1;
            }
            else
            {
                auto_sinhros = 0;
            }
            }

            temp = buff_16bit[adres_auto_CH1_Udel];
            if (temp != 0)
            {
            	auto_CH1_Udel = 1;
            }
            else
            {
            	auto_CH1_Udel = 0;
            }
            temp = buff_16bit[adres_auto_CH2_Udel];
            if (temp != 0)
            {
            	auto_CH2_Udel = 1;
            }
            else
            {
            	auto_CH2_Udel = 0;
            }
            temp = buff_16bit[adres_Usinhros_type];
            if (temp == 0)
            {
                	Usinhros_type = TUsinhros_type.FRONT;
            }
            else if (temp == 1)
            {
                	Usinhros_type = TUsinhros_type.FALL;
            }
            else if (temp == 2)
            {
                	Usinhros_type = TUsinhros_type.FRONT_WAIT;
            }
            else if (temp == 3)
            {
                	Usinhros_type = TUsinhros_type.FALL_WAIT;
            }
            else if (temp == 4)
            {
                	Usinhros_type = TUsinhros_type.OFF;
            }

        bufpoz_old = bufpoz;
        disable_set_comands = 0;
        settings_read_ok=1;
        return 0;
    }

    //================================================================================================
    //
    //================================================================================================
    //чтение буфера
    private int readBuf(int start, boolean avr, boolean delitel)
    {
        int V1=0, V2=0;

        if (dont_use_bufpoz==1) {
        	dont_use_bufpoz=0;
        	return 0;
        }

        dont_use_bufpoz=1;
       //================================================================================================
        if ((bufpoz + (2 + SIZE_BUF_SETTINGS)) < (ADC_Buff.length )
        		&& (bufpoz >= SIZE_BUF_SETTINGS)
        		&& (bufpoz + (2 + SIZE_BUF_SETTINGS) < rxCount ) )
        {


       //================================================================================================
       //================================================================================================
       if (avr == true)
       {

                int cound_temp = 0;
                U1 = 0;
                U2 = 0;
                double u1temp = 0;
                double u2temp = 0;
                long Uadc1 = 0;
                long Uadc2 = 0;

                    if ((minmax == 1)||(grafic_type == Tgrafic_type.AVERAGE))
                    {
                        Uadc1 = 0;
                        Uadc2 = 0;
                    }
                    else
                    {
                        Uadc1 = 255;
                        Uadc2 = 255;
                    }

                for (int j = 0; j < scale_t; j ++)
                {
                    if (((byte)ADC_Buff[bufpoz] == (byte)'o') &&
                        ((byte)ADC_Buff[bufpoz + 1] == (byte)'s') &&
                        ((byte)ADC_Buff[bufpoz + 2] == (byte)'c') &&
                        ((byte)ADC_Buff[bufpoz + 3] == (byte)' ') &&
                        ((byte)ADC_Buff[bufpoz + 4] == (byte)'v') &&
                        ((byte)ADC_Buff[bufpoz + 5] == (byte)'3'))
                    {
                    	bufpoz = bufpoz + SIZE_BUF_SETTINGS;
                    	readSettings(bufpoz);
                    }

                    V1 = (int)( ADC_Buff[bufpoz] &0xFF) ;      //прочитать из него символ
                    V2 = (int)( ADC_Buff[bufpoz+1]&0xFF);      //прочитать из него символ

                    cound_temp++;
                    if (grafic_type == Tgrafic_type.AVERAGE)
                    {
                        Uadc1 += V1;
                        Uadc2 += V2;
                    }
                    else
                    {
                        if (minmax == 1)
                        {
                            if (Uadc1 < V1) Uadc1 = V1;
                            if (Uadc2 < V2) Uadc2 = V2;
                        }
                        else
                        {
                            if (Uadc1 > V1) Uadc1 = V1;
                            if (Uadc2 > V2) Uadc2 = V2;
                        }
                    }
                    if (bufpoz < (ADC_Buff.length - ( 6 ))) bufpoz = bufpoz + 2;
                    else break;
                }

                if (minmax == 0) minmax = 1;
                else minmax = 0;

                if (grafic_type == Tgrafic_type.AVERAGE)
                {
                    if (delitel==true) u1temp = CH1_delitel * (((double)Uadc1 / cound_temp) - ADC9288_pol_diapasona);
                    else               u1temp =  (((double)Uadc1 / cound_temp) - ADC9288_pol_diapasona);
                    if (delitel==true) u2temp = CH2_delitel * (((double)Uadc2 / cound_temp) - ADC9288_pol_diapasona);
                    else               u2temp =  (((double)Uadc2 / cound_temp) - ADC9288_pol_diapasona);
                }
                else
                {
                    if (delitel==true) u1temp = CH1_delitel * ((double)Uadc1 - ADC9288_pol_diapasona);
                    else               u1temp =((double)Uadc1 - ADC9288_pol_diapasona);
                    if (delitel==true) u2temp = CH2_delitel * ((double)Uadc2 - ADC9288_pol_diapasona);
                    else               u2temp = ((double)Uadc2 - ADC9288_pol_diapasona);
                }
                    U1 = u1temp;
                    U2 = u2temp;
            }
       //================================================================================================
            else
            {
                //================================================================================================
                for (int i = 0; i < SIZE_BUF_SETTINGS; i++)
                {
                  if ((bufpoz - i) < 0) break;
                  if ((byte)ADC_Buff[bufpoz - i] == (byte)'o')
                  {
                    if (((byte)ADC_Buff[(bufpoz-i)] == (byte)'o') &&
                        ((byte)ADC_Buff[(bufpoz-i) + 1] == (byte)'s') &&
                        ((byte)ADC_Buff[(bufpoz-i) + 2] == (byte)'c') &&
                        ((byte)ADC_Buff[(bufpoz-i) + 3] == (byte)' ') &&
                        ((byte)ADC_Buff[(bufpoz-i) + 4] == (byte)'v') &&
                        ((byte)ADC_Buff[(bufpoz-i) + 5] == (byte)'3'))
                    {
                        bufpoz = (bufpoz - i) + SIZE_BUF_SETTINGS;
                        readSettings(bufpoz);
                        break;
                    }
                  }
                }
            	//================================================================================================

                if (ADC_Interleaved_mode == 0)
                {
                    V1 = (int)(ADC_Buff[bufpoz]&0xFF);        //прочитать из него символ
                    V2 = (int)(ADC_Buff[bufpoz + 1]&0xFF);      //прочитать из него символ
                    if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) Xpoz = Xpoz + 2;

                    if (delitel==true) U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                    else               U1 = ((double)V1 - ADC9288_pol_diapasona);
                    if (delitel==true) U2 = CH2_delitel * ((double)V2 - ADC9288_pol_diapasona);
                    else               U2 = ((double)V2 - ADC9288_pol_diapasona);
                }
                else {
                    V1 = (int)(ADC_Buff[bufpoz]&0xFF);        //прочитать из него символ
                    V2 = (int)(ADC_Buff[bufpoz + 1]&0xFF);      //прочитать из него символ
                    if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) Xpoz = Xpoz + 2;

                    if (delitel==true) U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                    else               U1 = ((double)V1 - ADC9288_pol_diapasona);
                    if (delitel==true) U2 = CH1_delitel * ((double)V2 - ADC9288_pol_diapasona);
                    else               U2 = ((double)V2 - ADC9288_pol_diapasona);
                }
            }
       //================================================================================================
            dont_use_bufpoz=0;
            return 1;
        }
        else {
        	U1 = 0;
        	U2 = 0;
        	dont_use_bufpoz=0;
        	return 0;
       }
    }

    //================================================================================================
    //
    //================================================================================================
    //чтение буфера
    private int readBuf()
    {
        int V1=0, V2=0;

       //================================================================================================
       // if ((Xpoz + ( 6 + SIZE_BUF_SETTINGS) < ADC_Buff.length) && (Xpoz >= SIZE_BUF_SETTINGS)
       // 		&& (Xpoz + ( 6 + SIZE_BUF_SETTINGS)) < rxCount  )
         if ((Xpoz + 2  < ADC_Buff.length)
        	&& (Xpoz + 2  < rxCount  ))

        {
       //================================================================================================
       //================================================================================================

                if (ADC_Interleaved_mode == 0)
                {
                    V1 = (int)(ADC_Buff[Xpoz]&0xFF);        //прочитать из него символ
                    V2 = (int)(ADC_Buff[Xpoz + 1]&0xFF);      //прочитать из него символ
                    if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) Xpoz = Xpoz + 2;

                     U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                     U2 = CH2_delitel * ((double)V2 - ADC9288_pol_diapasona);

                }
                else {
                    V1 = (int)(ADC_Buff[Xpoz]&0xFF);        //прочитать из него символ
                    V2 = (int)(ADC_Buff[Xpoz + 1]&0xFF);      //прочитать из него символ
                    if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) Xpoz = Xpoz + 2;

                    U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                    U2 = CH1_delitel * ((double)V2 - ADC9288_pol_diapasona);
                }

       //================================================================================================

            return 1;
        }
        else {
        	U1 = 0;
        	U2 = 0;
        	return 0;
       }
    }

    //================================================================================================
    //
    //================================================================================================
    private void getRMS() {
            double U1rms = 0, U2rms = 0;
            double U1avr = 0, U2avr = 0;
            double U1min = CH1_delitel *256, U2min = CH2_delitel *256;
            double U1max = 0, U2max = 0;

            Xpoz= Xpoz_for_RMS;
            if (Xpoz_old == Xpoz)
            {
                return;
            }
            Xpoz_old = Xpoz;


            int okCount = 0;
            int stop = Xmax * scale_t ;
            if (stop > rxCount) stop = rxCount;


            //if ((rxCount > Xpoz+Xmax*scale_t ))//(timer1.Enabled == false) &&
            //{
            	//bufpoz = Xpoz;

                for (int x = 0; x <  stop; x++)
                {
                    if (readBuf() != 0)
                    {
                        U1avr += U1;
                        U2avr += U2;

                        if (U1max < U1) U1max = U1;
                        if (U2max < U2) U2max = U2;

                        if (U1min > U1) U1min = U1;
                        if (U2min > U2) U2min = U2;

                        U1rms += (U1 * U1);
                        if (U1 < 0) U1 = -U1;
                        U2rms += (U2 * U2);
                        if (U2 < 0) U2 = -U2;
                        okCount++;
                    }
                }
                U1rms = Math.sqrt(U1rms / okCount);
                U2rms = Math.sqrt(U2rms / okCount);
                U1avr = (U1avr / okCount);
                U2avr = (U2avr / okCount);

                if (okCount > 0)
                {
                if (ADC_CH1_ON == 1)
                {
                    if ((U1avr > 1)||(U1avr <- 1)) textView_CH1rms.setText("Uavr=" +  String.format("%.2f", U1avr) + " В");
                    else textView_CH1rms.setText("Uavr=" +  String.format("%.1f", U1avr*1000) + " мВ");

                    if (U1rms > 1) textView_CH1avr.setText("Urms=" +  String.format("%.2f", U1rms) + " В");
                    else textView_CH1avr.setText("Urms=" +  String.format("%.1f", U1rms*1000) + " мВ");

                    if ((U1max - U1min) > 1) textView_CH1pp.setText("Up-p=" +  String.format("%.2f", (U1max - U1min)) + " В");
                    else textView_CH1pp.setText("Up-p=" +  String.format("%.1f", (U1max - U1min)*1000) + " мВ");
                }
                else {
                	textView_CH1rms.setText("Uavr=-");
                	textView_CH1avr.setText("Urms=-");
                	textView_CH1pp.setText("Up-p=-");
                }


                if (ADC_CH2_ON == 1)
                {
                    if ((U2avr > 1)||(U2avr <-1)) textView_CH2rms.setText("Uavr=" +  String.format("%.2f", U2avr) + " В");
                    else textView_CH2rms.setText("Uavr=" +  String.format("%.1f", U2avr*1000) + " мВ");

                    if (U2rms > 1) textView_CH2avr.setText("Urms=" +  String.format("%.2f", U2rms) + " В");
                    else textView_CH2avr.setText("Urms=" +  String.format("%.1f", U2rms*1000) + " мВ");

                    if ((U2max - U2min) > 1) textView_CH2pp.setText("Up-p=" +  String.format("%.2f", (U2max - U2min)) + " В");
                    else textView_CH2pp.setText("Up-p=" +  String.format("%.1f", (U2max - U2min)*1000) + " мВ");
                }
                else {
                	textView_CH2rms.setText("Uavr=-");
                	textView_CH2avr.setText("Urms=-");
                	textView_CH2pp.setText("Up-p=-");
                }
                }


                if (count_CH1_freq > 0)
                {
                    CH1_freq = CH1_freq_temp / count_CH1_freq;
                    CH1_freq_temp = 0;
                    count_CH1_freq = 0;
                    if  (CH1_freq<1000)  textView_CH1freq.setText( "f=" +  String.valueOf(CH1_freq) + "Гц");
                    else if (CH1_freq < 1000000) textView_CH1freq.setText( "f=" +  String.format("%.2f", ((float)CH1_freq / 1000)) + "кГц");
                    else     textView_CH1freq.setText( "f=" +  String.format("%.3f", ((float)CH1_freq / 1000000)) + "МГц");

                }
                else textView_CH1freq.setText("f= - ");

                if (count_CH2_freq > 0)
                {
                    CH2_freq = CH2_freq_temp / count_CH2_freq;
                    CH2_freq_temp = 0;
                    count_CH2_freq = 0;
                    if  (CH2_freq<1000)  textView_CH2freq.setText( "f=" +  String.valueOf(CH2_freq) + "Гц");
                    else if (CH2_freq < 1000000) textView_CH2freq.setText( "f=" +  String.format("%.2f", ((float)CH2_freq / 1000)) + "кГц");
                    else     textView_CH2freq.setText( "f=" +  String.format("%.3f", ((float)CH2_freq / 1000000)) + "МГц");

                }
                else textView_CH2freq.setText("f= - ");

                if (count_CH1_duty_cycl > 0)
                {
                    CH1_duty_cycl =(byte) (CH1_duty_cycl_temp / count_CH1_duty_cycl);
                    CH1_duty_cycl_temp = 0;
                    count_CH1_duty_cycl = 0;
                    textView_CH1dutyc.setText( "DutyС=" +  String.valueOf(CH1_duty_cycl) + "%");
                }
                else textView_CH1dutyc.setText( "DutyС=-");

                if (count_CH2_duty_cycl > 0)
                {
                    CH2_duty_cycl =(byte) (CH2_duty_cycl_temp / count_CH2_duty_cycl);
                    CH2_duty_cycl_temp = 0;
                    count_CH2_duty_cycl = 0;
                    textView_CH2dutyc.setText( "DutyС=" +  String.valueOf(CH2_duty_cycl) + "%");
                }
                else textView_CH2dutyc.setText( "DutyС=-");
            //}
            //bufpoz = Xpoz;
            //Xpoz = Xpoz_old;
        }

    /*----------------------------------------------------------------------------
    *   adc_sinhros
    *---------------------------------------------------------------------------*/
    private int adc_sinhros(int Xstart, int Xstop)
    {
        byte flagSINHRnull = 0;
        int nmassiv=0;
        int point_low=0, point_hight=0;
        int buffer_size1;
        int buffer_start1;
        int buffer_size2;
        int buffer_start2;
        int sinhros;
        int CH1sinhros=0;
        int CH2sinhros=0;

        byte lr = 0;
        byte hr = 0;

        if (Xstop > rxCount) Xstop = rxCount;
        if (Xstart<0) return 0;

if (ADC_Interleaved_mode==0){
//=====================================================================
    buffer_start1 = (int)Xstart;
    buffer_size1 = (int)Xstop;

    buffer_start2 = (int)Xstart;
    buffer_size2 = (int)Xstop;

    if (buffer_size1 > SIZE_BUF_RX - 32) buffer_size1 = (int)(SIZE_BUF_RX - 32);
    if (buffer_size2 > SIZE_BUF_RX - 32) buffer_size2 = (int)(SIZE_BUF_RX - 32);
//================================================================================
    if (ADC_CH1_ON == 1)
    {
        flagSINHRnull = 0;
        CH1sinhros = 0;
        point_low = 0;
        point_hight = 0;
        for (nmassiv = buffer_start1; nmassiv < buffer_size1; nmassiv += 2)
        {
            //=======================
            if (flagSINHRnull == 0)
            {
                if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) > Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) <= Usinhros))
                    {
                        flagSINHRnull = 1;
                    }
                }
                else
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) < Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) >= Usinhros))
                    {
                        flagSINHRnull = 1;
                    }
                }
            }
            //=======================
            if (flagSINHRnull == 1)
            {
                if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) < Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) >= Usinhros))
                    {
                        flagSINHRnull = 2;
                        CH1sinhros = nmassiv;
                        point_hight = nmassiv;
                    }
                }
                else
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) > Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) <= Usinhros))
                    {
                        flagSINHRnull = 2;
                        CH1sinhros = nmassiv;
                        point_low = nmassiv;
                    }
                }
            }
            //=======================
            if (flagSINHRnull == 2)
            {
                if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) > Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) <= Usinhros))
                    {
                        flagSINHRnull = 3;
                        point_low = nmassiv;
                    }
                }
                else
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) < Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) >= Usinhros))
                    {
                        flagSINHRnull = 3;
                        point_hight = nmassiv;
                    }
                }
            }
            //=======================
            if (flagSINHRnull == 3)
            {
                if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) < Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) >= Usinhros))
                    {
                        flagSINHRnull = 0;
                        CH1sinhros = nmassiv;
                        CH1_freq_temp += (int)(ADC_freq / ((nmassiv - point_hight) / 2));
                        CH1_duty_cycl_temp += (int)(100 * (point_low - point_hight) / (nmassiv - point_hight));
                        count_CH1_freq++;
                        count_CH1_duty_cycl++;
                        break;
                        //point_hight=nmassiv;
                    }
                }
                else
                {
                    if ((((((ADC_Buff[nmassiv] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 2] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 4] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 6] >> lr) & 0xFF)
                                   + ((ADC_Buff[nmassiv + 8] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 10] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 12] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 14] >> lr) & 0xFF)
                            ) >> 3) > Usinhros)
                      && (((((ADC_Buff[nmassiv + 16] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 18] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 20] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 22] >> lr) & 0xFF)
                                 + ((ADC_Buff[nmassiv + 24] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 26] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 28] >> lr) & 0xFF) + ((ADC_Buff[nmassiv + 30] >> lr) & 0xFF)
                        ) >> 3) <= Usinhros))
                    {
                        flagSINHRnull = 0;
                        CH1sinhros = nmassiv;
                        CH1_freq_temp += (int)(ADC_freq / ((nmassiv - point_low) / 2));
                        CH1_duty_cycl_temp += (int)(100 - (100 * (point_hight - point_low) / (nmassiv - point_low)));
                        count_CH1_freq++;
                        count_CH1_duty_cycl++;
                        break;
                        //point_low=nmassiv;
                    }
                }
            }
            //=======================
        }
    }

if (ADC_CH2_ON==1){
flagSINHRnull=0;
  CH2sinhros = 0;
  point_low=0;
  point_hight=0;
  for ( nmassiv=buffer_start2; nmassiv<buffer_size2; nmassiv+=2){
	//=======================
if(flagSINHRnull==0){
    if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
    {

 if ( ((( ((ADC_Buff[nmassiv+1]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+3]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+5]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+7]>>hr)& 0xFF)
		        +((ADC_Buff[nmassiv+9]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+11]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+13]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+15]>>hr)& 0xFF)
		 )>>3) > Usinhros)
   && ((( ((ADC_Buff[nmassiv+17]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+19]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+21]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+23]>>hr)& 0xFF)
	          + ((ADC_Buff[nmassiv+25]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+27]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+29]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+31]>>hr)& 0xFF)
	 )>>3) <= Usinhros) )

	 {
		flagSINHRnull=1;
     }
 }
else {
   if ((((((ADC_Buff[nmassiv + 1] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 3] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 5] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 7] >> hr) & 0xFF)
                  + ((ADC_Buff[nmassiv + 9] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 11] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 13] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 15] >> hr) & 0xFF)
           ) >> 3) < Usinhros)
     && (((((ADC_Buff[nmassiv + 17] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 19] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 21] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 23] >> hr) & 0xFF)
                + ((ADC_Buff[nmassiv + 25] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 27] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 29] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 31] >> hr) & 0xFF)
       ) >> 3) >= Usinhros))
	 {
		flagSINHRnull=1;
     }
 }
}

	//=======================
if(flagSINHRnull==1){
    if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
    {

 if ( ((( ((ADC_Buff[nmassiv+1]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+3]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+5]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+7]>>hr)& 0xFF)
		        +((ADC_Buff[nmassiv+9]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+11]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+13]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+15]>>hr)& 0xFF)
		 )>>3) < Usinhros)
   && ((( ((ADC_Buff[nmassiv+17]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+19]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+21]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+23]>>hr)& 0xFF)
	          + ((ADC_Buff[nmassiv+25]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+27]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+29]>>hr)& 0xFF) + ((ADC_Buff[nmassiv+31]>>hr)& 0xFF)
	 )>>3) >= Usinhros) )
	 {
		flagSINHRnull=2;
		CH2sinhros=nmassiv;
		point_hight=nmassiv;
     }
 }
else {
   if ((((((ADC_Buff[nmassiv + 1] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 3] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 5] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 7] >> hr) & 0xFF)
                  + ((ADC_Buff[nmassiv + 9] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 11] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 13] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 15] >> hr) & 0xFF)
           ) >> 3) > Usinhros)
     && (((((ADC_Buff[nmassiv + 17] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 19] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 21] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 23] >> hr) & 0xFF)
                + ((ADC_Buff[nmassiv + 25] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 27] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 29] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 31] >> hr) & 0xFF)
       ) >> 3) <= Usinhros))
	 {
		flagSINHRnull=2;
		CH2sinhros=nmassiv;
		point_low=nmassiv;
     }
 }
}
	//=======================
if(flagSINHRnull==2){
    if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
    {
        if ((((((ADC_Buff[nmassiv + 1] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 3] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 5] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 7] >> hr) & 0xFF)
                       + ((ADC_Buff[nmassiv + 9] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 11] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 13] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 15] >> hr) & 0xFF)
                ) >> 3) > Usinhros)
          && (((((ADC_Buff[nmassiv + 17] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 19] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 21] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 23] >> hr) & 0xFF)
                     + ((ADC_Buff[nmassiv + 25] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 27] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 29] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 31] >> hr) & 0xFF)
            ) >> 3) <= Usinhros))
	 {
		flagSINHRnull=3;
		point_low=nmassiv;
     }
 }
else {
   if ((((((ADC_Buff[nmassiv + 1] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 3] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 5] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 7] >> hr) & 0xFF)
                  + ((ADC_Buff[nmassiv + 9] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 11] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 13] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 15] >> hr) & 0xFF)
           ) >> 3) < Usinhros)
     && (((((ADC_Buff[nmassiv + 17] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 19] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 21] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 23] >> hr) & 0xFF)
                + ((ADC_Buff[nmassiv + 25] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 27] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 29] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 31] >> hr) & 0xFF)
       ) >> 3) >= Usinhros))
	 {
		flagSINHRnull=3;
		point_hight=nmassiv;
    }
 }
}
	//=======================
if(flagSINHRnull==3){
    if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT))
    {
        if ((((((ADC_Buff[nmassiv + 1] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 3] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 5] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 7] >> hr) & 0xFF)
                       + ((ADC_Buff[nmassiv + 9] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 11] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 13] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 15] >> hr) & 0xFF)
                ) >> 3) < Usinhros)
          && (((((ADC_Buff[nmassiv + 17] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 19] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 21] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 23] >> hr) & 0xFF)
                     + ((ADC_Buff[nmassiv + 25] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 27] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 29] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 31] >> hr) & 0xFF)
            ) >> 3) >= Usinhros))
	 {
		flagSINHRnull=0;
		CH2sinhros=nmassiv;
        CH2_freq_temp += (int)(ADC_freq / ((nmassiv - point_hight)/2));
        CH2_duty_cycl_temp += (int)(100 * (point_low - point_hight) / (nmassiv - point_hight));
        count_CH2_freq++;
        count_CH2_duty_cycl++;
		break;
		//point_hight=nmassiv;
 }
 }
else {
   if ((((((ADC_Buff[nmassiv + 1] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 3] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 5] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 7] >> hr) & 0xFF)
                  + ((ADC_Buff[nmassiv + 9] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 11] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 13] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 15] >> hr) & 0xFF)
           ) >> 3) > Usinhros)
     && (((((ADC_Buff[nmassiv + 17] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 19] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 21] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 23] >> hr) & 0xFF)
                + ((ADC_Buff[nmassiv + 25] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 27] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 29] >> hr) & 0xFF) + ((ADC_Buff[nmassiv + 31] >> hr) & 0xFF)
       ) >> 3) <= Usinhros))
	 {
		flagSINHRnull=0;
		CH2sinhros=nmassiv;
        CH2_freq_temp += (int)(ADC_freq / ((nmassiv - point_low)/2));
        CH2_duty_cycl_temp += (int)(100 - (100 * (point_hight - point_low) / (nmassiv - point_low)));
        count_CH2_freq++;
        count_CH2_duty_cycl++;
		break;
		//point_low=nmassiv;
 }
 }
}
	//=======================
}
}
//================================================================================
if (Usinhros_for_CH == 1)
{
  sinhros = CH1sinhros;
}
else sinhros = CH2sinhros;

if (sinhros == 0) {

	sinhros = (int)Xstart;

}
else {
	sinhros = sinhros + 18;
}

return (int)sinhros;
//================================================================================
}
//================================================================================
else{
buffer_start1 = (int)Xstart;
buffer_size1 = (int)Xstop;
if (buffer_size1 > SIZE_BUF_RX - 15) buffer_size1 = (int)(SIZE_BUF_RX - 15);
//================================================================================
flagSINHRnull=0;
  CH1sinhros = 0;
  point_low=0;
  point_hight=0;
  for ( nmassiv=buffer_start1; nmassiv<buffer_size1; nmassiv++){
if(flagSINHRnull==0){
 if ( ((( ((ADC_Buff[nmassiv] ) ) + ((ADC_Buff[nmassiv+1] ) ) + ((ADC_Buff[nmassiv+2] ) ) + ((ADC_Buff[nmassiv+3] ) ) )>>2) < Usinhros)
   && ((( ((ADC_Buff[nmassiv+4] ) ) + ((ADC_Buff[nmassiv+5] ) ) + ((ADC_Buff[nmassiv+6] ) ) + ((ADC_Buff[nmassiv+7] ) ) )>>2) > Usinhros) )
	 {
		flagSINHRnull=1;
        if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT)) CH1sinhros = nmassiv;
 }
 }
else if(flagSINHRnull==1){
 if ( ((( ((ADC_Buff[nmassiv] ) ) + ((ADC_Buff[nmassiv+1] ) ) + ((ADC_Buff[nmassiv+2] ) ) + ((ADC_Buff[nmassiv+3] ) ) )>>2) > Usinhros)
   && ((( ((ADC_Buff[nmassiv+4] ) ) + ((ADC_Buff[nmassiv+5] ) ) + ((ADC_Buff[nmassiv+6] ) ) + ((ADC_Buff[nmassiv+7] ) ) )>>2) < Usinhros) )
	 {
		flagSINHRnull=2;
        if ((Usinhros_type == TUsinhros_type.FALL) || (Usinhros_type == TUsinhros_type.FALL_WAIT)) CH1sinhros = nmassiv;
 }
 }
else if(flagSINHRnull==2){
 if ( ((( ((ADC_Buff[nmassiv] ) ) + ((ADC_Buff[nmassiv+1] ) ) + ((ADC_Buff[nmassiv+2] ) ) + ((ADC_Buff[nmassiv+3] ) ) )>>2) < Usinhros)
   && ((( ((ADC_Buff[nmassiv+4] ) ) + ((ADC_Buff[nmassiv+5] ) ) + ((ADC_Buff[nmassiv+6] ) ) + ((ADC_Buff[nmassiv+7] ) ) )>>2) > Usinhros) )
	 {
		 flagSINHRnull=3;
         if ((Usinhros_type == TUsinhros_type.FRONT) || (Usinhros_type == TUsinhros_type.FRONT_WAIT)) CH1sinhros = nmassiv;
		 point_hight=nmassiv;
 }
 }
else if(flagSINHRnull==3){
 if ( ((( ((ADC_Buff[nmassiv] ) ) + ((ADC_Buff[nmassiv+1] ) ) + ((ADC_Buff[nmassiv+2] ) ) + ((ADC_Buff[nmassiv+3] ) ) )>>2) > Usinhros)
   && ((( ((ADC_Buff[nmassiv+4] ) ) + ((ADC_Buff[nmassiv+5] ) ) + ((ADC_Buff[nmassiv+6] ) ) + ((ADC_Buff[nmassiv+7] ) ) )>>2) < Usinhros) )
	 {
		 flagSINHRnull=4;
         if ((Usinhros_type == TUsinhros_type.FALL) || (Usinhros_type == TUsinhros_type.FALL_WAIT)) CH1sinhros = nmassiv;
		 point_low=nmassiv;
 }
 }
else if(flagSINHRnull==4){
 if ( ((( ((ADC_Buff[nmassiv] ) ) + ((ADC_Buff[nmassiv+1] ) ) + ((ADC_Buff[nmassiv+2] ) ) + ((ADC_Buff[nmassiv+3] ) ) )>>2) < Usinhros)
   && ((( ((ADC_Buff[nmassiv+4] ) ) + ((ADC_Buff[nmassiv+5] ) ) + ((ADC_Buff[nmassiv+6] ) ) + ((ADC_Buff[nmassiv+7] ) ) )>>2) > Usinhros) )
	 {
         flagSINHRnull = 0;
         if (nmassiv < buffer_size1 - 12)
         {
             CH1_freq_temp += (int)(ADC_freq / ((nmassiv - CH1sinhros) / 2));
             count_CH1_freq++;
             CH1_duty_cycl_temp += (int)(100 * (point_low - point_hight) / (nmassiv - point_hight));
             count_CH1_duty_cycl++;
         }
         break;
 }
 }
}
//================================================================================
 sinhros=CH1sinhros;
 if (sinhros == 0) sinhros = (int)Xstart;
 return (int)sinhros;
//================================================================================
}
}
    
    //================================================================================================
    //
    //================================================================================================
    private int Grafik_refresh(int Xstart, boolean USync)
    {
        byte sync_fall = 1;
        int Xstop;
        int Xmax_temp;
        int start_temp = Xstart;

        if ((ADC_CH1_ON == 0) && (ADC_CH2_ON == 0)) return start_temp;

        if ((Usinhros_type == TUsinhros_type.OFF) || (ADC_freq < freq11) || (stream_viev == 1)) size_buff = (Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS;
        else
        {
            if (ADC_freq < freq9) size_buff = ((SIZE_BUF_RX_for_MK/2) + SIZE_BUF_SETTINGS);
            size_buff = (SIZE_BUF_RX_for_MK + SIZE_BUF_SETTINGS);
        }

        if (Xmax > Xmax_scale_x_max)
        {
            Xmax_temp = Xmax_scale_x_max;
        }
        else
        {
            Xmax_temp = Xmax;
        }

        if (Xstart + size_buff > rxCount) {
            if (rxCount>size_buff)   Xstart = rxCount-size_buff;
            else return start_temp;
        }
        if (Xstart < 0) Xstart = 0;

        Xstop = Xstart + size_buff;
        if (Xstop > rxCount)
        {
            if (stream_viev == 0) Xstop = rxCount;
            else return start_temp;
        }

        if 	(Xstart>SIZE_BUF_SETTINGS){
        for (int i = 0; i < SIZE_BUF_SETTINGS; i++)
        {
          if ((Xstart - i) < 0) break;
          if ((byte)ADC_Buff[Xstart - i] == (byte)'o')
          {
            if (((byte)ADC_Buff[(Xstart-i)] == (byte)'o') &&
                ((byte)ADC_Buff[(Xstart-i) + 1] == (byte)'s') &&
                ((byte)ADC_Buff[(Xstart-i) + 2] == (byte)'c') &&
                ((byte)ADC_Buff[(Xstart-i) + 3] == (byte)' ') &&
                ((byte)ADC_Buff[(Xstart-i) + 4] == (byte)'v') &&
                ((byte)ADC_Buff[(Xstart-i) + 5] == (byte)'3'))
            {
            	Xstart = (Xstart - i) + SIZE_BUF_SETTINGS;
                readSettings(Xstart);
                break;
            }
          }
        }
        }

     //==================================================================================================
        if (USync == true)
        {
            while (Xstart < rxCount)
            {
                if ((byte)ADC_Buff[Xstart] == (byte)'o')
                {
                    if (((byte)ADC_Buff[(Xstart)] == (byte)'o') &&
                        ((byte)ADC_Buff[(Xstart) + 1] == (byte)'s') &&
                        ((byte)ADC_Buff[(Xstart) + 2] == (byte)'c') &&
                        ((byte)ADC_Buff[(Xstart) + 3] == (byte)' ') &&
                        ((byte)ADC_Buff[(Xstart) + 4] == (byte)'v') &&
                        ((byte)ADC_Buff[(Xstart) + 5] == (byte)'3'))
                    {
                        readSettings(Xstart + SIZE_BUF_SETTINGS);
                        Xstart+=(SIZE_BUF_SETTINGS);

                        if ((Usinhros_type == TUsinhros_type.OFF) || (ADC_freq < freq11) || (stream_viev == 1)) size_buff = (Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS;
                        else
                        {
                            if (ADC_freq < freq9) size_buff = ((SIZE_BUF_RX_for_MK/2) + SIZE_BUF_SETTINGS);
                            size_buff = (SIZE_BUF_RX_for_MK + SIZE_BUF_SETTINGS);
                        }
                        break;
                    }
                }
                Xstart++;
            }

            if (Xstart == rxCount)
            {
                if (Xstart + size_buff > rxCount)
                {
                    if (rxCount > size_buff)
                    {
                        Xstart = rxCount - size_buff;
                        while (Xstart < rxCount)
                        {
                            if ((byte)ADC_Buff[Xstart] == (byte)'o')
                            {
                                if (((byte)ADC_Buff[(Xstart)] == (byte)'o') &&
                                     ((byte)ADC_Buff[(Xstart) + 1] == (byte)'s') &&
                                     ((byte)ADC_Buff[(Xstart) + 2] == (byte)'c') &&
                                     ((byte)ADC_Buff[(Xstart) + 3] == (byte)' ') &&
                                     ((byte)ADC_Buff[(Xstart) + 4] == (byte)'v') &&
                                     ((byte)ADC_Buff[(Xstart) + 5] == (byte)'3'))
                                {
                                    readSettings(Xstart + SIZE_BUF_SETTINGS);
                                    if ((Usinhros_type == TUsinhros_type.OFF) || (ADC_freq < freq11) || (stream_viev == 1)) size_buff = (Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS;
                                    else
                                    {
                                        if (ADC_freq < freq9) size_buff = ((SIZE_BUF_RX_for_MK / 2) + SIZE_BUF_SETTINGS);
                                        size_buff = (SIZE_BUF_RX_for_MK + SIZE_BUF_SETTINGS);
                                    }
                                    //TRACK_BAR_COEFF = size_buff;
                                    break;
                                }
                            }
                            Xstart++;
                        }
                        if (Xstart == rxCount)
                        {
                            return start_temp;
                        }
                    }
                    else return start_temp;
                }
           }

            start_temp = Xstart;
            if (ADC_Interleaved_mode == 0)
            {
                if  (((Usinhros_type == TUsinhros_type.FALL)||(Usinhros_type == TUsinhros_type.FRONT))&& (ADC_freq >= freq11)){
            	  int smesenie= (scale_t * Xmax);
                  if ((smesenie % 2) != 0) smesenie++;
                  Xstart = adc_sinhros(Xstart + smesenie, (int)start_temp + (size_buff-SIZE_BUF_SETTINGS) );
                  if ((start_temp + smesenie) == Xstart)
                  {
                    sync_fall = 1;
                  }
                  else
                  {
                    sync_fall = 0;
                  }
                  Xstart -= smesenie;
                  }
                  else {
                	 sync_fall = 1;
                	 adc_sinhros(Xstart, (int)start_temp + (size_buff-SIZE_BUF_SETTINGS));
                  }
            }
            else {
                if (((Usinhros_type == TUsinhros_type.FALL)||(Usinhros_type == TUsinhros_type.FRONT))&& (ADC_freq >= freq11)){
            	  int smesenie= (scale_t * Xmax/2);
                  if ((smesenie % 2) != 0) smesenie++;
                  Xstart = adc_sinhros(Xstart + smesenie, (int)start_temp + (size_buff-SIZE_BUF_SETTINGS) );
                  if ((start_temp + smesenie) == Xstart)
                  {
                    sync_fall = 1;
                  }
                  else
                  {
                    sync_fall = 0;
                  }
                  Xstart -= smesenie;
                }
                else {
               	 sync_fall = 1;
               	 adc_sinhros(Xstart, (int)start_temp + (size_buff-SIZE_BUF_SETTINGS) );
               }
            }
        }
        //==================================================================================================
        else
        {
            adc_sinhros(Xstart, Xstart + (size_buff-SIZE_BUF_SETTINGS) );//+ Xmax*scale_t
        }
        //==================================================================================================
        if (ADC_Interleaved_mode == 0)
        	Xstop = (Xstart + size_buff);
        else
        	Xstop = (Xstart + size_buff/2);

        if (Xstop > rxCount)
        {
            if (stream_viev == 0) Xstop = rxCount;
            else return start_temp;
        }

        Xpoz_for_RMS = Xstart;
        int j=0;
        minmax = 0;
        bufpoz=Xstart;

        DataPoint[] vertikal_line_values = new DataPoint[(int)(2)];
        graph.getViewport().setMaxX(Xmax_temp);

            if (sync_fall==0) {
              vertikal_line_values[0] =  new DataPoint(Xmax_temp/2, -127);//graph.getViewport().getMinY(false)
              vertikal_line_values[1] =  new DataPoint(Xmax_temp/2, 127);//graph.getViewport().getMaxY(false)
            }
            else {
            	vertikal_line_values[0] =  new DataPoint(0, 0);
            	vertikal_line_values[1] =  new DataPoint(0, 0);
            }


        if (ADC_Interleaved_mode == 0)
        {
            while ((j < Xmax_temp)&& (Xstart < Xstop))
            {
            	if (bufpoz > rxCount) break;

                if (readBuf(bufpoz, true, false) != 0)
                {
                    if (ADC_CH1_ON == 1)  ch1_values[j] =  new DataPoint(j, U1);
                    else ch1_values[j] =  new DataPoint(0, 0);

                    if (ADC_CH2_ON == 1)  ch2_values[j] = new DataPoint(j, U2);
                    else ch2_values[j] =  new DataPoint(0, 0);

                    if (j<(Xmax_temp)) j++;
                    else break;

                }
                else{
                	break;
                }
            }
        }
        else {
            while ((j < Xmax_temp)&& (Xstart < Xstop))
            {
                if (readBuf(bufpoz, false, false) != 0)
                {
                    if (ADC_CH1_ON == 1) {
                        ch1_values[j] =  new DataPoint(j, U1);
                        ch2_values[j] =  new DataPoint(0, 0);
                        if (j<(Xmax_temp)) j++;
                        else break;
                        ch1_values[j] =  new DataPoint(j, U2);
                        ch2_values[j] =  new DataPoint(0, 0);
                        if (j<(Xmax_temp)) j++;
                        else break;
                    }
                    else break;
                }
                else {
                	break;
                }
            }
        }

        series1.resetData(ch1_values);
        series2.resetData(ch2_values);
        series3.resetData(vertikal_line_values);

        bufpoz=Xstart;
        return Xstart;
    }
    
   private void refresh( ) {
   	if (settings_read_ok==0) return;
   	settings_read_ok=0;
   	disable_set_comands = 1;
               if (ADC_Interleaved_mode == 1)
               {
                  	//if (Text_interlive!=null) Text_interlive.setText("Interl.");
               }
               else
               {
                  	//if (Text_interlive!=null) Text_interlive.setText("");
               }
               set_u_ch1();
               set_u_ch2();

           if (CH1_dc_ac == 0)
           {
           	if (CH1_ACDC!=null) CH1_ACDC.setText("AC");
           }
           else
           {
           	if (CH2_ACDC!=null) CH1_ACDC.setText("DC");
           }
           if (CH2_dc_ac == 0)
           {
           	if (CH1_ACDC!=null) CH2_ACDC.setText("AC");
           }
           else
           {
           	if (CH2_ACDC!=null) CH2_ACDC.setText("DC");
           }

           if (auto_sinhros == 1)
               {
        	    if (sync_auto!=null) sync_auto.setText("auto");
                if (Usinhros_for_CH == 2)
                {
             	   sync_auto.setTextColor(Color.parseColor("#00FFFF"));
                }
                else if (Usinhros_for_CH == 1)
                {
               	   sync_auto.setTextColor(Color.parseColor("#00FF00"));
                }
               }
           else {
               if (Usinhros_for_CH == 2)
               {
            	   if (sync_auto!=null) sync_auto.setText("CH2");
            	   sync_auto.setTextColor(Color.parseColor("#00FFFF"));
               }
               else if (Usinhros_for_CH == 1)
               {
            	   if (sync_auto!=null) sync_auto.setText("CH1");
            	   sync_auto.setTextColor(Color.parseColor("#00FF00"));
               }
           }
               double vremya;
               if (ADC_Interleaved_mode==0) vremya = ((double)(Xmax * scale_t) /
               		(ADC_freq)) / KOL_KLETOK_T;
               else  vremya = ((double)(Xmax * scale_t) / (ADC_freq*2)) / KOL_KLETOK_T;

               String message;
               if (vremya < 1)
               {

               vremya = vremya * 1000;
               if (vremya >= 1)  message= String.format("%.1f", vremya) + " мc";
               else
               {
                   vremya = vremya * 1000;
                   if (vremya >= 1) message = String.format("%.0f", vremya) + " мкc";
                   else message = String.format("%.0f", vremya*1000) + " нc";
               }
               }
               else message = String.format("%.1f", vremya) + " c";
               if (t_auto!=null) t_auto.setText(message);

           if (auto_time == 1)
           {
           	if (t_auto!=null) t_auto.setTextColor(Color.RED) ;
           }
           else if(auto_time == 0)
           {
               if (t_auto!=null) t_auto.setTextColor(Color.WHITE) ;
           }

           if (stream_viev == 1)
           {
             if (screen_bt_flow!=null) screen_bt_flow.setTextColor(Color.RED) ;
           }
           else if(stream_viev == 0)
           {
             if (screen_bt_flow!=null) screen_bt_flow.setTextColor(Color.WHITE) ;
           }


           if (auto_CH1_Udel == 1 )
           {
           	if (ch1_auto!=null)ch1_auto.setTextColor(Color.RED) ;
           }
           else if (auto_CH1_Udel == 0 )
           {
           	if (ch1_auto!=null)ch1_auto.setTextColor(Color.parseColor("#00FF00")) ;
           }

           if (auto_CH2_Udel == 1)
           {
               if (ch2_auto!=null)ch2_auto.setTextColor(Color.RED) ;
           }
           else if (auto_CH2_Udel == 0)
           {
           	if (ch2_auto!=null)ch2_auto.setTextColor(Color.parseColor("#00FFFF")) ;
           }
               if (Usinhros_type == TUsinhros_type.FRONT)
               {
            	  if (sync_type!=null)sync_type.setText("front");
               }
               else if (Usinhros_type == TUsinhros_type.FALL)
               {
            	   if (sync_type!=null)sync_type.setText("fall");
               }
               else if (Usinhros_type == TUsinhros_type.FRONT_WAIT)
               {
            	   if (sync_type!=null)sync_type.setText("up");
               }
               else if (Usinhros_type == TUsinhros_type.FALL_WAIT)
               {
            	   if (sync_type!=null)sync_type.setText("down");
               }
               else if (Usinhros_type == TUsinhros_type.OFF)
               {
            	   if (sync_type!=null)sync_type.setText("OFF");
               }

               disable_set_comands = 0;
   }

    private enum TUsinhros_type {
     FRONT,
     FALL,
     FRONT_WAIT,
     FALL_WAIT,
     OFF,
    }
private enum Tgrafic_type
    {
        AVERAGE,
        MINMAX
    }
   
    enum Direction {LEFT, RIGHT;}
   
}
