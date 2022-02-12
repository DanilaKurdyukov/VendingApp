package com.example.vendingmachineapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

import com.example.vendingmachineapp.Fragments.StartFragment;
import com.example.vendingmachineapp.R;

public class MainActivity extends AppCompatActivity {
Toolbar mainBar;
FrameLayout frmMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainBar = findViewById(R.id.main_app_bar);
        frmMain = findViewById(R.id.main_frame);
        setSupportActionBar(mainBar);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame,StartFragment.class,null).commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount()==0){
            super.onBackPressed();
        }
        else{
            getSupportFragmentManager().popBackStack();
        }
    }
}