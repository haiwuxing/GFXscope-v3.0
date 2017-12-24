package com.TCP_Client;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;

import com.gfxscope.FragmentMain;


public class TCPCommunicator {
	static InitTCPClientTask task=null;
	private static TCPCommunicator uniqInstance;
	private static String serverHost;
	private static int serverPort;
	private static List<TCPListener> allListeners;
	private static BufferedWriter out;
	private static DataInputStream in; //BufferedReader
	private static Socket s;
	private static Handler UIHandler;
	private static boolean mRun = false;
	
	private TCPCommunicator()	
	{
		allListeners = new ArrayList<TCPListener>();
	}
	public static TCPCommunicator getInstance()
	{
		if(uniqInstance==null)
		{
			uniqInstance = new TCPCommunicator();
		}
		return uniqInstance;
	}

	public static  TCPWriterErrors writeToSocket(final String outMsg,Handler handle)
	{
		UIHandler=handle;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try
				{
			        out.write(outMsg);
			        out.flush();
			        Log.i("TcpClient", "sent: " + outMsg);
				}
				catch(Exception e)
				{
					UIHandler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.i("TcpClient", "a problem has occured, the app might not be able to reach the server");
							//Toast.makeText(appContext ,"a problem has occured, the app might not be able to reach the server", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}

		};
		Thread thread = new Thread(runnable);
		thread.start();
		return TCPWriterErrors.OK;

	}

	public static void addListener(TCPListener listener)
	{
		allListeners.clear();
		allListeners.add(listener);
	}
			
	public static void removeAllListeners()
	{
		allListeners.clear();
	}

	public static void closeStreams()
	{
		try
		{   mRun = false;
			s.close();
			in.close();
			out.close();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public static String getServerHost() {
		return serverHost;
	}
	
	public static void setServerHost(String serverHost) {
		TCPCommunicator.serverHost = serverHost;
	}

	public static int getServerPort() {
		return serverPort;
	}

	public  void setServerPort(int serverPort) {
		TCPCommunicator.serverPort = serverPort;
	}

	public static void cancelTask() {
		    if (task == null) return;
		    Log.d("TcpClient", "cancel result: " + task.cancel(true));
	}

	public  TCPWriterErrors init(String host,int port)
	{
		setServerHost(host);
		setServerPort(port);
		task = new InitTCPClientTask();
		task.execute(new Void[0]);
		return TCPWriterErrors.OK;
	}
	  
	public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK}

	public class InitTCPClientTask extends AsyncTask<Void, Void, Void>
	{
		public InitTCPClientTask()
		{

		}

	    @Override
	    protected void onCancelled() {
	      super.onCancelled();
	      Log.d("TcpClient", "TcpClient Cancel");
	    }

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			mRun = true;

			try
			{
				 s = new Socket(getServerHost(), getServerPort());
		         //in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		         out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
		         // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
	             in = new DataInputStream(s.getInputStream());

		         for(TCPListener listener:allListeners)
			        	listener.onTCPConnectionStatusChanged(true);

		        while(mRun)
		        {
                        // count the available bytes form the input stream
                        int count = in.available();

                        if (count>FragmentMain.SIZE_BUF_SETTINGS){
                          // create buffer
                          byte[] bs = new byte[count];

                          in.read(bs);
                    	  //call the method messageReceived from MyActivity class
                    	  for(TCPListener listener:allListeners)
				          	  listener.onTCPMessageRecieved(bs);

                        }
                        else Thread.sleep(100);

                    	if (isCancelled()) {
                    		Log.d("TcpClient", "TcpClient Cancel!!!");
                    		return null;
                    	}
		        }

		    }

		    catch (UnknownHostException e) {
		        e.printStackTrace();
		    }
		    catch (IOException e) {
		        e.printStackTrace();
		    }
		    catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

//			finally {
//                //the socket must be closed. It is not possible to reconnect to this socket
//                // after it is closed, which means a new socket instance has to be created.
//                try {
//					s.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
			Log.d("TcpClient", "TcpClient Cancel!");
			return null;
		}
	}
}
