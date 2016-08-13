package com.example.emma_baumstarck.newyorktimes;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import org.parceler.Parcels;

import java.util.Calendar;

/**
 * Created by emma_baumstarck on 8/12/16.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatePickerDialog.OnDateSetListener listener;

    public static final String SEARCH_OPTIONS_KEY = "searchOptions";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        SearchOptions searchOptions = Parcels.unwrap(getArguments().getParcelable(SEARCH_OPTIONS_KEY));
        if (searchOptions.year > 0) {
            // use search options date if set
            year = searchOptions.year;
            month = searchOptions.monthOfYear;
            day = searchOptions.dayOfMonth;
        } else {
            // use current date as default
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setOnDateListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        listener.onDateSet(datePicker, i, i1, i2);
    }
}

