package com.example.mqtt_smarthome;

import static com.example.mqtt_smarthome.MainActivity.editor;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceStatusHolder> {
    List<Device> deviceList;
    MqttAndroidClient client;
    public DeviceAdapter(List<Device> deviceList,MqttAndroidClient client) {
        this.deviceList = deviceList;
        this.client =client;
    }
    @NonNull
    @Override
    public DeviceStatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.button_item,parent,false);
        return new DeviceStatusHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DeviceStatusHolder holder, int position) {
        holder.setData(deviceList.get(position),position);
    }
    @Override
    public int getItemCount() {
        return deviceList.size();
    }
    public class DeviceStatusHolder extends RecyclerView.ViewHolder{
        public TextView tvDevice;
        public ImageView ivStatus;

        public DeviceStatusHolder(View itemView) {
            super(itemView);
            tvDevice = itemView.findViewById(R.id.tvDeviceName);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }

        public void setData(final Device data,int position) {
            tvDevice.setText(data.getNameDevice());
            chooseImage(data.getStatusDevice());
            ivStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImage(!data.getStatusDevice());
                    data.setStatusDevice(!data.getStatusDevice());
                    int index =position+1;
                    editor.putBoolean("deviceStatus"+index,data.getStatusDevice());
                    editor.commit();
                    pushStatus(data,position);
                }
            });
        }

        private void pushStatus(Device data, int position) {
            String payload = Integer.toString(position)+","+data.getStatusDevice();
            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(MainActivity.topic+position, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("NewApi")
        private void chooseImage(boolean choose) {
            if (choose == true) {
                Picasso.get().load(R.drawable.turnon).into(ivStatus);
            } else {
                Picasso.get().load(R.drawable.turnoff).into(ivStatus);
            }
        }
    }
}

