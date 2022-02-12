package com.example.vendingmachineapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendingmachineapp.Models.VendingMachineDrink;
import com.example.vendingmachineapp.R;

import java.util.List;

public class VendingMachineDrinkAdapterForAdmin extends RecyclerView.Adapter<VendingMachineDrinkAdapterForAdmin.ViewHolder>{


    public interface  ItemClickListener{
        void onClick(VendingMachineDrink vendingMachineDrink);
    }
    private List<VendingMachineDrink> vendingMachineDrinkList;
    private ItemClickListener itemClickListener;
    private LayoutInflater inflater;
    public VendingMachineDrinkAdapterForAdmin(List<VendingMachineDrink> vendingMachineDrinkList, Context mContext) {
        this.vendingMachineDrinkList = vendingMachineDrinkList;
        this.inflater = LayoutInflater.from(mContext);
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(),R.layout.vending_drink_item,null);
        return new VendingMachineDrinkAdapterForAdmin.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VendingMachineDrink current = vendingMachineDrinkList.get(position);
        holder.drinkName.setText(current.getDrinkName());
        holder.drinkCost.setText(String.valueOf(current.getDrinkCost())+"р.");
        holder.drinkImage.setImageBitmap(current.getImageData());
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
        return vendingMachineDrinkList.size();
    }



    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView drinkImage;
        TextView drinkName,drinkCost;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            drinkImage = itemView.findViewById(R.id.drink_image);
            drinkName = itemView.findViewById(R.id.drink_name);
            drinkCost = itemView.findViewById(R.id.drink_cost);
        }
    }

}
