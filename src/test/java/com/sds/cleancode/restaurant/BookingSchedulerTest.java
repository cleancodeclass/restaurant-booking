package com.sds.cleancode.restaurant;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class BookingSchedulerTest {

	@Test(expected=RuntimeException.class)
	public void 정시에_예약하지_않으면_예외발생() {
	
		// arrange
		BookingScheduler BookingScheduler= new BookingScheduler(3);
		DateTime dateTime= new DateTime(2019, 10, 16, 11, 5);
		int numberOfPeple= 3;
		Customer customer= new Customer("Yu", "010-1234-5678");
		Schedule schedule= new Schedule(dateTime, numberOfPeple, customer);
		
		// act
		BookingScheduler.addSchedule(schedule);
		
		// assert

	}
}
