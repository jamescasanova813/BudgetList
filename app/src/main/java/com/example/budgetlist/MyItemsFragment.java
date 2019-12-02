package com.example.budgetlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MyItemsFragment extends Fragment {

    private String[] allItems;
    private ArrayList<MyListItem> allListItems = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private MyListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myitems, container, false);

        GetItemsLocalStorage();

        buildRecyclerView(view);

        return view;
    }

    public void buildRecyclerView(View root){
        mRecyclerView = root.findViewById(R.id.myitems_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        for(String s : allItems){
            allListItems.add(new MyListItem(s));
        }
        mAdapter = new MyListAdapter(allListItems);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.OnItemClickListener(new MyListAdapter.OnItemClickListener() {
            @Override
            public void onSetItemQuantity(int position, int amount) {
                if(allListItems.get(position).getQuantity() + amount >= 0){
                    UpdateData(position, amount);
                }
            }

            @Override
            public void onDeleteItem(int position) {
                RemoveItem(position);
            }
        });
    }

    private void UpdateData(int position, int amount){
        allListItems.get(position).setQuantity(amount);
        mAdapter.notifyItemChanged(position);
    }

    private void RemoveItem(int position){
        allListItems.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private void GetItemsLocalStorage(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ShoppingItems", "");
        allItems = gson.fromJson(json, String[].class);
    }

    private void UpdateItemsLocalStorage(){

    }
}
