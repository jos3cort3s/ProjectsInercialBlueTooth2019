package com.jos3ocrt3s.appbluetoothme.model;

import com.jos3ocrt3s.appbluetoothme.persistence.ConfigRealm;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class ModelRegistroUser extends RealmObject{

   @PrimaryKey
    private long id;

   @Required
    private String user;
    private Date dateHours;
    private RealmList<ModelDataWearable> dataWearable;


    public ModelRegistroUser() {
    }


    public ModelRegistroUser(String user, RealmList<ModelDataWearable> dataWearable) {
        this.id = ConfigRealm.dataIdUser.incrementAndGet();
        this.user = user;
        this.dateHours = new Date();
        this.dataWearable = dataWearable;
    }


    public long getIdUser() {
        return id;
    }

    public void setIdUser(long idUser) {
        this.id = idUser;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDateHours() {
        return dateHours;
    }

    public void setDateHours(Date dateHours) {
        this.dateHours = dateHours;
    }

    public RealmList<ModelDataWearable> getDataWearable() {
        return dataWearable;
    }

    public void setDataWearable(RealmList<ModelDataWearable> dataWearable) {
        this.dataWearable = dataWearable;
    }
}
