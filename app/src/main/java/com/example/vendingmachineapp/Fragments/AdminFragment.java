package com.example.vendingmachineapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.vendingmachineapp.Models.VendingMachine;
import com.example.vendingmachineapp.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AdminFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public AdminFragment() {

    }


    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
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
EditText secretCode;
    Button btnEnter;
    AlertDialog.Builder errorBox;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

        errorBox = new AlertDialog.Builder(getContext());
        errorBox.setTitle("Ошибка!");
        errorBox.setCancelable(true);
        errorBox.create();
        errorBox.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    errorBox.create().cancel();
            }
        });
        secretCode = rootView.findViewById(R.id.secret_code);
        btnEnter = rootView.findViewById(R.id.enter);

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!secretCode.getText().toString().trim().isEmpty()){
                        new GetMachine().execute();
                    }
                    else throw new Exception("Введите код!");
                }
                catch (Exception e){
                    errorBox.setMessage(e.getMessage()).show();
                }
            }
        });

        return rootView;
    }
    private class GetMachine extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.0.103:50083/api/VendingMachines?code="+secretCode.getText().toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line  = "";
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
                if (s!=null){
                    JSONObject obj = new JSONObject(s);
                    VendingMachine machine = new VendingMachine(
                            obj.getInt("Id"),
                            obj.getString("SecretCode")
                    );
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,AdminMainFragment.class,null).addToBackStack(null).commit();
                }
                else throw new Exception("Неверный код!");
            }
            catch (Exception e){
                errorBox.setMessage(e.getMessage()).show();
            }
        }
    }
}