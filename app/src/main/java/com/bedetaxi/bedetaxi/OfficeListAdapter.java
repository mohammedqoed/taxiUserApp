package com.bedetaxi.bedetaxi;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by LENOVO on 4/13/2017.
 */

public class OfficeListAdapter extends BaseAdapter {
    String OfficesList[];
    Context context;
    LayoutInflater inflter;
    public OfficeListAdapter(Context context,String  Offices[]){
        super();
        this.context=context;
        this.OfficesList = Offices;
        inflter = (LayoutInflater.from(context));
    }



    @Override
    public int getCount() {
        return OfficesList.length;
    }

    @Override
    public Object getItem(int position) {
        return OfficesList[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflter.inflate(R.layout.spinner_item, null);//set layout for displaying items
        TextView row = (TextView) convertView.findViewById(R.id.SpinnerRow);


        row.setText(OfficesList[position]);



        return convertView;
    }
}
