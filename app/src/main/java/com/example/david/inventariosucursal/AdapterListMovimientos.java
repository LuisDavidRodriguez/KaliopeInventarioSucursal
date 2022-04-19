package com.example.david.inventariosucursal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterListMovimientos extends BaseAdapter {

    private ArrayList<HashMap> list;
    Activity activity;

    public AdapterListMovimientos (ArrayList<HashMap> list, Activity activity){
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
        TextView idEncabezado,
                pulseraAgente,
                pulseraReviso,
                tipoMovimiento,
                cantidad,
                importe,
                fechaHora;


    }



    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        view = layoutInflater.inflate(R.layout.list_movimientos,null);

        viewHolder.idEncabezado = (TextView) view.findViewById(R.id.list_movimientos_idEncabezadoTV);
        viewHolder.pulseraAgente = (TextView) view.findViewById(R.id.list_movimientos_pulseraAgenteTV);
        viewHolder.pulseraReviso = (TextView) view.findViewById(R.id.list_movimientos_pulseraRevisoTV);
        viewHolder.tipoMovimiento = (TextView) view.findViewById(R.id.list_movimientos_TipoMovimientoTV);
        viewHolder.cantidad = (TextView) view.findViewById(R.id.list_movimientos_cantidadTV);
        viewHolder.importe = (TextView)view.findViewById(R.id.list_movimientos_importeTV);
        viewHolder.fechaHora = (TextView)view.findViewById(R.id.list_movimientos_fechaHoraTV);

        HashMap hashMap = list.get(i);
        viewHolder.idEncabezado.setText(hashMap.get(Constant.UNO).toString());
        viewHolder.pulseraAgente.setText(hashMap.get(Constant.DOS).toString());
        viewHolder.pulseraReviso.setText(hashMap.get(Constant.TRES).toString());
        viewHolder.tipoMovimiento.setText(hashMap.get(Constant.CUATRO).toString());
        viewHolder.cantidad.setText(hashMap.get(Constant.CINCO).toString());
        viewHolder.importe.setText(hashMap.get(Constant.SEIS).toString());
        viewHolder.fechaHora.setText(hashMap.get(Constant.SIETE).toString());

        return view;
    }
}
