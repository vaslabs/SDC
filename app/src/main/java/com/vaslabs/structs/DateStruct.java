package com.vaslabs.structs;

import java.util.Calendar;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class DateStruct {
    public final int year;
    public final int month;
    public final int day;


    public DateStruct(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public DateStruct(Calendar cal) {
        this(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DateStruct that = (DateStruct) o;

        if (year != that.year) return false;
        if (month != that.month) return false;
        return day == that.day;

    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + day;
        return result;
    }
}
