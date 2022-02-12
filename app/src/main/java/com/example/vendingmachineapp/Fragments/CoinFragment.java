package com.example.vendingmachineapp.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vendingmachineapp.Adapters.VendingCoinAdapter;
import com.example.vendingmachineapp.Adapters.VendingCoinAdapterForAdmin;
import com.example.vendingmachineapp.Models.VendingMachineCoin;
import com.example.vendingmachineapp.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class CoinFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public CoinFragment() {
        // Required empty public constructor
    }


    public static CoinFragment newInstance(String param1, String param2) {
        CoinFragment fragment = new CoinFragment();
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
        new GetCoins().execute();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_menu,menu);
        MenuItem item = menu.findItem(R.id.item_delete);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_edit:
                Bundle args = new Bundle();
                args.putSerializable("Coin",current);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,EditCoinFragment.class,args).commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    RecyclerView viewCoins;
    List<VendingMachineCoin> vendingMachineCoins = new ArrayList<>();
    VendingCoinAdapterForAdmin vendingCoinAdapter;
    VendingMachineCoin current;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_coin, container, false);

        vendingCoinAdapter = new VendingCoinAdapterForAdmin(vendingMachineCoins,getContext());
        viewCoins = rootView.findViewById(R.id.recycler_view_coins);
        viewCoins.setLayoutManager(new GridLayoutManager(getContext(),4));
        setHasOptionsMenu(true);
        setMenuVisibility(false);
        vendingCoinAdapter.setOnItemClickListener(new VendingCoinAdapterForAdmin.ItemClickListener() {
            @Override
            public void onClick(VendingMachineCoin vendingMachineCoin) {
                current = vendingMachineCoin;
                setMenuVisibility(true);
            }
        });
        viewCoins.setAdapter(vendingCoinAdapter);

        return rootView;
    }

    private class GetCoins extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.0.103:50083/api/VandingMachineCoins");
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
                vendingCoinAdapter.notifyDataSetChanged();
                }
            }
            catch (Exception e){
                e.getMessage();
            }
        }
    }

}