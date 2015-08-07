package com.vaslabs.sdc.units;


public enum TimeUnit {

    MILLISECONDS("ms") {
        public double toMillis(double d)  { return d; }
        public double toSeconds(double d) { return d/(C3/C2); }
        public double toMinutes(double d) { return d/(C4/C2); }
        public double toHours(double d)   { return d/(C5/C2); }
        public double toDays(double d)    { return d/(C6/C2); }
        public double convert(double d, TimeUnit u) { return u.toMillis(d); }
    },
    SECONDS("s") {
        public double toMillis(double d)  { return x(d, C3/C2, MAX/(C3/C2)); }
        public double toSeconds(double d) { return d; }
        public double toMinutes(double d) { return d/(C4/C3); }
        public double toHours(double d)   { return d/(C5/C3); }
        public double toDays(double d)    { return d/(C6/C3); }
        public double convert(double d, TimeUnit u) { return u.toSeconds(d); }
    },
    MINUTES("m") {
        public double toMillis(double d)  { return x(d, C4/C2, MAX/(C4/C2)); }
        public double toSeconds(double d) { return x(d, C4/C3, MAX/(C4/C3)); }
        public double toMinutes(double d) { return d; }
        public double toHours(double d)   { return d/(C5/C4); }
        public double toDays(double d)    { return d/(C6/C4); }
        public double convert(double d, TimeUnit u) { return u.toMinutes(d); }
    },
    HOURS("h") {
        public double toMillis(double d)  { return x(d, C5/C2, MAX/(C5/C2)); }
        public double toSeconds(double d) { return x(d, C5/C3, MAX/(C5/C3)); }
        public double toMinutes(double d) { return x(d, C5/C4, MAX/(C5/C4)); }
        public double toHours(double d)   { return d; }
        public double toDays(double d)    { return d/(C6/C5); }
        public double convert(double d, TimeUnit u) { return u.toHours(d); }
    },
    DAYS("days") {
        public double toMillis(double d)  { return x(d, C6/C2, MAX/(C6/C2)); }
        public double toSeconds(double d) { return x(d, C6/C3, MAX/(C6/C3)); }
        public double toMinutes(double d) { return x(d, C6/C4, MAX/(C6/C4)); }
        public double toHours(double d)   { return x(d, C6/C5, MAX/(C6/C5)); }
        public double toDays(double d)    { return d; }
        public double convert(double d, TimeUnit u) { return u.toDays(d); }
    };

    // Handy constants for conversion methods
    static final double C2 = 1;
    static final double C3 = C2 * 1000;
    static final double C4 = C3 * 60;
    static final double C5 = C4 * 60;
    static final double C6 = C5 * 24;

    static final double MAX = Double.MAX_VALUE;
    public final String signature;

    TimeUnit(String signature) {
         this.signature = signature;
    }

    /**
     * Scale d by m, checking for overflow.
     * This has a short name to make above code more readable.
     */
    static double x(double d, double m, double over) {
        if (d >  over) return Double.MAX_VALUE;
        if (d < -over) return Double.MIN_VALUE;
        return d * m;
    }

    // To maintain full signature compatibility with 1.5, and to improve the
    // clarity of the generated javadoc (see 6287639: Abstract methods in
    // enum classes should not be listed as abstract), method convert
    // etc. are not declared abstract but otherwise act as abstract methods.

    /**
     * Convert the given time duration in the given unit to this
     * unit.  Conversions from finer to coarser granularities
     * truncate, so lose precision. For example converting
     * {@code 999} milliseconds to seconds results in
     * {@code 0}. Conversions from coarser to finer granularities
     * with arguments that would numerically overflow saturate to
     * {@code double.MIN_VALUE} if negative or {@code double.MAX_VALUE}
     * if positive.
     *
     * <p>For example, to convert 10 minutes to milliseconds, use:
     * {@code TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)}
     *
     * @param sourceDuration the time duration in the given {@code sourceUnit}
     * @param sourceUnit the unit of the {@code sourceDuration} argument
     * @return the converted duration in this unit,
     * or {@code double.MIN_VALUE} if conversion would negatively
     * overflow, or {@code double.MAX_VALUE} if it would positively overflow.
     */
    public double convert(double sourceDuration, TimeUnit sourceUnit) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to {@code MILLISECONDS.convert(duration, this)}.
     * @param duration the duration
     * @return the converted duration,
     * or {@code double.MIN_VALUE} if conversion would negatively
     * overflow, or {@code double.MAX_VALUE} if it would positively overflow.
     * @see #convert
     */
    public double toMillis(double duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to {@code SECONDS.convert(duration, this)}.
     * @param duration the duration
     * @return the converted duration,
     * or {@code double.MIN_VALUE} if conversion would negatively
     * overflow, or {@code double.MAX_VALUE} if it would positively overflow.
     * @see #convert
     */
    public double toSeconds(double duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to {@code MINUTES.convert(duration, this)}.
     * @param duration the duration
     * @return the converted duration,
     * or {@code double.MIN_VALUE} if conversion would negatively
     * overflow, or {@code double.MAX_VALUE} if it would positively overflow.
     * @see #convert
     * @since 1.6
     */
    public double toMinutes(double duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to {@code HOURS.convert(duration, this)}.
     * @param duration the duration
     * @return the converted duration,
     * or {@code double.MIN_VALUE} if conversion would negatively
     * overflow, or {@code double.MAX_VALUE} if it would positively overflow.
     * @see #convert
     * @since 1.6
     */
    public double toHours(double duration) {
        throw new AbstractMethodError();
    }

    /**
     * Equivalent to {@code DAYS.convert(duration, this)}.
     * @param duration the duration
     * @return the converted duration
     * @see #convert
     * @since 1.6
     */
    public double toDays(double duration) {
        throw new AbstractMethodError();
    }

/**
 * Utility to compute the excess-nanosecond argument to wait,
 * sleep, join.
 * @param d the duration
 * @param m the number of milliseconds
 * @return the number of nanoseconds
 */
}
