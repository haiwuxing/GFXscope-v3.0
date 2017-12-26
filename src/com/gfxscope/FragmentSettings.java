package com.gfxscope;

import com.TCP_Client.TcpCommunicator;
import com.models.TGraficType;
import com.utils.Utils;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class FragmentSettings extends PreferenceFragment { //TODO extends PreferenceFragmentCompat
    public static final @NonNull String TAG = Utils.getTag(FragmentSettings.class);

    //private static final String PREF_IPadress = "IPadress";
    //private static final String PREF_MINMAX = "MINMAX";
    //SharedPreferences settings;
    //SharedPreferences.Editor prefEditor;
    Button set_ip = null;
    Button back_to_main = null;
    Button set_null = null;
    Button type_minmax = null;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
         menu.getItem(0).setEnabled(false);
         menu.getItem(1).setEnabled(false);
         menu.getItem(2).setEnabled(false);
         menu.getItem(3).setEnabled(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause(){
        super.onPause();
        //EditText nameBox = (EditText) getActivity().findViewById(R.id.text_input_ip_adress);

        // сохраняем в настройках
        //FragmentMain.prefEditor =  FragmentMain.settings.edit();
        //FragmentMain.prefEditor.putString(FragmentMain.PREF_IPadress, nameBox.getText().toString());
        //FragmentMain.prefEditor.putInt(FragmentMain.PREF_MINMAX, FragmentMain.get_grafic_type());
        //FragmentMain.prefEditor.apply();
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Context context = this.getContext();

        EditText u1 = (EditText) getActivity().findViewById(R.id.edit_text_mnojitel_ch1);
        EditText u2 = (EditText) getActivity().findViewById(R.id.edit_text_mnojitel_ch2);


        int temp=(int)FragmentMain.CH1_correct;
        String val1 = String.valueOf((int)(FragmentMain.CH1_correct));
        int val2=(int)(100*FragmentMain.CH1_correct-(100*temp));
        String upp;
        upp=(val1+"."+String.valueOf(val2));
        u1.setText(upp);

        temp=(int)FragmentMain.CH2_correct;
        val1 = String.valueOf((int)(FragmentMain.CH2_correct));
        val2=(int)(100*FragmentMain.CH2_correct-(100*temp));
        upp=(val1+"."+String.valueOf(val2));
        u2.setText(upp);

        type_minmax = (Button) getActivity().findViewById(R.id.type_minmax);
        type_minmax.setText(FragmentMain.get_grafic_type().getName(context));
        type_minmax.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final TGraficType currentTGraficType = FragmentMain.get_grafic_type();
                 final int nextTGraficTypeOrdinal = (currentTGraficType.ordinal()+1) % (TGraficType.values().length-1);
                 final TGraficType nextTGraficType = TGraficType.values()[nextTGraficTypeOrdinal];
                 FragmentMain.set_grafic_type(nextTGraficType);
                 type_minmax.setText(nextTGraficType.getName(context));

                 // сохраняем в настройках TODO
                 FragmentMain.prefEditor = FragmentMain.settings2.edit();
                 nextTGraficType.writeToPrefs(FragmentMain.prefEditor);
                 FragmentMain.prefEditor.apply();
             }
         });

        set_ip = (Button) getView().findViewById(R.id.set_ip_adress);
        set_ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isDeadFragment(FragmentSettings.this)) {
                    return;
                }

                EditText nameBox = (EditText) getActivity().findViewById(R.id.text_input_ip_adress);
                String name = nameBox.getText().toString();

                // сохраняем в настройках
                FragmentMain.prefEditor = FragmentMain.settings.edit();
                FragmentMain.prefEditor.putString(FragmentMain.PREF_IPadress, name);
                FragmentMain.prefEditor.apply();

                FragmentMain.tcpClient = TcpCommunicator.getInstanceIfExists();
                FragmentMain.tcpClient.init(name, 12345);
             }
         });

        set_null = (Button) getView().findViewById(R.id.set_null);
        set_null.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Set_null 1");

             }
         });


        Button genertor_type_off = (Button) getView().findViewById(R.id.genertor_type_off);
        genertor_type_off.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Type 0");

             }
         });

        Button genertor_type_Sinus = (Button) getView().findViewById(R.id.genertor_type_Sinus);
        genertor_type_Sinus.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Type 1");

             }
         });

        Button genertor_type_Meander = (Button) getView().findViewById(R.id.genertor_type_Meander);
        genertor_type_Meander.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Type 2");

             }
         });

        Button genertor_type_Triagle = (Button) getView().findViewById(R.id.genertor_type_Triagle);
        genertor_type_Triagle.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Type 3");

             }
         });

        Button genertor_type_Saw = (Button) getView().findViewById(R.id.genertor_type_Saw);
        genertor_type_Saw.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Type 4");

             }
         });

        Button genertor_type_Digital = (Button) getView().findViewById(R.id.genertor_type_Digital);
        genertor_type_Digital.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Type 5");

             }
         });


        Button genertor_freq_100 = (Button) getView().findViewById(R.id.genertor_freq_100);
        genertor_freq_100.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Freq 0");

             }
         });

        Button genertor_freq_1k = (Button) getView().findViewById(R.id.genertor_freq_1k);
        genertor_freq_1k.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Freq 1");

             }
         });


        Button genertor_freq_10k = (Button) getView().findViewById(R.id.genertor_freq_10k);
        genertor_freq_10k.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 FragmentMain.osc_set_param("Generator_Freq 2");

             }
         });




        Button genertor_set_Upp = (Button) getView().findViewById(R.id.genertor_set_Upp);
        genertor_set_Upp.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 EditText upp = (EditText) getActivity().findViewById(R.id.text_input_Upp);
                    String val = upp.getText().toString();
                    double temp_upp=0;
                    try{
                      temp_upp= Double.parseDouble(val);
                    }
                    catch (Exception e) {
                    }
                    if (temp_upp>2.0)temp_upp=2;
                    if (temp_upp<0)temp_upp=0;

                 FragmentMain.osc_set_param("Generator_Upp "+String.format("%.0f", (temp_upp*10)));

             }
         });


        final SeekBar seekBar_genertor_Upp = (SeekBar)getView().findViewById(R.id.seekBar_genertor_Upp);

        SeekBar.OnSeekBarChangeListener seekBarChangeListener_Upp = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 EditText upp = (EditText) getActivity().findViewById(R.id.text_input_Upp);
                 float val_upp=(float)(200*seekBar_genertor_Upp.getProgress()/255);
                 int temp=(int)val_upp/100;
                 String val1 = String.valueOf((int)(val_upp/100));
                 int val2=(int)(val_upp-(100*temp));

                 if (val2>9.9){
                      upp.setText(
                         val1
                         +"."
                         +String.valueOf(val2)
                         );
                 }
                 else {
                 upp.setText(
                         val1
                         +".0"
                         +String.valueOf(val2)
                         );
                 }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 EditText upp = (EditText) getActivity().findViewById(R.id.text_input_Upp);
                    String val = upp.getText().toString();
                    double temp_upp=0;
                    try{
                      temp_upp= Double.parseDouble(val);
                    }
                    catch (Exception e) {
                    }
                    if (temp_upp>2.0)temp_upp=2;
                    if (temp_upp<0)temp_upp=0;

                 FragmentMain.osc_set_param("Generator_Upp "+String.format("%.0f", (temp_upp*10)));

            }
            };
            seekBar_genertor_Upp.setOnSeekBarChangeListener(seekBarChangeListener_Upp);



            Button set_mnojitel_ch1 = (Button) getView().findViewById(R.id.set_mnojitel_ch1);
            set_mnojitel_ch1.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     EditText u = (EditText) getActivity().findViewById(R.id.edit_text_mnojitel_ch1);
                        String val = u.getText().toString();
                        double temp_u=0;
                        try{
                          temp_u= Double.parseDouble(val);
                        }
                        catch (Exception e) {
                        }
                        if (temp_u>1.5)temp_u=1;
                        if (temp_u<0.5)temp_u=1;

                         int temp=(int)temp_u;
                         String val1 = String.valueOf((int)(temp_u));
                         int val2=(int)(100*temp_u-(100*temp));
                         String upp;
                         upp=(val1+"."+String.valueOf(val2));

                         FragmentMain.osc_set_param("CH1_correct "+upp);

                 }
             });

            Button set_mnojitel_ch2 = (Button) getView().findViewById(R.id.set_mnojitel_ch2);
            set_mnojitel_ch2.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     EditText u = (EditText) getActivity().findViewById(R.id.edit_text_mnojitel_ch2);
                        String val = u.getText().toString();
                        double temp_u=0;
                        try{
                          temp_u= Double.parseDouble(val);
                        }
                        catch (Exception e) {
                        }
                        if (temp_u>1.5)temp_u=1;
                        if (temp_u<0.5)temp_u=1;

                         int temp=(int)temp_u;
                         String val1 = String.valueOf((int)(temp_u));
                         int val2=(int)(100*temp_u-(100*temp));
                         String upp;
                         upp=(val1+"."+String.valueOf(val2));
                         FragmentMain.osc_set_param("CH2_correct "+upp);
                 }
             });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, null);

        FragmentMain.settings = getActivity().getSharedPreferences(FragmentMain.PREF_IPadress, Context.MODE_MULTI_PROCESS);
        String name = FragmentMain.settings.getString(FragmentMain.PREF_IPadress,"sgfxscope");        
        EditText nameBox = (EditText) v.findViewById(R.id.text_input_ip_adress);
        nameBox.setText(name);

        //TcpClient.SERVER_IP=name;
        FragmentMain.tcpClient = TcpCommunicator.getInstance();
        FragmentMain.tcpClient.init(name, 12345); 
        
        //type_minmax = (Button) getActivity().findViewById(R.id.type_minmax);
        FragmentMain.settings2 = getActivity().getSharedPreferences(FragmentMain.PREF_MINMAX, Context.MODE_MULTI_PROCESS);

        int temp = FragmentMain.settings2.getInt(FragmentMain.PREF_MINMAX, 0);
        if (temp == 1) {
           FragmentMain.set_grafic_type(1);
        } else {
           FragmentMain.set_grafic_type(0);
        }
        
        return v;
    }   
}
