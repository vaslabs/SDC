package com.vaslabs.sdc.types;

import com.vaslabs.units.DistanceUnit;
import com.vaslabs.units.TimeUnit;

/**
 * Created by vnicolaou on 20/12/15.
 */
public interface ILogbookSummaryEntry {
    String getTitle();
    String getContent();
    void switchPreferredDistanceUnit(DistanceUnit distanceUnit);
    void switchPreferredTimeUnit(TimeUnit timeUnit);
}
