package com.example.budgetlist;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {

    private ArrayList<ShopperStop> allStops;
    private ArrayList<String> allItems;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void OpenMap(int position);
    }

    public void OnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public StopAdapter(ArrayList<ShopperStop> stops){
        allStops = stops;
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopper_stop, parent, false);
        StopViewHolder vh = new StopViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        ShopperStop currentStop = allStops.get(position);

        holder.stopName.setText(currentStop.getStoreName());
        holder.totalCost.setText("Total Cost: $" + String.valueOf(currentStop.getTotalCost()));
    }

    @Override
    public int getItemCount() {
        if(allStops != null)
            return allStops.size();
        return 0;
    }

    public static class StopViewHolder extends RecyclerView.ViewHolder{
        public TextView stopName;
        public TextView totalCost;
        public ImageButton mapButton;

        public StopViewHolder(@NonNull View stopView, final OnItemClickListener listener) {
            super(stopView);
            stopName = stopView.findViewById(R.id.stop_storename);
            totalCost = stopView.findViewById(R.id.stop_totalcost);
            mapButton = stopView.findViewById(R.id.stop_mapbutton);

            if (mapButton != null) {
                mapButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.OpenMap(position);
                            }
                        }
                    }
                });
            }
        }
    }
}
