package com.example.emma_baumstarck.newyorktimes.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.example.emma_baumstarck.newyorktimes.DatePickerFragment;
import com.example.emma_baumstarck.newyorktimes.R;
import com.example.emma_baumstarck.newyorktimes.SearchOptions;

import butterknife.BindView;

/**
 * Created by emma_baumstarck on 8/12/16.
 */
public class SearchOptionsFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    @BindView(R.id.sortOrderSpinner) Spinner spinner;
    @BindView(R.id.checkBoxArts) Button startDateButton;
    @BindView(R.id.saveButton) Button saveButton;
    @BindView(R.id.checkBoxArts) CheckBox checkBoxArts;
    @BindView(R.id.checkBoxFashionStyle) CheckBox checkBoxFashionStyle;
    @BindView(R.id.checkBoxSports) CheckBox checkBoxSports;

    SearchOptions searchOptions;


    public static SearchOptionsFragment newInstance(SearchOptions searchOptions) {
        SearchOptionsFragment fragment = new SearchOptionsFragment();
        fragment.searchOptions = searchOptions;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Search Options");
        return inflater.inflate(R.layout.search_options, container);
    }

    private void setDateButtonText() {
        if (searchOptions.year <= 0) {
            startDateButton.setText("N/A");
        } else {
            startDateButton.setText(
                    (searchOptions.monthOfYear + 1) + "/" + searchOptions.dayOfMonth + "/" + (searchOptions.year % 100));
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this.getContext(),
                R.array.sort_order_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(searchOptions.oldest ? 1 : 0, false);
        checkBoxArts.setChecked(searchOptions.searchArts);
        checkBoxFashionStyle.setChecked(searchOptions.searchFashionStyle);
        checkBoxSports.setChecked(searchOptions.searchSports);

        final DatePickerDialog.OnDateSetListener listener = this;

        setDateButtonText();
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.show(getChildFragmentManager(), "datePicker");
                newFragment.setOnDateListener(listener);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOptions.oldest = spinner.getSelectedItem().toString().equals("Oldest");
                searchOptions.searchArts = checkBoxArts.isChecked();
                searchOptions.searchFashionStyle = checkBoxFashionStyle.isChecked();
                searchOptions.searchSports = checkBoxSports.isChecked();
                dismiss();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        searchOptions.year = year;
        searchOptions.monthOfYear = monthOfYear;
        searchOptions.dayOfMonth = dayOfMonth;
        setDateButtonText();
    }
}
