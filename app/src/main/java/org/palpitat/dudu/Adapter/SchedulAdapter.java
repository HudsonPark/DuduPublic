package org.palpitat.dudu.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.palpitat.dudu.R;
import org.palpitat.dudu.fragment.ScheduleFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.palpitat.dudu.R.*;
import static org.palpitat.dudu.R.id.*;

public class SchedulAdapter extends BaseAdapter  {

    private   List<String> list;
    private LayoutInflater inflater;
    private Calendar mCal = Calendar.getInstance();
    private Date mDate ;

    public SchedulAdapter(Context context, List<String> list){
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    int firstDay;

    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
/*
    DateCount dateCount = new DateCount() {
        @Override
        public void pluseMonthCount() {
            mCountK++;
        }

        @Override
        public void minusMonthCount() {
            mCountK--;
        }
    };
*/
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if  (convertView == null){
            convertView = inflater.inflate(layout.item_calendar,parent,false);
            holder = new ViewHolder();

            holder.calendaritem = (TextView)convertView.findViewById(calendar_item);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }




        holder.calendaritem.setText(""+ getItem(position));
        holder.calendaritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"현재 날짜는 "+mCal.get(Calendar.YEAR)+(mCal.get(Calendar.MONTH)+ScheduleFragment.mCountK)+getItem(position),Toast.LENGTH_SHORT).show();

            }
        });

        Integer today = mCal.get(Calendar.DAY_OF_MONTH);

        if(ScheduleFragment.mCountK==0){


        String sToday = String.valueOf(today);
        if(sToday.equals(getItem(position))) {

            holder.calendaritem.setTextColor(convertView.getResources().getColor(color.colorRED));
            holder.calendaritem.setBackgroundColor(convertView.getResources().getColor(color.colorBase));
            notifyDataSetChanged(); }

        }


        int DayEventY = (mCal.get(Calendar.YEAR));
        int DayEventD = (mCal.get(Calendar.DAY_OF_MONTH));
        int DayEventM = (mCal.get(Calendar.MONTH)+ScheduleFragment.mCountK+1);
        String sDayEvent = String.valueOf(DayEventD);
        if(DayEventY == Integer.parseInt(Integer.toString(ScheduleFragment.mCountKK).substring(0,4))) {

                if (DayEventM == Integer.parseInt(Integer.toString(ScheduleFragment.mCountKK).substring(4, 6))) {
                        if((Integer.parseInt(Integer.toString(ScheduleFragment.mCountKK).substring(6, 7)))==0){
                            if ((Integer.toString(ScheduleFragment.mCountKK).substring(7, 8)).equals(getItem(position))) {
                                holder.calendaritem.setTextColor(convertView.getResources().getColor(color.colorWhiteGray));
                                holder.calendaritem.setBackgroundColor(convertView.getResources().getColor(R.color.colorBase));


                                notifyDataSetChanged();
                            }}
                        else {
                            if ((Integer.toString(ScheduleFragment.mCountKK).substring(6, 8)).equals(getItem(position))) {
                                holder.calendaritem.setTextColor(convertView.getResources().getColor(color.colorWhiteGray));
                                holder.calendaritem.setBackgroundColor(convertView.getResources().getColor(R.color.colorBase));


                                notifyDataSetChanged();
                            }
                        }
                }
            }
//        mCal.set(2019,8,23);
/*
        String sPlan = String.valueOf("3");
        if(sPlan.equals(getItem(position))){
            holder.calendaritem.setTextColor(convertView.getResources().getColor(R.color.colorBlueDark));
            holder.calendaritem.setBackgroundColor(convertView.getResources().getColor(R.color.colorGray));
            notifyDataSetChanged();

        }
*/
        // String planDay = String.valueOf();
        return convertView;


    }
/*
    @Override
    public void pluseMonthCount(int count) {
        mCountK++;
    }

    @Override
    public void minusMonthCount(int count) {
        mCountK--;
    }

*/
    private class ViewHolder{
        TextView calendaritem;

    }
    private int Itemtt(int iz){
        return iz;
    }

   /* private int getFirstDay(int  dayOfWeek){
        int result = 0 ;
        if(dayOfWeek == Calendar.SUNDAY){
            result ==
        }

        return result;
  */



}
