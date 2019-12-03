package com.example.budgetlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListViewHolder> {

    private ArrayList<MyListItem> myListItems;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onSetItemQuantity(int position, int amount);
        void onDeleteItem(int position);
    }

    public void OnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public MyListAdapter(ArrayList<MyListItem> items){
        myListItems = items;
    }

    @NonNull
    @Override
    public MyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        MyListViewHolder vh = new MyListViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyListViewHolder holder, int position) {
        MyListItem currentItem = myListItems.get(position);

        holder.itemName.setText(currentItem.getItemName());
        holder.itemQuantity.setText(String.valueOf(currentItem.getQuantity()));
    }

    @Override
    public int getItemCount() {
        if(myListItems != null)
            return myListItems.size();
        return 0;
    }

    public static class MyListViewHolder extends RecyclerView.ViewHolder{
        public TextView itemName;
        public TextView itemQuantity;
        public ImageButton deleteButton;
        public ImageButton addButton;
        public ImageButton minusButton;

        public MyListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemName = itemView.findViewById(R.id.card_item_name);
            itemQuantity = itemView.findViewById(R.id.card_item_quantity);
            deleteButton = itemView.findViewById(R.id.card_item_delete);
            addButton = itemView.findViewById(R.id.card_item_add);
            minusButton = itemView.findViewById(R.id.card_item_minus);

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onSetItemQuantity(position, 1);
                        }
                    }
                }
            });

            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onSetItemQuantity(position, -1);
                        }
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteItem(position);
                        }
                    }
                }
            });
        }
    }
}
