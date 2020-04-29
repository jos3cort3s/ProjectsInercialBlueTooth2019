package com.jos3ocrt3s.appbluetoothme.model;

import com.jos3ocrt3s.appbluetoothme.persistence.ConfigRealm;

import org.jetbrains.annotations.NotNull;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ModelDataWearable extends RealmObject {

    @PrimaryKey
    private long id;

    private double time;
    private String dataAx, dataAy, dataAz;


    public ModelDataWearable() {
    }

    public ModelDataWearable(double time, String dataAx, String dataAy, String dataAz) {
        this.id = ConfigRealm.dataIdData.incrementAndGet();
        this.time = time;
        this.dataAx = dataAx;
        this.dataAy = dataAy;
        this.dataAz = dataAz;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public String getDataAx() {
        return dataAx;
    }

    public void setDataAx(String dataAx) {
        this.dataAx = dataAx;
    }

    public String getDataAy() {
        return dataAy;
    }

    public void setDataAy(String dataAy) {
        this.dataAy = dataAy;
    }

    public String getDataAz() {
        return dataAz;
    }

    public void setDataAz(String dataAz) {
        this.dataAz = dataAz;
    }
}
