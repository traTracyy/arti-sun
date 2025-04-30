package com.example.arti_sun;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterOrderShop extends Filter {
    private ArrayList<ModelOrderShop> filterList;
    private AdapterOrderShop adapter;

    public FilterOrderShop(AdapterOrderShop adapter, ArrayList<ModelOrderShop> filterList){
        this.adapter = adapter;
        this.filterList = filterList;

    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelOrderShop> filteredproduct = new ArrayList<>();
            for (int i=0; i<filterList.size();i++){
                if (filterList.get(i).getOrderStatus().toUpperCase().contains(charSequence)){

                    filteredproduct.add(filterList.get(i));
                }
            }
            results.count = filteredproduct.size();
            results.values = filteredproduct;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;

        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

        adapter.orderShopArrayList = (ArrayList<ModelOrderShop>) filterResults.values;
        adapter.notifyDataSetChanged();

    }
}
