package com.example.budgetlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class CalculationFragment extends Fragment {

    DatabaseReference database;

    private ArrayList<MyListItem> allItems;
    private ArrayList<ShopperStop> allStops;

    private ArrayList<String> nearbyStores;

    private HashMap<String, Float> storeCosts;
    private HashMap<String, String> locationToName;

    private RecyclerView mRecyclerView;
    private StopAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ListView mListView;
    ArrayAdapter<String> shopItems;

    private int iteration = 0;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calculation, container, false);

        database = FirebaseDatabase.getInstance().getReference();

        GetItemsLocalStorage();

        storeCosts = new HashMap<>();
        locationToName = new HashMap<>();
        nearbyStores = new ArrayList<>();
        GetNearbyStores();

        buildListView();
        buildRecyclerView();

        return view;
    }

    private void buildListView(){
        mListView = view.findViewById(R.id.stop_listview);

        ArrayList<String> listItems = new ArrayList<>();
        for(MyListItem item : allItems){
            listItems.add(item.getItemName() + " - Qty: " + item.getQuantity());
        }

        shopItems = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice, listItems);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(shopItems);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }

    public void buildRecyclerView(){
        mRecyclerView = view.findViewById(R.id.stop_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        allStops = new ArrayList<>();
        allStops.clear();
        mAdapter = new StopAdapter(allStops);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.OnItemClickListener(new StopAdapter.OnItemClickListener() {

            @Override
            public void OpenMap(int position) {
                String coordinates = "";
                for(Map.Entry element : locationToName.entrySet()){
                    if(element.getValue().toString().equals(allStops.get(position).getStoreName())){
                        coordinates = element.getKey().toString();
                    }
                }
                coordinates = coordinates.replaceAll(",", ".");
                coordinates = coordinates.replaceAll("\\|", ",");

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + coordinates);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    private void GetItemsLocalStorage(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ShoppingItems", "");

        Type type = new TypeToken<ArrayList<MyListItem>>(){}.getType();
        allItems = gson.fromJson(json, type);
    }

    private void CalculateBestStops(){
        if(allItems != null){
            for(final MyListItem item : allItems){
                for(final String location : nearbyStores){
                    database.child("StoreLocations").child(location).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            float cost = 0;
                            if(dataSnapshot.hasChild(item.getItemName())){
                                cost = item.getQuantity() * Float.parseFloat(dataSnapshot.child(item.getItemName()).getValue().toString());
                            }

                            AddCosts(location, cost, item);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    private void GetNearbyStores(){
        database.child("StoreLocations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot location : dataSnapshot.getChildren()){
                    nearbyStores.add(location.getKey());
                }
                CalculateBestStops();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("msg", databaseError.toString());
            }
        });
    }

    private void AddCosts(String storeLoc, float amount, MyListItem item){
        iteration++;
        if(storeCosts.containsKey(storeLoc)){
            float prevCost = storeCosts.get(storeLoc);
            storeCosts.replace(storeLoc, prevCost + amount);
        }
        else {
            storeCosts.put(storeLoc, amount);
            AddStoreName(storeLoc);
        }
    }

    private void AddStops(){
        HashMap<String, Float> tempMap = storeCosts;
        float lowestCost;
        Map.Entry mapItem = null;
        while(tempMap.size() != 0){
            Log.d("Size", String.valueOf(tempMap.size()));
            lowestCost = Float.MAX_VALUE;
            for (Map.Entry element : tempMap.entrySet()) {
                if (Float.parseFloat(element.getValue().toString()) < lowestCost) {
                    lowestCost = Float.parseFloat(element.getValue().toString());
                    mapItem = element;
                }
            }
            float roundedAmount  = Math.round(Float.parseFloat(mapItem.getValue().toString()) * 100f) / 100f;
            allStops.add(new ShopperStop(locationToName.get(mapItem.getKey().toString()), roundedAmount));
            tempMap.remove(mapItem.getKey().toString());
        }

        mAdapter.notifyDataSetChanged();
    }

    private void AddStoreName(final String loc){
        database.child("StoreLocations").child(loc).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("StoreName")){
                    AddStore(loc, dataSnapshot.child("StoreName").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void AddStore(String loc, String name){
        if(!locationToName.containsKey(loc)){
            iteration++;
            locationToName.put(loc, name);
        }

        if(iteration == allItems.size() * nearbyStores.size() + nearbyStores.size()){
            AddStops();
        }
    }
}
