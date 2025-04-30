package com.example.arti_sun;

import android.widget.Filter;

import java.util.ArrayList;

public class UFilterProduct extends Filter {
    private ArrayList<ProductList> filterList;
    private AdapterUProduct adapter;

    public UFilterProduct(AdapterUProduct adapter, ArrayList<ProductList> filterList){
        this.adapter = adapter;
        this.filterList = filterList;

    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if(charSequence != null && charSequence.length()>0){
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ProductList> filteredproduct = new ArrayList<>();
            for (int i=0; i<filterList.size();i++){
                if (filterList.get(i).getProductName().toUpperCase().contains(charSequence)||
                        filterList.get(i).getProductCategory().toUpperCase().contains(charSequence)){

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

        adapter.productsList = (ArrayList<ProductList>) filterResults.values;
        adapter.notifyDataSetChanged();

    }
}
