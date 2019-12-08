package com.example.testcalendrier;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class action_event extends Fragment implements View.OnClickListener{
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
    String action;
    Event event = new Event();

    private int mYear, mMonth, mDay, mHour, mMinute;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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

        Bundle bundle = this.getArguments();

        if(bundle != null){
            action = bundle.getString("action");
            if(action.equals("add")){
                calID = bundle.getLong("calID");
            }else if(action.equals("update")){
                calID = bundle.getLong("calID");
                editTitle.setText(bundle.getString("titleEvent"));
                startMillis = bundle.getLong("startMillis");
                endMillis = bundle.getLong("endMillis");
                event = (Event) bundle.getSerializable("event");
                beginTime.setTimeInMillis(startMillis);
                endTime.setTimeInMillis(endMillis);
                editDateBegin.setText(beginTime.getTime().toString());
                editDateEnd.setText(endTime.getTime().toString());
            }

        }
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

                if(!hasErrors(event)){
                    if(action.equals("add")){
                        if(!hasErrors(event)){
                            addEvent(calID,beginTime,endTime,txt);
                            Toast.makeText(context, "Event added !", Toast.LENGTH_SHORT).show();
                        }

                    }else if(action.equals("update")){
                        event.setTitle(txt);
                        event.setDateBegin(beginTime.getTimeInMillis());
                        event.setDateEnd(endTime.getTimeInMillis());
                        updateEvent(event);

                    }

                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.activity_main, new calendar_fragment(), "Calendar_Fragment");
                    transaction.commit();
                }

                break;
        }


    }

    /* If there are errors, return true, else false */
    public boolean hasErrors(Event event){
        boolean hasError = false;

        if(editTitle.getText().toString().matches("")){
            editTitle.setError("You did not enter a title");
            editTitle.requestFocus();
            hasError = true;
        }
        else if(editDateBegin.getText().toString().matches("")){editDateBegin.setError("You did not enter a begin date");

            editDateBegin.setError("You did not enter a begin date");
            editDateBegin.requestFocus();
            hasError = true;

        }
        else if(editDateEnd.getText().toString().matches("")){
            editDateEnd.setError("You did not enter a ending date");
            editDateEnd.requestFocus();
            hasError = true;
        }
        else if(beginTime.compareTo(endTime) > 0){
            editDateBegin.setError("Begin date is after end date");
            editDateBegin.requestFocus();
            hasError = true;
        }
        return hasError;
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
        event.setDateBegin(startMillis);
        event.setCalendar_ID(calID);
        event.setTitle(title);
        event.setDateEnd(endMillis);
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
            cr.insert(CalendarContract.Events.CONTENT_URI, values);



    }

    public void updateEvent(Event event){
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;
        // The new title for the event
        values.put(CalendarContract.Events.TITLE, event.getTitle());
        values.put(CalendarContract.Events.DTSTART, event.getDateBegin());
        values.put(CalendarContract.Events.DTEND, event.getDateEnd());
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEvent_ID());
        cr.update(updateUri, values, null, null);
    }


}
