package com.TCP_Client;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class TcpCommunicator {
    private static String serverHost;
    private static int serverPort;
    private List<TCPListener> allListeners = new ArrayList<>();
    private BufferedWriter out;
    private DataInputStream in; //BufferedReader
    private Socket s;
    private Handler UIHandler;
    private boolean mRun = false;
    private InitTCPClientTask task = null;

    private static TcpCommunicator sInstance;

    public static @NonNull TcpCommunicator getInstance(final @NonNull Context context) {
        TcpCommunicator instance = sInstance;
        if (instance == null) {
            synchronized (TcpCommunicator.class) {
                instance = sInstance;
                if (instance == null) {
                    instance = sInstance = new TcpCommunicator(context);
                }
            }
        }
        return instance;
    }

    public static @Nullable TcpCommunicator getInstanceIfExists() {
        return sInstance;
    }

    private TcpCommunicator(final @NonNull Context context) { }

    public void init(String host,int port) {
        setServerHost(host);
        setServerPort(port);
        task = new InitTCPClientTask();
        task.execute();
    }

    public void writeToSocket(final String outMsg, Handler handle) {
        UIHandler=handle;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    out.write(outMsg);
                    out.flush(); 
                    Log.i("TcpClient", "sent: " + outMsg);
                } catch(Exception e) {
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
    }
            
    public void addListener(final @NonNull TCPListener listener) {
        allListeners.clear();
        allListeners.add(listener);
    }

    public void removeAllListeners() {
        allListeners.clear();
    }

    public void closeStreams() {
        try {
            mRun = false;
            s.close();
            in.close();
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static void setServerHost(String serverHost) {
        TcpCommunicator.serverHost = serverHost;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public  void setServerPort(int serverPort) {
        TcpCommunicator.serverPort = serverPort;
    }

    public void cancelTask() {
        if (task == null) {
            return;
        }

        Log.d("TcpClient", "cancel result: " + task.cancel(true));
    }
      
    public class InitTCPClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onCancelled() {
          super.onCancelled();        
          Log.d("TcpClient", "TcpClient Cancel");     
        }
                
        @Override
        protected Void doInBackground(Void... params) {
            mRun = true;
            try {
                 s = new Socket(getServerHost(), getServerPort());
                 //in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                 // Конвертируем потоки в другой тип, чтоб легче обрабатывать текстовые сообщения.
                 in = new DataInputStream(s.getInputStream());
                 
                 for(TCPListener listener:allListeners)
                        listener.onTCPConnectionStatusChanged(true);
                 
                while(mRun) {
                        // count the available bytes form the input stream
                        int count = in.available();
                        
                        if (count>0){
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
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Log.d("TcpClient", "TcpClient Cancel!");
            return null;            
        }        
    }

}
