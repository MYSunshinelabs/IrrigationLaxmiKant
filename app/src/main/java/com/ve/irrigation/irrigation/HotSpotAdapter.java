package com.ve.irrigation.irrigation;

import android.app.Activity;
import android.net.Network;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ve.irrigation.DataValues.ConnectionSourceData;
import com.ve.irrigation.DataValues.WifiHotspot;
import com.ve.irrigation.customview.CustomTextView;
import com.ve.irrigation.hotspot.ClientScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HotSpotAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public ArrayList<WifiHotspot> networks = new ArrayList<>();
    HotspotListener mHotspotListener;
    List<ConnectionSourceData> connectionSourceDatas;

    public HotSpotAdapter(Activity activity, List<ConnectionSourceData> connectionSourceDatas) {
        mHotspotListener = (HotspotListener) activity;
        this.connectionSourceDatas = connectionSourceDatas;
        addNetworks(connectionSourceDatas);


    }

    private void addNetworks(List<ConnectionSourceData> connectionSourceDatas) {

        for (int i = 0; i < 5; i++) {
            WifiHotspot wifiHotspot = new WifiHotspot();
            wifiHotspot.setSsid("sun0" + i);
            wifiHotspot.setPassword("sun01234");
            wifiHotspot.setType("hotspot");
            networks.add(wifiHotspot);
        }

        for (ConnectionSourceData connectionSourceData : connectionSourceDatas) {
            WifiHotspot wifiHotspot = new WifiHotspot();
            if (connectionSourceData.getSSID().length() > 0)
                wifiHotspot.setSsid(connectionSourceData.getSSID());
            else
                wifiHotspot.setSsid(connectionSourceData.getUrl());

            wifiHotspot.setPassword(connectionSourceData.getPassword());
            wifiHotspot.setType(connectionSourceData.getType());
            networks.add(wifiHotspot);
        }


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifihotspot, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final DataObjectHolder dataObjectHolder = (DataObjectHolder) holder;

        dataObjectHolder.mTextViewName.append(networks.get(position).getSsid());

        dataObjectHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHotspotListener.setUpHotSpot(networks.get(position));
            }
        });


    }


    @Override
    public int getItemCount() {
        return (networks.size());
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewName;
        private LinearLayout mLinearLayout;

        public DataObjectHolder(View itemView) {
            super(itemView);

            mTextViewName = (CustomTextView) itemView.findViewById(R.id.textview_hotspotname);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.layout_hotspot);

        }

    }

    public interface HotspotListener {
        public void setUpHotSpot(WifiHotspot wifiHotspot);
    }


}