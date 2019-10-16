package com.sds.cleancode.restaurant;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class BookingSchedulerTest {

	private static final int MAX_CAPACITY = 3;
	private static final int NUMBER_OF_PEOPLE = 3;
	private static final DateTime NOT_ON_THE_HOUR= new DateTime(2019, 10, 16, 11, 5);
	private static final Customer CUSTOMER= new Customer("Yu", "010-1234-5678");
	private static final DateTime ON_THE_HOUR= new DateTime(2019, 10, 16, 11, 0);
	private BookingScheduler bookingScheduler= new BookingScheduler(MAX_CAPACITY);
	
	@Test(expected=RuntimeException.class)
	public void 정시에_예약하지_않으면_예외발생() {
	
		// arrange
		Schedule schedule= new Schedule(NOT_ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER);
		
		// act
		bookingScheduler.addSchedule(schedule);
		
		// assert

	}
	
	@Test
	public void 정시에_예약할_경우_예약성공() {
	
		// arrange
		Schedule schedule= new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER);
		
		// act
		bookingScheduler.addSchedule(schedule);
		
		// assert
		assertThat(bookingScheduler.hasSchedule(schedule), is(true));
	}
}
