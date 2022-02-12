package com.example.vendingmachineapp.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendingmachineapp.Models.VendingMachineCoin;
import com.example.vendingmachineapp.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class VendingCoinAdapterForAdmin extends RecyclerView.Adapter<VendingCoinAdapterForAdmin.ViewHolder>{
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = View.inflate(parent.getContext(),R.layout.vending_coin_admin_item,null);
        return new VendingCoinAdapterForAdmin.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VendingMachineCoin current = vendingMachineCoins.get(position);
        holder.txtName.setText(String.valueOf(current.getCoinValue()));
        holder.txtCount.setText(String.valueOf(current.getCount()));
        holder.isActive.setChecked(current.isActive());
        holder.txtCount.setEnabled(false);
        holder.isActive.setEnabled(false);
        if (itemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onClick(current);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return vendingMachineCoins.size();
    }

    public interface  ItemClickListener{
        void onClick(VendingMachineCoin vendingMachineCoin);
    }

    private ItemClickListener itemClickListener;
    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener  = itemClickListener;
    }


    private List<VendingMachineCoin> vendingMachineCoins;
    private LayoutInflater inflater;
    public VendingCoinAdapterForAdmin(List<VendingMachineCoin> vendingMachineCoins, Context mContext) {
        this.vendingMachineCoins = vendingMachineCoins;
        this.inflater = LayoutInflater.from(mContext);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName;
        EditText txtCount;
        MaterialCheckBox isActive;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.coin_name_admin);
            txtCount = itemView.findViewById(R.id.coin_count_admin);
            isActive = itemView.findViewById(R.id.coin_is_active_admin);
        }
    }



}
