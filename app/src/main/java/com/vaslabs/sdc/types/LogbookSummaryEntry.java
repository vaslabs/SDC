package com.vaslabs.sdc.types;

import android.content.Context;

import com.vaslabs.logbook.LogbookSummary;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.units.DistanceUnit;
import com.vaslabs.units.TimeUnit;
import com.vaslabs.units.composite.VelocityUnit;

/**
 * Created by vnicolaou on 20/12/15.
 */
public abstract class LogbookSummaryEntry implements ILogbookSummaryEntry {
    private final String title;

    private LogbookSummaryEntry(String title) {
        this.title = title;
    }

    public static LogbookSummaryEntry[] fromLogbookSummary(LogbookSummary logbookSummary, Context context) {
        LogbookSummaryEntry[] logbookSummaryEntries = new LogbookSummaryEntry[4];
        logbookSummaryEntries[0] = new LogbookSummaryEntryNumerical(logbookSummary.getNumberOfJumps(),
                context.getString(R.string.number_of_dives_title));
        logbookSummaryEntries[1] = new LogbookSummaryEntryDistance(logbookSummary.getAverageDeployAltitude(),
                context.getString(R.string.average_deploy_altitude));
        logbookSummaryEntries[2] = new LogbookSummaryEntryDistance(logbookSummary.getAverageExitAltitude(),
                context.getString(R.string.average_exit_altitude));
        logbookSummaryEntries[3] = new LogbookSummaryEntryVelocity(logbookSummary.getAverageTopSpeed(),
                context.getString(R.string.average_top_speed));
        return logbookSummaryEntries;
    }

    @Override
    public String getTitle() {
        return title;
    }

    private static class LogbookSummaryEntryDistance extends LogbookSummaryEntry {
        private final float value;
        private final DistanceUnit myUnit = DistanceUnit.METERS;
        private DistanceUnit preferredUnit = DistanceUnit.METERS;

        private LogbookSummaryEntryDistance(float value, String title) {
            super(title);
            this.value = value;
        }

        @Override
        public String getContent() {
            return preferredUnit.toString(preferredUnit.convert(myUnit, value));
        }

        @Override
        public void switchPreferredDistanceUnit(DistanceUnit distanceUnit) {
            preferredUnit = distanceUnit;
        }

        @Override
        public void switchPreferredTimeUnit(TimeUnit timeUnit) {

        }
    }

    private static class LogbookSummaryEntryVelocity extends LogbookSummaryEntry {
        private final TimeUnit myTimeUnit = TimeUnit.SECONDS;
        private TimeUnit preferredTimeUnit = TimeUnit.SECONDS;
        private final DistanceUnit myDistanceUnit = DistanceUnit.METERS;
        private DistanceUnit preferredDistanceUnit = DistanceUnit.METERS;

        private final VelocityUnit velocityUnit;

        private LogbookSummaryEntryVelocity(float value, String title) {
            super(title);
            velocityUnit = new VelocityUnit(myDistanceUnit, myTimeUnit, value);
        }

        @Override
        public String getContent() {
            return velocityUnit.convert(preferredDistanceUnit, preferredTimeUnit).toString();
        }

        @Override
        public void switchPreferredDistanceUnit(DistanceUnit distanceUnit) {
            preferredDistanceUnit = distanceUnit;
        }

        @Override
        public void switchPreferredTimeUnit(TimeUnit timeUnit) {
            this.preferredTimeUnit = timeUnit;
        }
    }

    private class LogbookSummaryEntryTime extends LogbookSummaryEntry {
        private final float value;
        private final TimeUnit myUnit = TimeUnit.SECONDS;
        private TimeUnit preferredUnit = TimeUnit.SECONDS;


        private LogbookSummaryEntryTime(float value, String title) {
            super(title);
            this.value = value;
        }

        @Override
        public String getContent() {
            return preferredUnit.convert(value, myUnit) + preferredUnit.signature;
        }

        @Override
        public void switchPreferredDistanceUnit(DistanceUnit distanceUnit) {
        }

        @Override
        public void switchPreferredTimeUnit(TimeUnit timeUnit) {
            this.preferredUnit = timeUnit;
        }
    }

    private static class LogbookSummaryEntryNumerical extends LogbookSummaryEntry {
        private final int value;

        private LogbookSummaryEntryNumerical(int value, String title) {
            super(title);
            this.value = value;
        }

        @Override
        public String getContent() {
            return "#" + value;
        }

        @Override
        public void switchPreferredDistanceUnit(DistanceUnit distanceUnit) {
        }

        @Override
        public void switchPreferredTimeUnit(TimeUnit timeUnit) {

        }
    }

}
