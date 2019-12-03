package com.example.budgetlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MyItemsFragment extends Fragment {

    private ArrayList<MyListItem> allListItems = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private MyListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button calculateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myitems, container, false);

        SetupUI(view);

        GetItemsLocalStorage();

        buildRecyclerView(view);

        return view;
    }

    private void SetupUI(View v){
        calculateButton = v.findViewById(R.id.myitems_calculatebutton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeToDirectionsFragment();
            }
        });
    }

    private void ChangeToDirectionsFragment(){
        UpdateItemsLocalStorage();

        //NEED TO CHANGE FRAGMENT
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new CalculationFragment()).commit();
    }

    public void buildRecyclerView(View root){
        mRecyclerView = root.findViewById(R.id.myitems_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

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
        UpdateItemsLocalStorage();
    }

    private void RemoveItem(int position){
        allListItems.remove(position);
        mAdapter.notifyItemRemoved(position);
        UpdateItemsLocalStorage();
    }

    private void GetItemsLocalStorage(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ShoppingItems", "");

        Type type = new TypeToken<ArrayList<MyListItem>>(){}.getType();
        allListItems = gson.fromJson(json, type);
    }

    private void UpdateItemsLocalStorage(){
        Gson gson = new Gson();
        String json = gson.toJson(allListItems);
        SharedPreferences sharedPreferences = this.getActivity().getPreferences((Context.MODE_PRIVATE));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ShoppingItems", json);
        editor.apply();
    }
}
