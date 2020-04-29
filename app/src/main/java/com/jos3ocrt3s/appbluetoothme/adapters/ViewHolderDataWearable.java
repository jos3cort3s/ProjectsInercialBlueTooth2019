package com.jos3ocrt3s.appbluetoothme.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jos3ocrt3s.appbluetoothme.R;
import com.jos3ocrt3s.appbluetoothme.model.ModelRegistroUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ViewHolderDataWearable extends RecyclerView.ViewHolder {

    private TextView tvId, tvUser, tvDate;
    private ImageButton btnGraphis, btnExport;

    public ViewHolderDataWearable(View itemView) {
        super(itemView);
        tvId = itemView.findViewById(R.id.tv_id_adapter);
        tvUser = itemView.findViewById(R.id.tv_tittle_adapter);
        tvDate = itemView.findViewById(R.id.tv_date_adapter);
        btnGraphis = itemView.findViewById(R.id.btn_graphics_adapter);
        btnExport = itemView.findViewById(R.id.btn_export_adapter);



    }

    public void bindOnClick(final ModelRegistroUser registroUser, final AdapterDataWearable.onEventsClickRecyclerView onEventsClickRecyclerView){
        tvId.setText(String.valueOf(registroUser.getIdUser()));
        tvUser.setText(registroUser.getUser());
        DateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aaa");
        tvDate.setText(String.valueOf(myFormat.format(registroUser.getDateHours())));

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEventsClickRecyclerView.onClickListener(registroUser, getAdapterPosition());

            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                onEventsClickRecyclerView.onClickLongListener(registroUser, getAdapterPosition());
                return false;
            }
        });

        btnGraphis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEventsClickRecyclerView.onClickButtonOneGraphics(registroUser, getAdapterPosition() );

            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEventsClickRecyclerView.onClickButtonTwoExport(registroUser, getAdapterPosition());

            }
        });



    }
}
