package com.example.budgetlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ShopFragment extends Fragment {

    DatabaseReference database;
    EditText editText_shopSearch;
    ListView listView_shopResults;

    //Holds all the items found in the search query
    ArrayList<String> searchItems = new ArrayList<>();
    //Sets up the adapter to adjust the list view with the found search items
    ArrayAdapter<String> searchAdapter;

    //Holds all the items that the user selected to be in their list
    ArrayList<MyListItem> shopList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Err", "Shop Opened");
        //Sets the fragment up with the specified fragment xml file
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        //Gets the database reference
        database = FirebaseDatabase.getInstance().getReference();

        //Sets up the references for the ui elements
        editText_shopSearch = view.findViewById(R.id.shop_edittext_search);
        listView_shopResults = view.findViewById(R.id.shop_listview_searchresults);

        //Creates a new adapter for the list view
        searchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchItems);
        listView_shopResults.setAdapter(searchAdapter);

        InitializeShopperList();

        //Sets up the on item click listener for the list view
        listView_shopResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
                //Creates a dialog box to make sure the user wants to add it to their list
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Item").setMessage("Are you sure you want to add " +
                        searchItems.get(index) + "?");

                //Sets the positive button of the dialog box, and if pressed adds the item
                //to the shopper list
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean foundItem = false;

                        if(shopList != null){
                            for(MyListItem item : shopList){
                                if(item.getItemName().equals(searchItems.get(index))){
                                    foundItem = true;
                                }
                            }
                        }
                        else{
                            shopList = new ArrayList<>();
                        }

                        if(!foundItem){
                            shopList.add(new MyListItem(searchItems.get(index)));
                            SaveItemsToLocalStorage();
                        }
                    }
                });

                //Sets the negative button of the dialog box
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //Builds and shows the dialog box
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Adds an on text change listener to the edit text for the search query
        editText_shopSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Updates the list view when the text changes and updates the adapter
                if(editText_shopSearch.getText().length() > 0){
                    searchItems.clear();
                    PopulateListView(editText_shopSearch.getText().toString());
                }
                else{
                    searchItems.clear();
                    ((BaseAdapter)listView_shopResults.getAdapter()).notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    //Checks the database to see what products match the users query
    private void PopulateListView(final String itemName){
        database.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Each item found will be added to the list view
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    if(item.getKey().toLowerCase().contains(itemName.toLowerCase())){
                        searchItems.add(item.getKey());
                        ((BaseAdapter)listView_shopResults.getAdapter()).notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SaveItemsToLocalStorage(){
        Gson gson = new Gson();
        String json = gson.toJson(shopList);
        SharedPreferences sharedPreferences = this.getActivity().getPreferences((Context.MODE_PRIVATE));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ShoppingItems", json);
        editor.apply();
    }

    private void InitializeShopperList(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ShoppingItems", "");

        Type type = new TypeToken<ArrayList<MyListItem>>(){}.getType();
        shopList = gson.fromJson(json, type);
    }
}
