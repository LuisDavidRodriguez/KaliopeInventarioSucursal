package com.example.david.inventariosucursal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterListFiveItems extends BaseAdapter {
    private ArrayList<HashMap> list;
    Activity activity;

    public AdapterListFiveItems (ArrayList<HashMap> list, Activity activity){
        super();
        this.list = list;
        this.activity = activity;

    }


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
        TextView codigo,
                precio,
                vendedora,
                socia,
                empresaria;


    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.list_five_items,null);

        viewHolder.codigo = (TextView) view.findViewById(R.id.list_five_itemsCode);
        viewHolder.precio = (TextView) view.findViewById(R.id.list_five_itemsPrecio);
        viewHolder.vendedora = (TextView) view.findViewById(R.id.list_five_itemsVendedora);
        viewHolder.socia = (TextView) view.findViewById(R.id.list_five_itemsSocia);
        viewHolder.empresaria = (TextView)view.findViewById(R.id.list_five_itemsEmpresaria);

        HashMap hashMap = list.get(i);
        viewHolder.codigo.setText(hashMap.get(Constant.UNO).toString());
        viewHolder.precio.setText(hashMap.get(Constant.DOS).toString());
        viewHolder.vendedora.setText(hashMap.get(Constant.TRES).toString());
        viewHolder.socia.setText(hashMap.get(Constant.CUATRO).toString());
        viewHolder.empresaria.setText(hashMap.get(Constant.CINCO).toString());



        return view;
    }

}
