package com.jos3ocrt3s.appbluetoothme.activities;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jos3ocrt3s.appbluetoothme.R;
import com.jos3ocrt3s.appbluetoothme.adapters.AdapterDataWearable;
import com.jos3ocrt3s.appbluetoothme.model.ModelDataWearable;
import com.jos3ocrt3s.appbluetoothme.model.ModelRegistroUser;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;


public class HistoryListActivity extends AppCompatActivity {

    //Elements UI variables
    private RecyclerView listHistory;

    //Adapter for  Recycler view
    private AdapterDataWearable mAdapterRecycler;
    private RecyclerView.LayoutManager mLayoutManager;

    //Realm

    private Realm mRealm;
    private ModelDataWearable mModelDataWearable;
    private ModelRegistroUser mModelRegistroUser;
    private RealmResults<ModelRegistroUser> mRealmResultsUser;
    private RealmResults<ModelDataWearable>mRealmResultData;


    //Alert dialog variables
    private ImageButton ibtn_cancel;
    private TextView tv_showData;
    private GraphView gView_showData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_history_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        callId();
        mRealm =  Realm.getDefaultInstance();
        mRealmResultsUser = readAllUser();

        mRealmResultsUser.addChangeListener(new RealmChangeListener<RealmResults<ModelRegistroUser>>() {
            @Override
            public void onChange(RealmResults<ModelRegistroUser> modelRegistroUsers) {
              mAdapterRecycler.notifyDataSetChanged();
            }
        });


        mAdapterRecycler = new AdapterDataWearable(mRealmResultsUser, R.layout.layout_custon_recycler_user, new AdapterDataWearable.onEventsClickRecyclerView() {
            @Override
            public void onClickListener(ModelRegistroUser model, int position) {

            }

            @Override
            public void onClickLongListener(ModelRegistroUser model, int position)
            {
                    deleteOnlyUser(model);
                    Toast.makeText(HistoryListActivity.this, "Delete successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onClickButtonOneGraphics(ModelRegistroUser model, int position) {

                showBoxAlertDialog(position);
            }

            @Override
            public void onClickButtonTwoExport(ModelRegistroUser model, int position) {

                writeCSVFile(model.getUser(), position);

            }
        });
        mLayoutManager=  new LinearLayoutManager(this);
        listHistory.setAdapter(mAdapterRecycler);
        listHistory.setLayoutManager(mLayoutManager);
        listHistory.setItemAnimator(new DefaultItemAnimator());
    }

    private void callId () {
        listHistory = findViewById(R.id.rvListHistory); }

    private void  showBoxAlertDialog(int position){

        final Dialog box_DataDialog =  new Dialog(this);
        box_DataDialog.setContentView(R.layout.layout_card_box_show_data);
        gView_showData = box_DataDialog.findViewById(R.id.graph_boxData);
        tv_showData = box_DataDialog.findViewById(R.id.tv_BoxData);
        ibtn_cancel = box_DataDialog.findViewById(R.id.ibtn_BoxCancel);

        DataPoint [] mDataPointX = new DataPoint[readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).size()];
        DataPoint [] mDataPointY = new DataPoint[readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).size()];
        DataPoint [] mDataPointZ = new DataPoint[readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).size()];


        for(int i = 0; i<readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).size(); i++){

        tv_showData.setText(tv_showData.getText() + readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).get(i).getDataAx()+" / "+
                readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).get(i).getDataAy()+" / "+
                readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).get(i).getDataAz()+"\n");

        mDataPointX[i] =  new DataPoint(i, Double.parseDouble(readAllDataWearableUser(mRealmResultsUser.get(position)
                            .getIdUser()).get(i).getDataAx()));
        mDataPointY[i] =  new DataPoint(i, Double.parseDouble(readAllDataWearableUser(mRealmResultsUser.get(position)
                            .getIdUser()).get(i).getDataAy()));
        mDataPointZ[i] =  new DataPoint(i, Double.parseDouble(readAllDataWearableUser(mRealmResultsUser.get(position)
                            .getIdUser()).get(i).getDataAz()));




        }
        ibtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box_DataDialog.cancel();

            }
        });


        //***************************Graphs**********************************

        LineGraphSeries<DataPoint> seriesX = new LineGraphSeries<>(mDataPointX);
        LineGraphSeries<DataPoint> seriesY = new LineGraphSeries<>(mDataPointY);
        LineGraphSeries<DataPoint> seriesZ = new LineGraphSeries<>(mDataPointZ);

        seriesX.setTitle("X");
        seriesX.setColor(Color.GREEN);
        seriesY.setTitle("Y");
        seriesY.setColor(Color.RED);
        seriesZ.setTitle("Z");
        seriesZ.setColor(Color.BLUE);

        gView_showData.getViewport().setYAxisBoundsManual(true);
        gView_showData.getViewport().setMinY(-1000);
        gView_showData.getViewport().setMaxY(1000);

        gView_showData.getViewport().setXAxisBoundsManual(true);
        //gView_showData.getViewport().setMinX(10000);
        gView_showData.getViewport().setMaxX(700);
        gView_showData.getViewport().setScalable(true);
        gView_showData.getViewport().setScalableY(true);
        gView_showData.addSeries(seriesX);
        gView_showData.addSeries(seriesY);
        gView_showData.addSeries(seriesZ);

        //*******************************************************************


        box_DataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        box_DataDialog.show();

    }


    // WRITE and SAVE file CSV on SD

    private void writeCSVFile(String nameFile, int position){

        List<String[]> mdata = new ArrayList<String[]>();
        mdata.add(new String[]{"   # Interaction   ", "   Axis X   ", "   Axis Y   ", "   Axis Z   "});

        for (int i=0; i<readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).size(); i++){
            mdata.add(new String[]{"   "+String.valueOf(i)+"   ",
                    "     "+String.valueOf(readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).get(i).getDataAx())+"     ",
                    "     "+String.valueOf(readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).get(i).getDataAy())+"    ",
                    "     "+String.valueOf(readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).get(i).getDataAz())+"     "});
        }


        //Double mListExport = readAllDataWearableUser(mRealmResultsUser.get(position).getIdUser()).get(0).getData();


       File mDirApp = new File(Environment.getExternalStorageDirectory()+"/ScanBlueApp");
        if(!mDirApp.exists()){
            mDirApp.mkdirs(); }

        String csv = (mDirApp+"/"+nameFile+".csv"); // Here csv file name is MyCsvFile.csv

                CSVWriter writer = null;
                try {
                    writer = new CSVWriter(new FileWriter(csv));

                   /* List<String[]> data = new ArrayList<String[]>();
                    data.add(new String[]{"# Interaction", "Value"});
                    data.add(new String[]{"India", "New Delhi"});
                    data.add(new String[]{"United States", "Washington D.C"});
                    data.add(new String[]{"Germany", "Berlin"});*/

                    writer.writeAll(mdata); // data is adding to csv
                    writer.close();
                    Toast.makeText(this, "Save file succesful", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Create in:\n C:/ScanBlueApp/..", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error, could not save", Toast.LENGTH_SHORT).show();
                }
    }


    //CRUD date base Realm

    private RealmResults<ModelRegistroUser> readAllUser(){
        return  mRealm.where(ModelRegistroUser.class).findAll();
    }

    private RealmList<ModelDataWearable> readAllDataWearableUser(long userId){
        if(userId>=0) {
             mModelRegistroUser = mRealm.where(ModelRegistroUser.class).equalTo("id", userId).findFirst();
            return mModelRegistroUser.getDataWearable();
        }else{
            return null;
        }
    }

    private void deleteOnlyUser(ModelRegistroUser user){
        mRealm.beginTransaction();
        user.deleteFromRealm();
        mRealm.commitTransaction();
    }

}




