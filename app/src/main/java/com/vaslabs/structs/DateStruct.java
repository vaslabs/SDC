package com.vaslabs.structs;

import java.util.Calendar;

/**
 * Created by vnicolao on 04/07/15.
 */
public final class DateStruct implements Comparable<DateStruct> {
    public final int year;
    public final int month;
    public final int day;
    public final int sessionRef;

    public DateStruct(int year, int month, int day) {
        this(year, month, day, 0);
    }

    public DateStruct(int year, int month, int day, int sessionRef) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.sessionRef = sessionRef;
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
        if (day != that.day) return false;
        return sessionRef == that.sessionRef;

    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + day;
        result = 31 * result + sessionRef;
        return result;
    }

    @Override
    public int compareTo(DateStruct dateStruct) {
        if (dateStruct == null)
            return 1;
        if (this.year > dateStruct.year)
            return 1;
        else if (this.year < dateStruct.year)
            return -1;

        if (this.month > dateStruct.month)
            return 1;
        else if (this.month < dateStruct.month)
            return -1;

        if (this.day > dateStruct.day)
            return 1;
        else if (this.day < dateStruct.day)
            return -1;
        if (this.sessionRef > dateStruct.sessionRef)
            return 1;
        else if (this.sessionRef < dateStruct.sessionRef)
            return -1;

        return 0;
    }
}
