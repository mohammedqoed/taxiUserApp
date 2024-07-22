package com.bedetaxi.bedetaxi;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by LENOVO on 1/23/2017.
 */

public class HistoryAdapter extends BaseAdapter {


    private Context context;
    private List<HistoryList> MList1;

    public HistoryAdapter(Context mContext, List<HistoryList> mList1) {
        this.context = mContext;
        this.MList1 = mList1;
    }

    @Override
    public int getCount() {
        return MList1.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View vi=convertView;
        if(convertView==null)
            vi = LayoutInflater.from(context).inflate(R.layout.history_row, null);



        TextView date = (TextView)vi.findViewById(R.id.date);
        TextView dest = (TextView)vi.findViewById(R.id.dest);
        TextView pickup = (TextView)vi.findViewById(R.id.pickup);
        TextView driver = (TextView)vi.findViewById(R.id.driverName);

        Calendar calendar = Calendar.getInstance();
        String ackwardRipOff = MList1.get(position).getDate().replace("/Date(", "").replace(")/", "");
        Long timeInMillis = Long.valueOf(ackwardRipOff);
        calendar.setTimeInMillis(timeInMillis);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatted = format1.format(calendar.getTime());

        date.setText(formatted);
        dest.setText(MList1.get(position).getDest());
        pickup.setText(MList1.get(position).getPickUp());
        driver.setText(MList1.get(position).getDrive());





        return vi;
    }
}
