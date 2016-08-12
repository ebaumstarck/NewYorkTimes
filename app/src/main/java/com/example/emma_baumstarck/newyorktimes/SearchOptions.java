package com.example.emma_baumstarck.newyorktimes;

import org.parceler.Parcel;

/**
 * Created by emma_baumstarck on 8/12/16.
 */
@Parcel
public class SearchOptions {
    public int year;
    public int monthOfYear;
    public int dayOfMonth;
    public boolean oldest;
    public boolean searchArts;
    public boolean searchFashionStyle;
    public boolean searchSports;

    public SearchOptions() {}
}
