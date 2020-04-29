package com.jos3ocrt3s.appbluetoothme.utils;

import java.util.UUID;

public class Constants {

    public static String SERVICE_STRING = "795090c7-420d-4048-a24e-18e60180e23c";
    public static UUID SERVICE_UUID = UUID.fromString("795090c7-420d-4048-a24e-18e60180e23c");

    public static String CHARACTERISTIC_COUNTER_STRING = "31517c58-66bf-470c-b662-e352a6c80cba";
    public static UUID CHARACTERISTIC_COUNTER_UUID = UUID.fromString("31517c58-66bf-470c-b662-e352a6c80cba");


    public static UUID CHARACTERISTIC_INTERACTOR_UUID = UUID.fromString("0b89d2d4-0ea6-4141-86bb-0c5fb91ab14a");
    public static UUID DESCRIPTOR_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public static String CHARACTERISTIC_ECHO_STRING = "7D2EBAAD-F7BD-485A-BD9D-92AD6ECFE93E";
    public static UUID CHARACTERISTIC_ECHO_UUID = UUID.fromString(CHARACTERISTIC_ECHO_STRING);

    public static final long SCAN_PERIOD = 5000;

}
