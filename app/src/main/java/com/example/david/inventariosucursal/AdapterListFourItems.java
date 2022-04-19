package com.example.david.inventariosucursal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;



public class AdapterListFourItems extends BaseAdapter {
    private ArrayList <HashMap> list;
    Activity activity;

    public AdapterListFourItems(ArrayList<HashMap> list, Activity activity){
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
                itemDos,
                itemTres,
                itemCuatro;


    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.list_four_items,null);

        viewHolder.itemUno = (TextView) view.findViewById(R.id.list_four_item1);
        viewHolder.itemDos = (TextView) view.findViewById(R.id.list_four_item2);
        viewHolder.itemTres = (TextView) view.findViewById(R.id.list_four_item3);
        viewHolder.itemCuatro = (TextView) view.findViewById(R.id.list_four_item4);

        HashMap hashMap = list.get(i);
        viewHolder.itemUno.setText(hashMap.get(Constant.UNO).toString());
        viewHolder.itemDos.setText(hashMap.get(Constant.DOS).toString());
        viewHolder.itemTres.setText(hashMap.get(Constant.TRES).toString());
        viewHolder.itemCuatro.setText(hashMap.get(Constant.CUATRO).toString());


        return view;



    }
}
