package com.sds.cleancode.restaurant;

import org.joda.time.DateTime;

public class TestableBookingScheduler extends BookingScheduler {

	public TestableBookingScheduler(int capacityPerHour) {
		super(capacityPerHour);
	}
	
	@Override
	public DateTime getNow() {
		DateTime now = new DateTime(2019, 2, 17, 0, 0);
		return now;
	}
}
