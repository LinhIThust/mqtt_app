package com.example.mqtt_smarthome;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG  = "xxx";
    MqttAndroidClient client = null;
    DeviceAdapter deviceAdapter;
    RecyclerView recyclerView ;
    List<Device> listDevice = new ArrayList<>();
    EditText etTopic,etNumber;
    Button btTest;
    public static String topic;
    private Integer numberDevice;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref =this.getSharedPreferences("MQTTAPP", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        etTopic =findViewById(R.id.etTopic);
        etNumber =findViewById(R.id.etNumber);
        btTest =findViewById(R.id.btTest);
        btTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveConfig();
            }
        });
        recyclerView =findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
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
        listDevice.add(0,new Device("Helo",true));
        listDevice.add(1,new Device("Helo",true));
        listDevice.add(2,new Device("Helo",true));
        deviceAdapter = new DeviceAdapter(listDevice,client);
        recyclerView.setAdapter(deviceAdapter);
    }

    private void saveConfig() {
        editor.putString("topic", etTopic.getText().toString());
        editor.putInt("number", Integer.parseInt(etNumber.getText().toString()));
        editor.commit();
    }

    private void getConfig() {
        topic = sharedPref.getString("topic", "");
        numberDevice = sharedPref.getInt("number", 0);
        etTopic.setText(topic);
        etNumber.setText(numberDevice+"");
    }
}