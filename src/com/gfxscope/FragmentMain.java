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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
//import android.media.AudioManager;
//import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.WorkerThread;
import android.support.v4.app.Fragment;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.models.TGraficType;
import com.utils.Utils;
import android.widget.VerticalSeekBar;

public class FragmentMain extends Fragment implements TCPListener{
    public static final @NonNull String TAG = Utils.getTag(FragmentMain.class);

    private static GraphView graph;
    private static Runnable mTimerScrollLeft;
    private static Runnable mTimerScrollRighr;
    private static Runnable mTimerRefreshRMS;
    private static Runnable mTimerDont_read_settings;
    private static Runnable mTimerGrafik_refresh;
    private static Runnable mTimerSettings_refresh;
    private static Runnable mTimerBitrate_refresh;
    private static Runnable mTimer_scrBt;
    private final static Handler mHandler = new Handler();
    
    private static int pause=1;
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
    private static int bufpoz;
    private static int Xpoz_for_RMS;
    private static int Xpoz;
    private int Xpoz_old = 1;
    private int bufpoz_old;
    private double U1;
    private double U2;
    private final int     adres_KOL_KLETOK_T=6;
    private final int     adres_ADC_freqH=7;
    private final int     adres_ADC_freqL=8;
    //private final int     adres_ADC_max_freqH=9;
    //private final int     adres_ADC_max_freqL=10;
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
    private static SeekBar seekBar = null;
    private static com.jjoe64.graphview.GraphView grafik= null;
    private static Button screen_bt_pause = null;
    private static Button screen_bt_plus = null;
    private static Button screen_bt_minus = null;
    private static Button screen_bt_free = null;
    private static Button screen_bt_ch1plus = null;
    private static Button screen_bt_ch1minus = null;
    private static Button screen_bt_ch2plus = null;
    private static Button screen_bt_ch2minus = null;
    private static Button screen_bt_Tplus = null;
    private static Button screen_bt_Tminus = null;
    private int view_scrbt=0;
    private int first_load=0;
    private static Button send = null;
    private Button CH1_ACDC = null;
    private Button CH2_ACDC = null;
    private Button t_auto = null;
    private Button sync_type = null;
    private Button sync_auto = null;
    private Button ch1_auto = null;
    private Button ch2_auto = null;
    private Button MenuBtn = null;
    private Button SaveBtn = null;
    private Button OpenBtn = null;
 
//    private  boolean CH1_viev_RMS=false, CH2_viev_RMS=false; 
    private static int rxCount;
//    private static int rxIn;
//    private static int rxOut;
    private static int rxCount_old;
    private static int scale_x=1;
//    private int caunt_frq = 0;      

    private final static int OTSTUP_SLEVA = 11;
    private final static int OTSTUP_SPRAVA = 10;
    private final static int ILI9341_HEIGHT = 480;
    public static int Xmax_scale_x_max = ((ILI9341_HEIGHT - (OTSTUP_SLEVA + OTSTUP_SPRAVA)));
    private volatile static int Xmax = Xmax_scale_x_max;
    
    private static int SIZE_BUF_SETTINGS = 64;
    private static int SIZE_BUF_RX_for_MK = 100*1024;
    private static int SIZE_BUF_RX = 20*10*(SIZE_BUF_RX_for_MK+SIZE_BUF_SETTINGS);
    private static byte[] ADC_Buff = new byte[SIZE_BUF_RX]; 
    
    private static int scale_t=1;
    private static int scale_t_old=1;
    private static int size_buff=(Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS ;//100000;
    
//    private int n_line = 0, X1, X2;
    private enum TUsinhros_type {
        FRONT,
        FALL,
        FRONT_WAIT,
        FALL_WAIT,
        OFF,
    }

    private static TUsinhros_type Usinhros_type=TUsinhros_type.FRONT;

    private static TGraficType grafic_type = TGraficType.AVERAGE;
    private int AlternativVoltDiapason;
    private static int KOL_KLETOK_T = 17;
    private static int KOL_KLETOK_T_old = 17;
    private final static int KOL_KLETOK_U = 10;
//    private final int ADC_STM32_diapason =256;
    private final static int ADC_9288_diapason =256;
    private final static double ADC9288_pol_diapasona =128;//(ADC_9288_diapason/2);
//    private final double  ADC_9288_NIn =1.245;
//    private final double  ADC_STM32_Ref =2.495;
    private final static double  ADC_9288_Ref= 1.024;
    private final static double  AMP2_gain =10.0;
//    private final static double  ADC_9288_delitel1 = ((ADC_9288_Ref*(50.0*100.0) )/(ADC_9288_diapason*AMP2_gain));//"50"
//    private final static double  ADC_9288_delitel2 = ((ADC_9288_Ref*(20.0*100.0) )/(ADC_9288_diapason*AMP2_gain));//"20"
//    private final static double  ADC_9288_delitel3 = ((ADC_9288_Ref*(10.0*100.0) )/(ADC_9288_diapason*AMP2_gain));//"10"
//    private final static double  ADC_9288_delitel4 = ((ADC_9288_Ref*(5.0*100.0)  )/(ADC_9288_diapason*AMP2_gain));//"5"
//    private final static double  ADC_9288_delitel5 = ((ADC_9288_Ref*(2.0*100.0)  )/(ADC_9288_diapason*AMP2_gain));//"2"
//    private final static double  ADC_9288_delitel6 = ((ADC_9288_Ref*(1.0*100.0)  )/(ADC_9288_diapason*AMP2_gain));//"1"
//    private final static double  ADC_9288_delitel7 = ((ADC_9288_Ref*50.0        )/(ADC_9288_diapason*AMP2_gain)) ;//"0.5"
//    private final static double  ADC_9288_delitel8 = ((ADC_9288_Ref*20.0         )/(ADC_9288_diapason*AMP2_gain));//"0.2"
//    private final static double  ADC_9288_delitel9 = ((ADC_9288_Ref*10.0         )/(ADC_9288_diapason*AMP2_gain));//"0.1"
//    private final static double  ADC_9288_delitel10 =((ADC_9288_Ref*5.0         )/(ADC_9288_diapason*AMP2_gain)) ;//"50m"
//    private final static double  ADC_9288_delitel11 =((ADC_9288_Ref*2.0         )/(ADC_9288_diapason*AMP2_gain)) ;//"20m"
//    private final static double  ADC_9288_delitel12 =((ADC_9288_Ref*1.0         )/(ADC_9288_diapason*AMP2_gain)) ;//"10m"
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
    private final static int freq0= 54000000;
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
    private static byte ADC_Interleaved_mode;
    private static byte play = 0;
    private static byte dont_read_settings;
    private static byte ADC_CH1_ON = 1;
    private static byte ADC_CH2_ON = 1;
    private int bitrate;
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
    static float CH1_correct=1;
    static float CH2_correct=1;
    public static int Connecting;
    private static int settings_read_ok;
    private static int max_scale_t=32;
    
	public static TCPCommunicator tcpClient;
	private static Handler UIHandler = new Handler();
    
    static final String PREF_IPadress = "IPadress";
    static final String PREF_MINMAX = "MINMAX";    
    static SharedPreferences settings;
    static SharedPreferences settings2;
	public static Editor prefEditor;

	private static final String LOG_TAG = "FlankLOGS";
	private int error_bitrate=0;

    static final int DELTA = 50;
	protected static final int INVALID_POINTER_ID = 0;

    private float mLastTouchX; 
    private float mLastTouchY;
    private  int tfree;
    
    DataPoint[] ch1_values = new DataPoint[(int)(Xmax)];
    DataPoint[] ch2_values = new DataPoint[(int)(Xmax)];
        
    
    static @NonNull TGraficType get_grafic_type() {
    	return grafic_type;
    }    
    
    static void set_grafic_type(int type) {
    	grafic_type = type == 0 ? TGraficType.AVERAGE : TGraficType.MINMAX;
    }

	@Override
    @WorkerThread
    public void onTCPMessageRecieved(byte[] bs) {
        for (final byte b : bs) {
            if (rxCount < ADC_Buff.length) {
                ADC_Buff[rxCount] = b;
                rxCount++;
            } else {
                //noinspection StatementWithEmptyBody
                while (dont_use_bufpoz == 1) {
                    // empty
                }

                rxCount = 0;
                bufpoz = 0;
            }
        }
    }

	@Override
	public void onTCPConnectionStatusChanged(boolean isConnectedNow) {
        Connecting = isConnectedNow ? 1 : 0;
	}

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
                	if (play==1){
                	                	
                    if (ADC_freq >= freq11)
                    {
                      //if (( rxCount > temp_rxCount + size_buff)&&(rxCount >= size_buff * 2))
                      //{  
                        bufpoz = rxCount - size_buff * 2;
                        Grafik_refresh(bufpoz, true);
                        temp_rxCount = rxCount;
                      //}                      	
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
     public static  void osc_set_param(String param) {  
  	   if (disable_set_comands == 1) return;
  	   
//          if (mTcpClient != null) {
//              mTcpClient.sendMessage(param + "\r\n");
//          }     
          
          TCPCommunicator.writeToSocket(param + "\r\n", UIHandler);
       
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

          message = "get_buff 1";
          if (send!=null)send.setText("Стоп");  
          osc_set_param(message);
          osc_set_param(message);
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
        osc_set_param(message);
        osc_set_param(message);
        osc_set_param(message);
        
//		// ОТКЛЮЧИТЬСЯ
//        if (mTcpClient != null) {            
//          	  mTcpClient.stopClient();
//          	  mTcpClient = null;	              
//        }
        
//        TCPCommunicator.closeStreams();
        
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
             	           osc_set_param("get_buff 1"); 
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
    	
	       //if (mTcpClient == null) {                     
 	            conect_begin();
 	       //}
	       
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
            if (vremya >= 1)  message= String.format("%.0f", vremya) + " мc";
            else
            {
                vremya = vremya * 1000;
                if (vremya >= 1) message = String.format("%.0f", vremya) + " мкc";
                else message = String.format("%.0f", vremya*1000) + " нc";
            }
        }
        else message = String.format("%.0f", vremya) + " c";
        t_auto.setText(message);
         //dont_read_settings = 0;

    }
    
	private void time_del_minus() {
//    	if (Usinhros_type==TUsinhros_type.OFF)max_scale_t = 64;//ADC1_MAX/Xmax_scale_x_max;
//  	    else  max_scale_t=16; 
		
		// TODO Auto-generated method stub
        if  ((play==1) && (ADC_freq<freq1)){						
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
            if (vremya >= 1)  message= String.format("%.0f", vremya) + " мc";
            else
            {
                vremya = vremya * 1000;
                if (vremya >= 1) message = String.format("%.0f", vremya) + " мкc";
                else message = String.format("%.0f", vremya*1000) + " нc";
            }
        }
        else message = String.format("%.0f", vremya) + " c";
        t_auto.setText(message);
        
         //dont_read_settings = 0;
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

	

//	private void setupDialog() {
//		dialog = new ProgressDialog(this,ProgressDialog.STYLE_SPINNER);
//		dialog.setTitle("Loading");
//		dialog.setMessage("Please wait...");
//		dialog.setIndeterminate(true);
//		dialog.show();
//	}
	
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
            
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if((requestCode==123 ) && (resultCode==-1)) {
            Uri selectedfile = data.getData(); //The uri with the location of the file        
            LoadAndDisplayWurfs(selectedfile.getPath());
        }
    }
        

    
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
      MenuBtn = (Button) getView().findViewById(R.id.settingsBtn);
      SaveBtn = (Button) getView().findViewById(R.id.SaveBtn);
      OpenBtn = (Button) getView().findViewById(R.id.OpenBtn);
      grafik= (com.jjoe64.graphview.GraphView)  getView().findViewById(R.id.OscillPlot);          
      screen_bt_pause = (Button) getView().findViewById(R.id.screen_bt_pause);  
  	  screen_bt_plus = (Button) getView().findViewById(R.id.screen_bt_plus);  
      screen_bt_minus = (Button) getView().findViewById(R.id.screen_bt_minus); 
      screen_bt_free = (Button) getView().findViewById(R.id.screen_bt_Tfree);       
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

      MenuBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              final Activity activity = getActivity();
              if (Utils.isDead(activity) || !(activity instanceof MainActivity)) {
                  return;
              }

              ((MainActivity) activity).showSettings();
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
    		}
    		else {
    		  screen_bt_plus.setVisibility(View.VISIBLE);
    		  screen_bt_minus.setVisibility(View.VISIBLE);  
    		  screen_bt_free.setVisibility(View.VISIBLE);
    		  screen_bt_ch1plus.setVisibility(View.INVISIBLE); 
    		  screen_bt_ch1minus.setVisibility(View.INVISIBLE); 
    		  screen_bt_ch2plus.setVisibility(View.INVISIBLE); 
    		  screen_bt_ch2minus.setVisibility(View.INVISIBLE);    		
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
    		mHandler.removeCallbacks(mTimer_scrBt);
    		mTimer_scrBt=null;
    	}
	}
	
	
	
	
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
            	if (AlternativVoltDiapason==0) u = 50;
            	else if (AlternativVoltDiapason==1) u = 25;
                break;
            case 2:  
            	if (AlternativVoltDiapason==0) u = 20;        	
            	else if (AlternativVoltDiapason==1) u = 10;
                break;
            case 3: 
            	if (AlternativVoltDiapason==0) u = 10;        	
            	else if (AlternativVoltDiapason==1) u = 5;
                break;
            case 4: 
            	if (AlternativVoltDiapason==0) u = 5;        	
            	else if (AlternativVoltDiapason==1) u = 2.5;
                break;
            case 5: 
            	if (AlternativVoltDiapason==0) u = 2;        	
            	else if (AlternativVoltDiapason==1) u = 1;
                break;
            case 6: 
            	if (AlternativVoltDiapason==0) u = 1;        	
            	else if (AlternativVoltDiapason==1) u = 0.5;
                break;
            case 7: 
            	if (AlternativVoltDiapason==0) u = 0.5;        	
            	else if (AlternativVoltDiapason==1) u = 0.25;
                break;
            case 8: 
            	if (AlternativVoltDiapason==0) u = 0.2;        	
            	else if (AlternativVoltDiapason==1) u = 0.1;
                break;
            case 9: 
            	if (AlternativVoltDiapason==0) u = 0.10;        	
            	else if (AlternativVoltDiapason==1) u = 0.05;
                break;
            case 10: 
            	if (AlternativVoltDiapason==0) u = 0.05;        	
            	else if (AlternativVoltDiapason==1) u = 0.025;
                break;
            case 11:  
            	if (AlternativVoltDiapason==0) u = 0.02;        	
        	    else if (AlternativVoltDiapason==1) u = 0.01;
                break;
            case 12: 
            	if (AlternativVoltDiapason==0) u = 0.010;        	
            	else if (AlternativVoltDiapason==1) u = 0.005;
                break;
        }
        
        CH1_delitel= ((ADC_9288_Ref*u*100)/(ADC_9288_diapason*AMP2_gain))* CH1_correct;
//        switch (CH1_n_delitel)
//        {
//            case (0): { CH1_delitel = ADC_9288_delitel6 * CH1_correct; }
//                break;
//            case (1): { CH1_delitel = ADC_9288_delitel1* CH1_correct; }
//                break;
//            case (2): { CH1_delitel = ADC_9288_delitel2* CH1_correct; }
//                break;
//            case (3): { CH1_delitel = ADC_9288_delitel3* CH1_correct; }
//                break;
//            case (4): { CH1_delitel = ADC_9288_delitel4* CH1_correct; }
//                break;
//            case (5): { CH1_delitel = ADC_9288_delitel5* CH1_correct; }
//                break;
//            case (6): { CH1_delitel = ADC_9288_delitel6* CH1_correct; }
//                break;
//            case (7): { CH1_delitel = ADC_9288_delitel7* CH1_correct; }
//                break;
//            case (8): { CH1_delitel = ADC_9288_delitel8* CH1_correct; }
//                break;
//            case (9): { CH1_delitel = ADC_9288_delitel9* CH1_correct; }
//                break;
//            case (10): { CH1_delitel = ADC_9288_delitel10* CH1_correct; }
//                break;
//            case (11): { CH1_delitel = ADC_9288_delitel11* CH1_correct; }
//                break;
//            case (12): { CH1_delitel = ADC_9288_delitel12* CH1_correct; }
//                break;
//        }
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
        //CH1_Umax = u; 
        //CH1_Umin = -u;
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
    	if (AlternativVoltDiapason==0) u = 50;
    	else if (AlternativVoltDiapason==1) u = 25;
        break;
    case 2:  
    	if (AlternativVoltDiapason==0) u = 20;        	
    	else if (AlternativVoltDiapason==1) u = 10;
        break;
    case 3: 
    	if (AlternativVoltDiapason==0) u = 10;        	
    	else if (AlternativVoltDiapason==1) u = 5;
        break;
    case 4: 
    	if (AlternativVoltDiapason==0) u = 5;        	
    	else if (AlternativVoltDiapason==1) u = 2.5;
        break;
    case 5: 
    	if (AlternativVoltDiapason==0) u = 2;        	
    	else if (AlternativVoltDiapason==1) u = 1;
        break;
    case 6: 
    	if (AlternativVoltDiapason==0) u = 1;        	
    	else if (AlternativVoltDiapason==1) u = 0.5;
        break;
    case 7: 
    	if (AlternativVoltDiapason==0) u = 0.5;        	
    	else if (AlternativVoltDiapason==1) u = 0.25;
        break;
    case 8: 
    	if (AlternativVoltDiapason==0) u = 0.2;        	
    	else if (AlternativVoltDiapason==1) u = 0.1;
        break;
    case 9: 
    	if (AlternativVoltDiapason==0) u = 0.10;        	
    	else if (AlternativVoltDiapason==1) u = 0.05;
        break;
    case 10: 
    	if (AlternativVoltDiapason==0) u = 0.05;        	
    	else if (AlternativVoltDiapason==1) u = 0.025;
        break;
    case 11:  
    	if (AlternativVoltDiapason==0) u = 0.02;        	
	    else if (AlternativVoltDiapason==1) u = 0.01;
        break;
    case 12: 
    	if (AlternativVoltDiapason==0) u = 0.010;        	
    	else if (AlternativVoltDiapason==1) u = 0.005;
        break;
}
        CH2_delitel= ((ADC_9288_Ref*u*100)/(ADC_9288_diapason*AMP2_gain))* CH2_correct;
//        switch (CH2_n_delitel)
//        {
//            case (0): { CH2_delitel = ADC_9288_delitel6 * CH2_correct; }
//                break;
//            case (1): { CH2_delitel = ADC_9288_delitel1* CH2_correct; }
//                break;
//            case (2): { CH2_delitel = ADC_9288_delitel2* CH2_correct; }
//                break;
//            case (3): { CH2_delitel = ADC_9288_delitel3* CH2_correct; }
//                break;
//            case (4): { CH2_delitel = ADC_9288_delitel4* CH2_correct; }
//                break;
//            case (5): { CH2_delitel = ADC_9288_delitel5* CH2_correct; }
//                break;
//            case (6): { CH2_delitel = ADC_9288_delitel6* CH2_correct; }
//                break;
//            case (7): { CH2_delitel = ADC_9288_delitel7* CH2_correct; }
//                break;
//            case (8): { CH2_delitel = ADC_9288_delitel8* CH2_correct; }
//                break;
//            case (9): { CH2_delitel = ADC_9288_delitel9* CH2_correct; }
//                break;
//            case (10): { CH2_delitel = ADC_9288_delitel10* CH2_correct; }
//                break;
//            case (11): { CH2_delitel = ADC_9288_delitel11* CH2_correct; }
//                break;
//            case (12): { CH2_delitel = ADC_9288_delitel12* CH2_correct; }
//                break;
//        }
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
        //CH2_Umax = u; 
        //CH2_Umin = -u;
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
            if (buff_16bit[adres_ADC_Interleaved_mode] != 0)
            {
               ADC_Interleaved_mode = 1;
            }
            else
            {
               ADC_Interleaved_mode = 0;
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
            if ((buff_16bit[adres_CH1_n_delitel]>>8)==1) AlternativVoltDiapason=1;
            else  AlternativVoltDiapason=0;
            set_u_ch1();
            temp = (buff_16bit[adres_CH2_n_delitel])&0xFF;
            if ((temp < 14) && (temp != 0x00))
            {
               CH2_n_delitel = (int)(temp - 1);
            }
            if (CH2_n_delitel > 0) { ADC_CH2_ON = 1; }
            else ADC_CH2_ON = 0;
            if ((buff_16bit[adres_CH2_n_delitel]>>8)==1) AlternativVoltDiapason=1;
            else  AlternativVoltDiapason=0;
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
            if ((temp != 0) && (temp < 128)&&(tfree==0)) scale_t = (int)temp;
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
        		&& (bufpoz + (2 + SIZE_BUF_SETTINGS) < rxCount ) ) {


            //================================================================================================
            //================================================================================================
            if (avr) {
                int cound_temp = 0;
                U1 = 0;
                U2 = 0;
                double u1temp = 0;
                double u2temp = 0;

                long Uadc1, Uadc2;
                Uadc1 = Uadc2 = (minmax == 1) || (grafic_type == TGraficType.AVERAGE) ? 0 : 255;

                for (int j = 0; j < scale_t; j ++) {
                    if ((ADC_Buff[bufpoz] == (byte)'o') &&
                    (ADC_Buff[bufpoz + 1] == (byte)'s') &&
                    (ADC_Buff[bufpoz + 2] == (byte)'c') &&
                    (ADC_Buff[bufpoz + 3] == (byte)' ') &&
                    (ADC_Buff[bufpoz + 4] == (byte)'v') &&
                    (ADC_Buff[bufpoz + 5] == (byte)'3')) {
                    	bufpoz = bufpoz + SIZE_BUF_SETTINGS;
                    	readSettings(bufpoz);                    	
                    }                    
                    
                    V1 = ADC_Buff[bufpoz] & 0xFF;      //прочитать из него символ
                    V2 = ADC_Buff[bufpoz+1]& 0xFF;      //прочитать из него символ
                    
                    cound_temp++;
                    if (grafic_type == TGraficType.AVERAGE) {
                        Uadc1 += V1;
                        Uadc2 += V2;
                    } else {
                        if (minmax == 1) {
                            if (Uadc1 < V1) Uadc1 = V1;
                            if (Uadc2 < V2) Uadc2 = V2;
                        } else {
                            if (Uadc1 > V1) Uadc1 = V1;
                            if (Uadc2 > V2) Uadc2 = V2;
                        }
                    }
                    if (bufpoz < (ADC_Buff.length - ( 6 ))) {
                        bufpoz = bufpoz + 2;
                    } else {
                        break;
                    }
                }
                
                if (minmax == 0){
                    minmax = 1;
                }

                if (grafic_type == TGraficType.AVERAGE) {
                    if (delitel) {
                        u1temp = CH1_delitel * (((double)Uadc1 / cound_temp) - ADC9288_pol_diapasona);
                        u2temp = CH2_delitel * (((double)Uadc2 / cound_temp) - ADC9288_pol_diapasona);
                    } else {
                        u1temp =  (((double)Uadc1 / cound_temp) - ADC9288_pol_diapasona);
                        u2temp =  (((double)Uadc2 / cound_temp) - ADC9288_pol_diapasona);
                    }
                } else {
                    if (delitel) {
                        u1temp = CH1_delitel * ((double)Uadc1 - ADC9288_pol_diapasona);
                        u2temp = CH2_delitel * ((double)Uadc2 - ADC9288_pol_diapasona);
                    } else {
                        u1temp =((double)Uadc1 - ADC9288_pol_diapasona);
                        u2temp = ((double)Uadc2 - ADC9288_pol_diapasona);
                    }
                }

                U1 = u1temp;
                U2 = u2temp;
            } else {
                //================================================================================================  	        	
                for (int i = 0; i < SIZE_BUF_SETTINGS; i++) {
                    if ((bufpoz - i) < 0) {
                        break;
                    }

                    if (ADC_Buff[bufpoz - i] == (byte)'o') {
                        if (ADC_Buff[(bufpoz-i)] == (byte)'o' &&
                        ADC_Buff[(bufpoz-i) + 1] == (byte)'s' &&
                        ADC_Buff[(bufpoz-i) + 2] == (byte)'c' &&
                        ADC_Buff[(bufpoz-i) + 3] == (byte)' ' &&
                        ADC_Buff[(bufpoz-i) + 4] == (byte)'v' &&
                        ADC_Buff[(bufpoz-i) + 5] == (byte)'3') {
                            bufpoz = (bufpoz - i) + SIZE_BUF_SETTINGS;
                            readSettings(bufpoz);
                            break;
                        }
                    }
                }
            	//================================================================================================  
            	
                if (ADC_Interleaved_mode == 0) {
                    V1 = ADC_Buff[bufpoz] & 0xFF;        //прочитать из него символ
                    V2 = ADC_Buff[bufpoz + 1] & 0xFF;      //прочитать из него символ

                    if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) {
                        Xpoz = Xpoz + 2;
                    }
                    
                    if (delitel) {
                        U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                        U2 = CH2_delitel * ((double)V2 - ADC9288_pol_diapasona);
                    } else {
                        U1 = ((double)V1 - ADC9288_pol_diapasona);
                        U2 = ((double)V2 - ADC9288_pol_diapasona);
                    }
                } else {
                    V1 = ADC_Buff[bufpoz] & 0xFF;        //прочитать из него символ
                    V2 = ADC_Buff[bufpoz + 1] & 0xFF;      //прочитать из него символ

                    if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) {
                        Xpoz = Xpoz + 2;
                    }
                    
                    if (delitel) {
                        U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                        U2 = CH1_delitel * ((double)V2 - ADC9288_pol_diapasona);
                    } else {
                        U1 = ((double)V1 - ADC9288_pol_diapasona);
                        U2 = ((double)V2 - ADC9288_pol_diapasona);
                    }
                }
            }       
            //================================================================================================
            dont_use_bufpoz=0;
            return 1;
        } else {
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
    private int readBuf() {
        int V1=0, V2=0;
               
        //================================================================================================
        // if ((Xpoz + ( 6 + SIZE_BUF_SETTINGS) < ADC_Buff.length) && (Xpoz >= SIZE_BUF_SETTINGS)
        // 		&& (Xpoz + ( 6 + SIZE_BUF_SETTINGS)) < rxCount  )
        if ((Xpoz + 2  < ADC_Buff.length) && (Xpoz + 2  < rxCount  )) {
            //================================================================================================
            //            for (int i = 0; i < SIZE_BUF_SETTINGS; i++)
            //            {
            //              if ((Xpoz - i) < 0) break;
            //              if ((byte)ADC_Buff[Xpoz - i] == (byte)'o')
            //              {
            //                if (((byte)ADC_Buff[(Xpoz-i)] == (byte)'o') &&
            //                    ((byte)ADC_Buff[(Xpoz-i) + 1] == (byte)'s') &&
            //                    ((byte)ADC_Buff[(Xpoz-i) + 2] == (byte)'c') &&
            //                    ((byte)ADC_Buff[(Xpoz-i) + 3] == (byte)' ') &&
            //                    ((byte)ADC_Buff[(Xpoz-i) + 4] == (byte)'v') &&
            //                    ((byte)ADC_Buff[(Xpoz-i) + 5] == (byte)'3'))
            //                {
            //                    Xpoz = (Xpoz - i) + SIZE_BUF_SETTINGS;
            //                    //readSettings(Xpoz);
            //                    break;
            //                }
            //              }
            //            }
            //================================================================================================
            //================================================================================================
   
            if (ADC_Interleaved_mode == 0) {
                V1 = ADC_Buff[Xpoz] & 0xFF;        //прочитать из него символ
                V2 = ADC_Buff[Xpoz + 1] & 0xFF;      //прочитать из него символ

                if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) {
                    Xpoz = Xpoz + 2;
                }

                U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                U2 = CH2_delitel * ((double)V2 - ADC9288_pol_diapasona);
            } else {
                V1 = ADC_Buff[Xpoz] & 0xFF;        //прочитать из него символ
                V2 = ADC_Buff[Xpoz + 1] & 0xFF;      //прочитать из него символ

                if ((Xpoz + 2 < ADC_Buff.length ) && (Xpoz + 2  < rxCount )) {
                    Xpoz = Xpoz + 2;
                }

                U1 = CH1_delitel * ((double)V1 - ADC9288_pol_diapasona);
                U2 = CH1_delitel * ((double)V2 - ADC9288_pol_diapasona);
            }

            return 1;
        } else {
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
    	//long bufpoz_temp= bufpoz;    
        byte sync_fall = 1;
  
        int Xstop;
        int Xmax_temp;
        
        //if ((Xstart % 2)!=0) Xstart++;
        
        int start_temp = Xstart;
        
        if ((ADC_CH1_ON == 0) && (ADC_CH2_ON == 0)) return start_temp; 
        
        if ((Usinhros_type == TUsinhros_type.OFF) || (ADC_freq < freq11)) size_buff = (Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS;
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
        
        //if ((Xstart % 2) != 0) Xstart++;
//        if (Xstart + (Xmax_temp * scale_t * 2) > rxCount ){              
//        	if (rxCount > (Xmax_temp * scale_t * 2)) Xstart = rxCount - (Xmax_temp * scale_t * 2);   
//        	else return;
//        }
        
        if (Xstart > rxCount) Xstart = rxCount;
        if (Xstart < 0) Xstart = 0;
        
        Xstop = Xstart + size_buff;          
        if (Xstop > rxCount)
        {
            Xstop = rxCount;
        }             
        
//        if 	(Xstart>SIZE_BUF_SETTINGS*2){
//        for (int i = 0; i < SIZE_BUF_SETTINGS*2; i++)
//        {
//          if ((Xstart - i) < 0) break;
//          if ((byte)ADC_Buff[Xstart - i] == (byte)'o')
//          {
//            if (((byte)ADC_Buff[(Xstart-i)] == (byte)'o') &&
//                ((byte)ADC_Buff[(Xstart-i) + 1] == (byte)'s') &&
//                ((byte)ADC_Buff[(Xstart-i) + 2] == (byte)'c') &&
//                ((byte)ADC_Buff[(Xstart-i) + 3] == (byte)' ') &&
//                ((byte)ADC_Buff[(Xstart-i) + 4] == (byte)'v') &&
//                ((byte)ADC_Buff[(Xstart-i) + 5] == (byte)'3')) 
//            {                                    
//            	Xstart = (Xstart - i) + SIZE_BUF_SETTINGS*2; 
//                readSettings(Xstart);
//                break;
//            }
//          }
//        } 	
//        }         
//        else 
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
                        
                        if ((Usinhros_type == TUsinhros_type.OFF) || (ADC_freq < freq11)) size_buff = (Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS;
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
                                    if ((Usinhros_type == TUsinhros_type.OFF) || (ADC_freq < freq11)) size_buff = (Xmax_scale_x_max * scale_t + 2048) * 2 + SIZE_BUF_SETTINGS;
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
            Xstop = rxCount;
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
               if (vremya >= 1)  message= String.format("%.0f", vremya) + " мc";
               else
               {
                   vremya = vremya * 1000;
                   if (vremya >= 1) message = String.format("%.0f", vremya) + " мкc";
                   else message = String.format("%.0f", vremya*1000) + " нc";
               }
               }
               else message = String.format("%.0f", vremya) + " c";
               if (t_auto!=null) t_auto.setText(message);                
 
           if (auto_time == 1)
           {
           	if (t_auto!=null) t_auto.setTextColor(Color.RED) ;
           }
           else if(auto_time == 0)
           {
               if (t_auto!=null) t_auto.setTextColor(Color.WHITE) ;
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
   
}
