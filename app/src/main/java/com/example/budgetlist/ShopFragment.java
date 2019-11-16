package com.example.budgetlist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopFragment extends Fragment {

    DatabaseReference database;
    EditText editText_shopSearch;
    ListView listView_shopResults;

    ArrayList<String> searchItems = new ArrayList<>();
    ArrayAdapter<String> searchAdapter;

    ArrayList<String> shopList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        database = FirebaseDatabase.getInstance().getReference();
        editText_shopSearch = view.findViewById(R.id.shop_edittext_search);
        listView_shopResults = view.findViewById(R.id.shop_listview_searchresults);

        searchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchItems);
        listView_shopResults.setAdapter(searchAdapter);

        listView_shopResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int index, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add Item").setMessage("Are you sure you want to add " +
                        searchItems.get(index) + "?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        shopList.add(searchItems.get(index));
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        editText_shopSearch.addTextChangedListener(new TextWatcher() {
            boolean ignore = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (ignore) return;

                ignore = true;

                String s = editable.toString();
                if (s.length() > 0) {
                    // The condition checks if the last typed char's ASCII value is equal to 10, which is the new line decimal value
                    if (((int)(s.charAt(s.length()-1)) == 10)) {
                        String newStr = s.substring(0, s.length()-1); // Removes the new line character from the string
                        editText_shopSearch.setText(newStr);
                        editText_shopSearch.setSelection(editText_shopSearch.length()); // Sets the text cursor to the end of the text
                    }
                }

                ignore = false;
            }
        });

        editText_shopSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER){
                    hideKeyboardFrom(getContext(), view);
                    if(editText_shopSearch.getText().length() > 0){
                        searchItems.clear();
                        PopulateListView(editText_shopSearch.getText().toString());
                    }
                }
                return false;
            }
        });

        return view;
    }

    private void PopulateListView(final String itemName){
        database.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    if(item.getKey().toLowerCase().contains(itemName.toLowerCase())){
                        searchItems.add(item.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
