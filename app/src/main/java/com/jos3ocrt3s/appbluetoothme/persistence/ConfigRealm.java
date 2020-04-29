package com.jos3ocrt3s.appbluetoothme.persistence;


import android.app.Application;

import com.jos3ocrt3s.appbluetoothme.model.ModelDataWearable;
import com.jos3ocrt3s.appbluetoothme.model.ModelRegistroUser;

import java.util.concurrent.atomic.AtomicLong;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class ConfigRealm  extends Application {

    public static AtomicLong dataIdUser =  new AtomicLong();
    public static AtomicLong dataIdData =  new AtomicLong();

    @Override
    public void onCreate() {
        super.onCreate();
        setUpRealmConfig();
        Realm realmConfig = Realm.getDefaultInstance();
        dataIdUser = getIdByTable(realmConfig, ModelRegistroUser.class);
        dataIdData = getIdByTable(realmConfig, ModelDataWearable.class);
        realmConfig.close();
    }

    private void setUpRealmConfig(){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicLong getIdByTable (Realm realm, Class<T> anyClass){
        RealmResults<T> results  =  realm.where(anyClass).findAll();
        return  (results.size()> 0 )? new AtomicLong(results.max("id").longValue()): new AtomicLong();
    }


}
