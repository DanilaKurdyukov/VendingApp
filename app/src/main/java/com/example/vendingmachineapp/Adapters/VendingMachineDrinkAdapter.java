package com.example.vendingmachineapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vendingmachineapp.Fragments.UserFragment;
import com.example.vendingmachineapp.Models.VendingMachineDrink;
import com.example.vendingmachineapp.R;

import java.util.List;

public class VendingMachineDrinkAdapter extends RecyclerView.Adapter<VendingMachineDrinkAdapter.ViewHolder>{

    public interface ItemClickListener{
        void onClick(VendingMachineDrink vendingMachineDrink);
    }

    private List<VendingMachineDrink> vendingMachineDrinks;
    private LayoutInflater inflater;
    private ItemClickListener itemClickListener;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    public VendingMachineDrinkAdapter(List<VendingMachineDrink> vendingMachineDrinks, Context mContext) {
        this.vendingMachineDrinks = vendingMachineDrinks;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =View.inflate(parent.getContext(),R.layout.vending_drink_item,null );
        return new VendingMachineDrinkAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VendingMachineDrink current =vendingMachineDrinks.get(position);
        holder.drinkName.setText(current.getDrinkName());
        holder.drinkCost.setText(String.valueOf(current.getDrinkCost())+"р.");
        holder.drinkImage.setImageBitmap(current.getImageData());
        if (current.getCount()==0){
            holder.itemView.setBackgroundColor(Color.GRAY);
            holder.itemView.setEnabled(false);
        }
        if (itemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        if (selectedItems!=null){
                            if (selectedItems.get(holder.getAdapterPosition(), false)) {
                                selectedItems.delete(holder.getAdapterPosition());
                                view.setSelected(false);
                            }
                            else{
                                selectedItems.put(holder.getAdapterPosition(), true);
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

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return vendingMachineDrinks.size();
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
