package com.jos3ocrt3s.appbluetoothme.model;

public class ModelBLE
{

    private String nameDevice;
    private String stateDevice;


    public ModelBLE(String nameDevice, String stateDevice) {
        this.nameDevice = nameDevice;
        this.stateDevice = stateDevice;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public String getStateDevice() {
        return stateDevice;
    }

    public void setStateDevice(String stateDevice) {
        this.stateDevice = stateDevice;
    }
}
