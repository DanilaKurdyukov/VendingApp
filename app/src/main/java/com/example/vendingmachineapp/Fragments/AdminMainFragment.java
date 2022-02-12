package com.example.vendingmachineapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.vendingmachineapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class AdminMainFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public AdminMainFragment() {
        // Required empty public constructor
    }


    public static AdminMainFragment newInstance(String param1, String param2) {
        AdminMainFragment fragment = new AdminMainFragment();
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
BottomNavigationView navigationView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View rootView =  inflater.inflate(R.layout.fragment_admin_main, container, false);

      navigationView = rootView.findViewById(R.id.navigation_menu);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,DrinkFragment.class,null).commit();
      navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
          @Override
          public boolean onNavigationItemSelected(@NonNull MenuItem item) {
              switch(item.getItemId()){
                  case R.id.item_drink:
                      getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,DrinkFragment.class,null).commit();
                      break;
                  case R.id.item_coin:
                      getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame,CoinFragment.class,null).commit();
                      break;
              }
              return true;
          }
      });

        return rootView;
    }
}