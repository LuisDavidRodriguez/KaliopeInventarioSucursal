package com.example.david.inventariosucursal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterListTwoItems extends BaseAdapter {
    private ArrayList<HashMap> list;
    Activity activity;

    public AdapterListTwoItems(ArrayList<HashMap> list, Activity activity){
        super();
        this.list = list;
        this.activity = activity;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    private class ViewHolder{
        TextView itemUno,
                itemDos;



    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.list_two_items,null);

        viewHolder.itemUno = (TextView) view.findViewById(R.id.list_two_item1);
        viewHolder.itemDos = (TextView) view.findViewById(R.id.list_two_item2);


        HashMap hashMap = list.get(i);
        viewHolder.itemUno.setText(hashMap.get(Constant.UNO).toString());
        viewHolder.itemDos.setText(hashMap.get(Constant.DOS).toString());

        return view;



    }
}
