package com.vaslabs.emergency;

import com.vaslabs.sdc.utils.AbstractTrendStrategy;

/**
 * Created by vnicolaou on 30/08/15.
 */
public interface StrategyVisitor {
    void visit(AbstractTrendStrategy trendStrategy);
}
