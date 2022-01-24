package com.example.mqtt_smarthome;

public class Device {
    private String nameDevice;
    private Boolean statusDevice;

    public Device() {
    }

    public Device(String nameDevice, Boolean statusDevice) {
        this.nameDevice = nameDevice;
        this.statusDevice = statusDevice;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public Boolean getStatusDevice() {
        return statusDevice;
    }

    public void setStatusDevice(Boolean statusDevice) {
        this.statusDevice = statusDevice;
    }
}
