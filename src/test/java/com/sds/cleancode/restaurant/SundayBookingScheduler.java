package com.sds.cleancode.restaurant;

import org.joda.time.DateTime;

public class SundayBookingScheduler extends BookingScheduler {

   private static final DateTime SUNDAY= new DateTime(2019, 10, 20, 10, 0);

   public SundayBookingScheduler(int capacityPerHour) {
	super(capacityPerHour);
   }

   @Override
   public DateTime getNow() {
      return SUNDAY;
   }
}
