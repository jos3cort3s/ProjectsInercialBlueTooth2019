package com.jos3ocrt3s.appbluetoothme.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jos3ocrt3s.appbluetoothme.model.ModelRegistroUser;

import java.util.List;

import io.realm.RealmList;

public class AdapterDataWearable extends RecyclerView.Adapter<ViewHolderDataWearable> {

    private List<ModelRegistroUser> listUser;
    private int mLayout;
    private onEventsClickRecyclerView eventsRecyclerView;


    public AdapterDataWearable(List<ModelRegistroUser> listUser, int mLayout, onEventsClickRecyclerView eventsRecyclerView) {
        this.listUser = listUser;
        this.mLayout = mLayout;
        this.eventsRecyclerView = eventsRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolderDataWearable onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view  = LayoutInflater.from(viewGroup.getContext()).inflate(mLayout, viewGroup, false);
        ViewHolderDataWearable mViewHolderDataWearable =  new ViewHolderDataWearable(view);
        return mViewHolderDataWearable;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDataWearable viewHolderDataWearable, int i) {
        viewHolderDataWearable.bindOnClick(listUser.get(i), eventsRecyclerView);
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public interface onEventsClickRecyclerView{

        void onClickListener(ModelRegistroUser model,  int position);
        void onClickLongListener(ModelRegistroUser model,  int position);
        void onClickButtonOneGraphics (ModelRegistroUser model, int position);
        void onClickButtonTwoExport (ModelRegistroUser model, int position);


    }
}
