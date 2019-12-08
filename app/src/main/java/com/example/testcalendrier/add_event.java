package com.example.testcalendrier;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class add_event extends Fragment implements View.OnClickListener{
    Context context;
    Calendar beginTime = Calendar.getInstance();
    Calendar endTime=Calendar.getInstance();
    EditText editTitle;
    EditText editDateBegin;
    EditText editDateEnd;

    Button btn_select_dateBegin;
    Button btn_select_dateEnd;
    Button btn_validate;

    long calID;
    long startMillis = 0;
    long endMillis = 0;

    int ihourOfDay;
    int iminute ;
    int iyear ;
    int imonthOfYear;
    int idayOfMonth;


    private int mYear, mMonth, mDay, mHour, mMinute;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if(bundle != null){
            calID = bundle.getLong("calID");
        }
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        context = v.getContext();
        editTitle = (EditText) v.findViewById(R.id.editTitle);
        editDateBegin = (EditText) v.findViewById(R.id.editDateBegin);
        editDateEnd = (EditText) v.findViewById(R.id.editDateEnd);

        btn_select_dateBegin = (Button) v.findViewById(R.id.btn_select_dateBegin);
        btn_select_dateEnd = (Button) v.findViewById(R.id.btn_select_dateEnd);
        btn_validate = (Button) v.findViewById(R.id.btn_validate);

        btn_select_dateBegin.setOnClickListener(this);
        btn_select_dateEnd.setOnClickListener(this);
        btn_validate.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_dateBegin:
                showdiag(beginTime,editDateBegin);
               // editDateBegin.setText(beginTime.getTime() +"");
                break;
            case R.id.btn_select_dateEnd:
                showdiag(endTime,editDateEnd);
               // editDateEnd.setText(endTime.getTime() +"");
                break;
            case R.id.btn_validate:
                String txt = "";
                txt = editTitle.getText().toString();
                addEvent(calID,beginTime,endTime,txt);
                break;
        }


        }


public void showdiag(final Calendar calendar, final EditText edit){
    final Calendar c = Calendar.getInstance();
    mYear = c.get(Calendar.YEAR);
    mMonth = c.get(Calendar.MONTH);
    mDay = c.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog datePickerDialog = new DatePickerDialog(context,
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {

                    iyear = year;
                    imonthOfYear = monthOfYear;
                    idayOfMonth = dayOfMonth;
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    ihourOfDay = hourOfDay;
                                    iminute = minute;
                                    calendar.set(iyear, imonthOfYear, idayOfMonth, ihourOfDay, iminute);
                                    edit.setText(calendar.getTime() +"");

                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                                   }
            }, mYear, mMonth, mDay);
    datePickerDialog.show();



}
    public void addEvent(long calID,Calendar begin,Calendar end, String title) {



        startMillis = begin.getTimeInMillis();
        endMillis = end.getTimeInMillis();
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, "TEST");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            return;
        }
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        long eventID = Long.parseLong(uri.getLastPathSegment());
        Log.d("LALLAALLALALA",""+eventID);
    }


}
