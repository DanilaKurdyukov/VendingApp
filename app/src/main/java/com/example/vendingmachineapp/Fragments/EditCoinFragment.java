package com.example.vendingmachineapp.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vendingmachineapp.Models.VendingMachineCoin;
import com.example.vendingmachineapp.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class EditCoinFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public EditCoinFragment() {
        // Required empty public constructor
    }


    public static EditCoinFragment newInstance(String param1, String param2) {
        EditCoinFragment fragment = new EditCoinFragment();
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
    }
TextView txtName;
    EditText txtCount;
    CheckBox isActive;
    VendingMachineCoin selected;
    MaterialButton save;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_edit_coin, container, false);

        txtName = rootView.findViewById(R.id.text_view_coin_name_admin);
        txtCount = rootView.findViewById(R.id.edit_text_coin_count_admin);
        isActive = rootView.findViewById(R.id.checkbox_coin_is_active_admin);
        save = rootView.findViewById(R.id.save_coin);
        if (getArguments()!=null){
            selected = (VendingMachineCoin) getArguments().getSerializable("Coin");
            txtName.setText(String.valueOf(selected.getCoinValue()));
            txtCount.setText(String.valueOf(selected.getCount()));
            isActive.setChecked(selected.isActive());
        }
        txtCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!txtCount.getText().toString().trim().isEmpty()){
                    selected.setCount(Integer.parseInt(txtCount.getText().toString()));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        isActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                selected.setActive(isActive.isChecked());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PutCoin().execute();
            }
        });
        return rootView;
    }
    private class PutCoin extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                JSONObject obj = new JSONObject();
                obj.put("Id",selected.getId());
                obj.put("MachineId",selected.getMachineId());
                obj.put("CoinId",selected.getCoinId());
                obj.put("Count",selected.getCount());
                obj.put("IsActive",selected.isActive());
                URL url = new URL("http://192.168.0.10:50083/api/VandingMachineCoins/"+selected.getId());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Content-Type","application/json");
                connection.connect();
                String jsonText = obj.toString();
                byte[] jsonData = jsonText.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(jsonData,0,jsonData.length);
                int responseCode = connection.getResponseCode();
                if (responseCode==204){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,CoinFragment.class,null).commit();
                }
            }
            catch (Exception e){
                e.getMessage();
            }
            return null;
        }
    }
}