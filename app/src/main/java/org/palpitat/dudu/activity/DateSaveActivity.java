package org.palpitat.dudu.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.palpitat.dudu.R;

import java.util.Calendar;

public class DateSaveActivity  extends AppCompatActivity {

    private TextView WriteMonth;
    private Calendar cal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datesave);
        WriteMonth =  findViewById(R.id.writeyear);
        WriteMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),WriteMonth.getText(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDate() {
        DatePickerDialog dialog = new DatePickerDialog(DateSaveActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {

                String msg = String.format("%d 년 %d ex월 %d 일", year, month + 1, date);
                Toast.makeText(DateSaveActivity.this, msg, Toast.LENGTH_SHORT).show();
//aaaaaa
            }
//aaaaaa
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
    }
}



