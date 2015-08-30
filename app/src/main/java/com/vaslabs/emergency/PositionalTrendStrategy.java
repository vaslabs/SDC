package com.vaslabs.emergency;

import com.vaslabs.sdc.types.DifferentiableFloat;
import com.vaslabs.sdc.types.TrendPoint;
import com.vaslabs.sdc.utils.AbstractTrendStrategy;
import com.vaslabs.sdc.utils.Position;

import java.util.List;

/**
 * Created by vnicolaou on 30/08/15.
 */
public class PositionalTrendStrategy<T> extends AbstractTrendStrategy<Position> {

    public PositionalTrendStrategy(double accuracy, double density, int historySize) {
        super(accuracy, density, historySize);
    }

    @Override
    protected void applyFilters() {

    }

    @Override
    public void getValueAt(Position point) {

    }

    @Override
    public List<TrendPoint> getNormalisedTrend() {
        return null;
    }
}
