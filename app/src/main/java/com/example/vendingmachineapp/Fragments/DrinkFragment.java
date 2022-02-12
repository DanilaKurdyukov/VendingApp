package com.example.vendingmachineapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vendingmachineapp.Adapters.VendingMachineDrinkAdapter;
import com.example.vendingmachineapp.Adapters.VendingMachineDrinkAdapterForAdmin;
import com.example.vendingmachineapp.BuildConfig;
import com.example.vendingmachineapp.Models.VendingMachineDrink;
import com.example.vendingmachineapp.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DrinkFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public DrinkFragment() {
        // Required empty public constructor
    }


    public static DrinkFragment newInstance(String param1, String param2) {
        DrinkFragment fragment = new DrinkFragment();
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
    }
RecyclerView drinksView;
    List<VendingMachineDrink> vendingMachineDrinkList=new ArrayList<>();
    VendingMachineDrinkAdapterForAdmin vendingMachineDrinkAdapter;
    MaterialButton btnAdd;
    VendingMachineDrink selected;
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_delete:
                new DeleteDrink().execute();
                break;
            case R.id.item_edit:
                Bundle args = new Bundle();
                args.putSerializable("Drink",selected);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,AddEditDrinkFragment.class,args).commit();
                break;
            case R.id.item_report:
                createPDF();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
private File targetFile;
    private final int SAVE_DOCUMENT_REQUEST_CODE = 0x445;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==SAVE_DOCUMENT_REQUEST_CODE){
                Uri uri = data.getData();
                saveFile(uri);
            }
        }
    }
    int xLoc = 400,yLoc=200;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void saveFile(Uri uri) {
        try {
            String[] headers = {"Название","Цена","Кол-во"};
            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument doc = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000,1000,1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint mainHeader = new Paint();
            mainHeader.setColor(Color.BLACK);
            mainHeader.setTextSize(30);
            canvas.drawText("Отчет",500,100,mainHeader);
            for (int i = 0; i<3;i++){
                Paint header = new Paint();
                header.setColor(Color.BLACK);
                header.setTextSize(24);
                header.setUnderlineText(true);
                canvas.drawText(headers[i], xLoc, yLoc, header);
                xLoc+=150;
            }
            xLoc=400;
            yLoc+=50;
            for(int i =0;i<vendingMachineDrinkList.size();i++){
                Paint value = new Paint();
                value.setColor(Color.BLACK);
                value.setTextSize(24);
                String text = vendingMachineDrinkList.get(i).getDrinkName() + "       " + vendingMachineDrinkList.get(i).getDrinkCost()  + "                                " + vendingMachineDrinkList.get(i).getCount();
                canvas.drawText(text,xLoc,yLoc,value);
                yLoc+=50;
            }
            pdfDocument.finishPage(page);
            try {
                pdfDocument.writeTo(getActivity().getContentResolver().openOutputStream(uri));
                Toast.makeText(getContext(), "Done", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("main", "error "+e.toString());
                Toast.makeText(getContext(), "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e){
            e.getMessage();
        }
    }

    private void createPDF(){

        try {
            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
            targetFile = new File(directory_path);
            Uri reportFileUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider",targetFile);
            Intent saveIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            saveIntent.addCategory(Intent.CATEGORY_OPENABLE);
            saveIntent.setType("application/pdf");
            saveIntent.putExtra(Intent.EXTRA_TITLE,targetFile.getName());
            saveIntent.putExtra(DocumentsContract.EXTRA_INITIAL_URI,reportFileUri);
            startActivityForResult(saveIntent,SAVE_DOCUMENT_REQUEST_CODE);
        }
        catch (Exception e){
            e.getMessage();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            View rootView = inflater.inflate(R.layout.fragment_drink, container, false);

            drinksView = rootView.findViewById(R.id.recycler_view_drinks);
            drinksView.setLayoutManager(new GridLayoutManager(getContext(),2));
            setHasOptionsMenu(true);
            setMenuVisibility(false);
            vendingMachineDrinkAdapter = new VendingMachineDrinkAdapterForAdmin(vendingMachineDrinkList,getContext());
            vendingMachineDrinkAdapter.setOnItemClickListener(new VendingMachineDrinkAdapterForAdmin.ItemClickListener() {
                @Override
                public void onClick(VendingMachineDrink vendingMachineDrink) {
                    selected = vendingMachineDrink;
                    setMenuVisibility(true);
                }
            });
            drinksView.setAdapter(vendingMachineDrinkAdapter);

            btnAdd = rootView.findViewById(R.id.add_drink);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,AddEditDrinkFragment.class,null).addToBackStack(null).commit();
                }
            });

            return rootView;
        }
        catch (Exception e){
            e.getMessage();
        }
       return null;
    }
    private class DeleteDrink extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.0.103:50083/api/VendingMachineDrinks/"+selected.getId());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("DELETE");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode==200){
                    vendingMachineDrinkList.remove(selected);
                    vendingMachineDrinkAdapter = new VendingMachineDrinkAdapterForAdmin(vendingMachineDrinkList,getContext());
                    drinksView.setAdapter(vendingMachineDrinkAdapter);
                }
            }
            catch (Exception e){
                e.getMessage();
            }

            return null;
        }
    }



    private class GetDrinks extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("http://192.168.0.103:50083/api/VendingMachineDrinks");
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
                    vendingMachineDrinkList.add(vendingMachineDrink);
                    vendingMachineDrinkAdapter.notifyDataSetChanged();
                }
            }
            catch (Exception e){
                e.getMessage();
            }
        }
    }
}