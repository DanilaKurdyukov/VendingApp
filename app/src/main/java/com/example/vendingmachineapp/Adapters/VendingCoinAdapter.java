package com.example.vendingmachineapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendingmachineapp.Models.VendingMachineCoin;
import com.example.vendingmachineapp.R;

import java.util.List;

public class VendingCoinAdapter extends RecyclerView.Adapter<VendingCoinAdapter.ViewHolder>{
    private List<VendingMachineCoin> vendingMachineCoinList;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(),R.layout.vending_coin_item,null);
        return new VendingCoinAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VendingMachineCoin current = vendingMachineCoinList.get(position);
        holder.txtNameCoin.setText(String.valueOf(current.getCoinValue()));
        if (current.isActive()==false){
            holder.itemView.setBackgroundColor(Color.GRAY);
            holder.itemView.setEnabled(false);
        }
        if (itemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        if (selectedItems!=null){
                            if (selectedItems.get(holder.getAdapterPosition(),false)){
                                selectedItems.delete(holder.getAdapterPosition());
                                view.setSelected(false);
                            }
                            else{
                                selectedItems.put(holder.getAdapterPosition(),true);
                                view.setSelected(true);
                            }
                        }

                        itemClickListener.onClick(current);

                    }
                    catch (Exception e){
                        e.getMessage();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return vendingMachineCoinList.size();
    }

    public interface ItemClickListener{
        void onClick(VendingMachineCoin vendingMachineCoin);
    }
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;

    public VendingCoinAdapter(List<VendingMachineCoin> vendingMachineCoinList, Context mContext) {
        this.vendingMachineCoinList = vendingMachineCoinList;
        this.inflater = LayoutInflater.from(mContext);
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtNameCoin;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNameCoin = itemView.findViewById(R.id.coin_name);
        }
    }

}
