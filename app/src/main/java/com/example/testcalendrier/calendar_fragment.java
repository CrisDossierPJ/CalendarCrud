package com.example.testcalendrier;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class calendar_fragment extends Fragment implements View.OnClickListener{
    Context context;
    Button show;
    Button add;
    Cursor cursor;
    ListView listView;
    EditText input;
    EditText edittext;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calendar_fragment, container, false);


        show = (Button) v.findViewById(R.id.show);
        add = (Button) v.findViewById(R.id.add);
        listView = (ListView) v.findViewById(R.id.listview);
        edittext = (EditText) v.findViewById(R.id.editText2);

        context = v.getContext();

        show.setOnClickListener(this);
        add.setOnClickListener(this);

        return v;
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.show:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("hi", "HELLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
                    return;
                }
                cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, null, null, null);
                if (cursor.moveToNext() == false) {
                    Log.d("hi", "à events");
                }
                //getCalendars();
                readEvents(Integer.parseInt(edittext.getText().toString()));

                break;
            case R.id.add:
                add_event addevent = new add_event();
                Bundle bundle = new Bundle();
                bundle.putLong("calID",Integer.parseInt(edittext.getText().toString()));
                addevent.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.activity_main, addevent);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                //addEvent(Integer.parseInt(edittext.getText().toString()));
        }
    }


    /*public void getCalendars() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_CALENDAR}, 7);
            return;
        }

        // Projection array. Creating indices for this array instead of doing dynamic lookups improves performance.
        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        // The indices for the projection array above.
        final int PROJECTION_ID_INDEX = 0;
        final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
        final int PROJECTION_DISPLAY_NAME_INDEX = 2;
        final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;


        ContentResolver contentResolver = context.getContentResolver();
        Cursor cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null);

        ArrayList<String> calendarInfos = new ArrayList<>();
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            String calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName);
            calendarInfos.add(calendarInfo);
        }

        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, calendarInfos);
        listView.setAdapter(stringArrayAdapter);
    }*/

    public void readEvents(long calID) {

        String[] mProjection =
                {
                        "_id",
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                };
        final int ID_EVENT = 0;
        final int EVENT_TITLE = 1;
        final int EVENT_LOCATION = 2;
        final int DTSART = 3;
        final int DEND = 4;
        // Submit the query
        Uri uri = CalendarContract.Events.CONTENT_URI;
        String selection = CalendarContract.Instances.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[]{"" + calID};
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR}, MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
            return;
        }
        Cursor cur = context.getContentResolver().query(uri, mProjection, selection, selectionArgs, null);


        ArrayList<Event> events = new ArrayList<>();
        while (cur.moveToNext()) {

            // Get the field values
            long eventID = cur.getLong(ID_EVENT);
            long beginVal = cur.getLong(DTSART);
            String title = cur.getString(EVENT_TITLE);
            String location = cur.getString(EVENT_LOCATION);

            Event event = new Event(eventID, calID, title, location, beginVal, beginVal);
            Toast.makeText(context, "EVENT_TITLE", Toast.LENGTH_SHORT).show();
            events.add(event);
        }

        // ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, events);
        MyCustomAdapter adap = new MyCustomAdapter(events, context, calendar_fragment.this);
        listView.setAdapter(adap);
    }



    public void updateEventDialog(final Event event){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Personal Details");
        builder.setIcon(R.drawable.ic_launcher_background);
        builder.setMessage("Modifier evénements");
        input = new EditText(context);
        input.setText(event.getTitle());
        builder.setView(input);
        builder.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String txt = input.getText().toString();
                updateEvent(event.getEvent_ID(),txt);
                readEvents(event.getCalendar_ID());
                //Toast.makeText()
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog ad = builder.create();
        ad.show();
    }

    public void updateEvent(long eventID, String title){
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;
        // The new title for the event
        values.put(CalendarContract.Events.TITLE, title);
        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = cr.update(updateUri, values, null, null);
    }

    public void deleteEvent(long eventID){
        ContentResolver cr = context.getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        int rows = cr.delete(deleteUri, null, null);
    }

}
