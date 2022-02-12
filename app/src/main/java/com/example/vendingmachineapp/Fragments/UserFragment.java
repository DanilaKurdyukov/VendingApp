package com.example.vendingmachineapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vendingmachineapp.Adapters.VendingCoinAdapter;
import com.example.vendingmachineapp.Adapters.VendingMachineDrinkAdapter;
import com.example.vendingmachineapp.Models.VendingMachineCoin;
import com.example.vendingmachineapp.Models.VendingMachineDrink;
import com.example.vendingmachineapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class UserFragment extends Fragment {



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }


    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        new GetDrinks().execute();
        new GetCoins().execute();
    }
    List<VendingMachineDrink> vendingMachineDrinks = new ArrayList<>();
    VendingMachineDrinkAdapter vendingMachineDrinkAdapter;
RecyclerView viewDrinks,viewCoins;
VendingCoinAdapter coinAdapter;
List<VendingMachineCoin> vendingMachineCoins = new ArrayList<>();
int totalSum=0;
VendingMachineDrink selected;
MaterialTextView txtTotalSum;
AlertDialog.Builder dialogBox;
MaterialButton btnCashBack;
int cashBack;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        dialogBox = new AlertDialog.Builder(getContext());
        dialogBox.setCancelable(true);
        dialogBox.setTitle("Ошибка!");
        dialogBox.create();
        dialogBox.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogBox.create().cancel();
            }
        });
        txtTotalSum = rootView.findViewById(R.id.totalSum);
        viewDrinks = rootView.findViewById(R.id.view_drink_list);
        viewDrinks.setLayoutManager(new GridLayoutManager(getContext(),2));
        vendingMachineDrinkAdapter = new VendingMachineDrinkAdapter(vendingMachineDrinks,getContext());
        viewDrinks.setAdapter(vendingMachineDrinkAdapter);
        vendingMachineDrinkAdapter.setOnItemClickListener(new VendingMachineDrinkAdapter.ItemClickListener() {
            @Override
            public void onClick(VendingMachineDrink current) {
                selected = current;
                if (selected.getDrinkCost()>totalSum){
                    dialogBox.setMessage("Цена напитка больше внесенной суммы!");
                    dialogBox.show();
                }
                else{
                    selected.setCount(selected.getCount()-1);
                    cashBack = totalSum -= selected.getDrinkCost();
                    txtTotalSum.setText(String.valueOf(totalSum));
                    new PutDrink().execute();
                    if (selected.getCount()==0){
                        vendingMachineDrinks = new ArrayList<>();
                        new GetDrinks().execute();
                    }
                }
            }
        });


        viewCoins = rootView.findViewById(R.id.view_coinList);
        viewCoins.setLayoutManager(new GridLayoutManager(getContext(),2));
        coinAdapter = new VendingCoinAdapter(vendingMachineCoins,getContext());
        viewCoins.setAdapter(coinAdapter);

        coinAdapter.setOnItemClickListener(new VendingCoinAdapter.ItemClickListener() {
            @Override
            public void onClick(VendingMachineCoin current) {
                totalSum+=current.getCoinValue();
                txtTotalSum.setText(String.valueOf(totalSum));
            }
        });


        btnCashBack = rootView.findViewById(R.id.cash_back);
        btnCashBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBox.setTitle("Информация");
                dialogBox.setMessage("Сдача: " + String.valueOf(cashBack) + " руб.").show();
                totalSum=0;
                txtTotalSum.setText("");
            }
        });


        return rootView;
    }

    private class PutDrink extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("Id",selected.getId());
                obj.put("MachineId",selected.getMachineId());
                obj.put("DrinkId",selected.getDrinkId());
                obj.put("Count",selected.getCount());
                obj.put("DrinkName",selected.getDrinkName());
                obj.put("DrinkCost",selected.getDrinkCost());
                obj.put("DrinkImage",selected.getDrinkImage());
                URL url = new URL("http://192.168.0.10:50083/api/VendingMachineDrinks/"+selected.getId());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type","application/json");
                connection.connect();
                String jsonText = obj.toString();
                byte[] dataJson = jsonText.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(dataJson,0,dataJson.length);
                int responseCode = connection.getResponseCode();
            }
            catch (Exception e){
                e.getMessage();
            }
            return null;
        }
    }

    private class GetDrinks extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.0.10:50083/api/VendingMachineDrinks");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line="";
                while((line= reader.readLine())!=null){
                    result.append(line);
                }
                return result.toString();
            }
            catch (Exception e){
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0;i<array.length();i++){
                    JSONObject obj = array.getJSONObject(i);
                    VendingMachineDrink vendingMachineDrink = new VendingMachineDrink(
                            obj.getInt("Id"),
                            obj.getInt("MachineId"),
                            obj.getInt("DrinkId"),
                            obj.getInt("Count"),
                            obj.getString("DrinkName"),
                            Integer.parseInt(String.valueOf(obj.getDouble("DrinkCost")).split("\\.")[0]),
                            obj.getString("DrinkImage")
                    );
                    vendingMachineDrinks.add(vendingMachineDrink);
                    vendingMachineDrinkAdapter.notifyDataSetChanged();
                }
            }
            catch (Exception e){
                e.getMessage();
            }
        }
    }
    private class GetCoins extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.0.10:50083/api/VandingMachineCoins");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line="";
                while((line= reader.readLine())!=null){
                    result.append(line);
                }
                return result.toString();
            }
            catch (Exception e){
                e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray array = new JSONArray(s);
                for(int i=0;i<array.length();i++){
                    JSONObject obj = array.getJSONObject(i);
                    VendingMachineCoin vendingMachineCoin = new VendingMachineCoin(
                            obj.getInt("Id"),
                            obj.getInt("MachineId"),
                            obj.getInt("CoinId"),
                            obj.getInt("Count"),
                            obj.getBoolean("IsActive"),
                            obj.getInt("CoinValue")
                    );
                    vendingMachineCoins.add(vendingMachineCoin);
                    coinAdapter.notifyDataSetChanged();
                }
            }
            catch (Exception e){
                e.getMessage();
            }
        }
    }
}