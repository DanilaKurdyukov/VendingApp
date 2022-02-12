package com.example.vendingmachineapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vendingmachineapp.Models.VendingMachineDrink;
import com.example.vendingmachineapp.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class AddEditDrinkFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public AddEditDrinkFragment() {
        // Required empty public constructor
    }


    public static AddEditDrinkFragment newInstance(String param1, String param2) {
        AddEditDrinkFragment fragment = new AddEditDrinkFragment();
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
EditText nameDrink,costDrink,countDrink;
    ImageView imageDrink;
    Button btnSave;
    Uri imageUri;
    AlertDialog.Builder errorDialog;
    private static final int GALLERY_CODE = 100;
    private static final int CAMERA_CODE = 101;
    VendingMachineDrink current;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_edit_drink, container, false);

        nameDrink = rootView.findViewById(R.id.edit_text_drink_name);
        costDrink = rootView.findViewById(R.id.edit_text_drink_cost);
        countDrink = rootView.findViewById(R.id.edit_text_drink_count);
        imageDrink = rootView.findViewById(R.id.image_view_drink_image);
        btnSave = rootView.findViewById(R.id.save);
        errorDialog = new AlertDialog.Builder(getContext());
        errorDialog.setTitle("Ошибка!");
        errorDialog.setCancelable(true);
        errorDialog.create();
        if (getArguments()!=null){
            current = (VendingMachineDrink) getArguments().getSerializable("Drink");
            nameDrink.setText(current.getDrinkName());
            costDrink.setText(String.valueOf(current.getDrinkCost()));
            countDrink.setText(String.valueOf(current.getCount()));
            imageDrink.setImageBitmap(current.getImageData());
            imageDrink.setEnabled(false);
            nameDrink.setEnabled(false);
        }
        errorDialog.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               errorDialog.create().cancel();
            }
        });
        imageDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickDialog();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!nameDrink.getText().toString().trim().isEmpty()
                            &&!costDrink.getText().toString().isEmpty()
                            &&!countDrink.getText().toString().trim().isEmpty()){
                        if (current==null){
                            new PostDrink().execute();
                        }
                            else{
                                new PutDrink().execute();
                        }
                    }
                    else throw new Exception("Заполните поля ввода!");
                }
                catch (Exception e){
                    errorDialog.setMessage(e.getMessage()).show();
                }

            }
        });
        return rootView;
    }

    private void imagePickDialog() {
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(getContext());
        imageDialog.setTitle("Выберите способ получения изображения...");
        imageDialog.setCancelable(false);
        imageDialog.setItems(new String[]{"Камера", "Галерея"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                   if (i==0){
                       pickFromCamera();
                   }
                   else{
                       pickFromGallery();
                   }
            }
        });
        imageDialog.create().show();
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Название изображения");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Описание изображения");
        imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,CAMERA_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==CAMERA_CODE){
                imageDrink.setImageURI(imageUri);
            }
            else{
                imageUri = data.getData();
                imageDrink.setImageURI(imageUri);
            }
            imageDrink.buildDrawingCache();
        }
    }

    private String convertToBase64(Bitmap imageBitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] data = stream.toByteArray();
        return Base64.encodeToString(data,Base64.NO_WRAP);
    }

    private class PutDrink extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("Id",current.getId());
                obj.put("MachineId",current.getMachineId());
                obj.put("DrinkId",current.getDrinkId());
                obj.put("Count",countDrink.getText().toString());
                obj.put("DrinkName",current.getDrinkName());
                obj.put("DrinkCost",costDrink.getText().toString());
                obj.put("DrinkImage",current.getDrinkImage());
                URL url = new URL("http://192.168.0.10:50083/api/VendingMachineDrinks/"+current.getId());
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
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,DrinkFragment.class,null).commit();
                }
            }
            catch (Exception e){
                e.getMessage();
            }
            return null;
        }
    }

    private class PostDrink extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject obj = new JSONObject();
                VendingMachineDrink vendingMachineDrink = new VendingMachineDrink(
                        1,
                        Integer.parseInt(countDrink.getText().toString()),
                        nameDrink.getText().toString(),
                        Integer.parseInt(costDrink.getText().toString()),
                        convertToBase64(imageDrink.getDrawingCache())
                );
                ;
                obj.put("MachineId",1);
                obj.put("Count",countDrink.getText().toString());
                obj.put("DrinkName",nameDrink.getText().toString().trim());
                obj.put("DrinkCost",costDrink.getText().toString());
                obj.put("DrinkImage",convertToBase64(imageDrink.getDrawingCache()));
                URL url = new URL("http://192.168.0.10:50083/api/VendingMachineDrinks");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestMethod("POST");
                connection.connect();
                String jsonText = obj.toString();
                byte[] jsonData = jsonText.getBytes(StandardCharsets.UTF_8);
                OutputStream stream = connection.getOutputStream();
                stream.write(jsonData,0,jsonData.length);
                int responseCode = connection.getResponseCode();
                if (responseCode==201){
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,DrinkFragment.class,null).commit();
                }
            }
            catch (Exception e){
                e.getMessage();
            }
            return null;
        }
    }
}