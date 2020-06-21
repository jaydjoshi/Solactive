package com.solactive.app.aggregator;

import com.solactive.app.model.ImmutableTick;
import com.solactive.app.model.Statistics;

public interface ITickerAggregator {

	Statistics getStatistics(long currentTime);

	int addAndUpdateStatistics(ImmutableTick immTick, long currentTime);

}
