package com.example.mqtt_smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG  = "xxx";
    MqttAndroidClient client = null;
    DeviceAdapter deviceAdapter;
    RecyclerView recyclerView ;
    List<Device> listDevice = new ArrayList<>();
    EditText etTopic,etNumber;
    Button btTest;
    FloatingActionButton fabAddDevice;
    Dialog dialog;
    public static String topic;
    private Integer numberDevice;
    SharedPreferences sharedPref;
    public static SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref =this.getSharedPreferences("MQTTAPP", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        etTopic =findViewById(R.id.etTopic);
        etNumber =findViewById(R.id.etNumber);
        btTest =findViewById(R.id.btTest);
        fabAddDevice =findViewById(R.id.fabAddDevice);
        btTest.setOnClickListener(this);
        fabAddDevice.setOnClickListener(this);

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_device);

        String clientId = MqttClient.generateClientId();
        client= new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess");
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        getConfig();


        recyclerView =findViewById(R.id.recyclerView);
        GridLayoutManager manager=new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(manager);
        deviceAdapter = new DeviceAdapter(listDevice,client);
        recyclerView.setAdapter(deviceAdapter);
    }

    private void saveConfig() {
        editor.putString("topic", etTopic.getText().toString());
//        editor.putInt("number", Integer.parseInt(etNumber.getText().toString()));
        editor.commit();
        getConfig();
    }

    private void getConfig() {
        topic = sharedPref.getString("topic", "");
        numberDevice = sharedPref.getInt("number", 0);
        if(numberDevice>0){
            for (int i = 1  ; i <= numberDevice ; i++) {
                listDevice.add(new Device(sharedPref.getString("device"+i,""),sharedPref.getBoolean("deviceStatus"+i,false)));
            }
        }
        etTopic.setText(topic);
        etNumber.setText(numberDevice+"");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btTest:
                saveConfig();
                break;
            case R.id.fabAddDevice:
                Log.d(TAG, "onClick: fabAddDevice");
                openDialog();
                break;
        }
    }

    private void openDialog() {
        EditText etNameDevice = dialog.findViewById(R.id.etNameDevice);
        Button btSaveDevice =dialog.findViewById(R.id.btSaveDevice);
        int index =listDevice.size()+1;
        etNameDevice.setText("Thiết bị "+index);
        dialog.show();
        btSaveDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameDevice = etNameDevice.getText().toString();
                listDevice.add(new Device(nameDevice,false));
                Log.d(TAG, "onClick: Add new device: "+listDevice.size());
                editor.putString("device"+listDevice.size(), nameDevice);
                editor.putInt("number",listDevice.size());
                etNumber.setText(listDevice.size()+"");
                editor.commit();
                deviceAdapter.notifyDataSetChanged();
                int index =listDevice.size()+1;
                etNameDevice.setText("Thiết bị "+index);
            }
        });
    }
}