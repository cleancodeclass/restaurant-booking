package com.sds.cleancode.restaurant;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class BookingSchedulerTest {

	private static final int CAPACITY_PER_HOUR = 3;
	private static final int NUMBER_OF_PEOPLE = 1;
	private static final Customer CUSTOMER_WITHOUT_EMAIL = new Customer("", "");
	private static final Customer CUSTOMER_WITH_EMAIL = new Customer("", "", "");
	private static final DateTime ON_THE_HOUR = new DateTime(2019, 2, 13, 9, 0);
	private static final DateTime NOT_ON_THE_HOUR = new DateTime(2019, 2, 13, 9, 10);
	
	private BookingScheduler bookingScheduler;
	
	private List<Schedule> schedules = new ArrayList<Schedule>(); 
	private TestableSmsSender smsSender = new TestableSmsSender();
	private TestableMailSender mailSender = new TestableMailSender(); 
	

	@Before
	public void setup() {
		bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);
		bookingScheduler.setSchedules(schedules);
		bookingScheduler.setSmsSender(smsSender);
		bookingScheduler.setMailSender(mailSender);
	}

	@Test
	public void isInstanceIsNotNullWhenBookingSchedulerWasCreated() {
		// when

		// then
		assertThat(bookingScheduler, is(notNullValue()));
	}

	@Test
	public void throwExceptionWhenBookingTimeIsNotOnTheHour() {
		// given
		Schedule schedule = new Schedule(NOT_ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		try {
			// when
			bookingScheduler.addSchedule(schedule);
			fail();
		} catch (RuntimeException e) {
			// then
			assertThat(e.getMessage(), is("Booking should be on the hour."));
		}
	}

	@Test
	public void scheduleIsAddedWhenBookingTimeIsOnTheHour() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(schedule);

		// then
		assertThat(bookingScheduler.hasSchedule(schedule), is(true));
	}
	
	@Test
	public void scheduleIsAddedWhenCapacityPerHourIsAvailble() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);
		bookingScheduler.addSchedule(schedule);
		
		Schedule newSchedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(newSchedule);

		// then
		assertThat(bookingScheduler.hasSchedule(newSchedule), is(true));
	}
	
	@Test
	public void scheduleIsAddedWhenBookingTimeIsOnTheOtherHour() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);
		bookingScheduler.addSchedule(schedule);
		
		Schedule newSchedule = new Schedule(ON_THE_HOUR.plusHours(1), NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(newSchedule);

		// then
		assertThat(bookingScheduler.hasSchedule(newSchedule), is(true));
	}
	
	@Test
	public void throwExceptionWhenCapacityPerHourIsOver() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, CAPACITY_PER_HOUR, CUSTOMER_WITHOUT_EMAIL);
		bookingScheduler.addSchedule(schedule);
		
		Schedule newSchedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		try {
			// when
			bookingScheduler.addSchedule(newSchedule);
			fail();
		} catch (RuntimeException e) {
			// then
			assertThat(e.getMessage(), is("Number of people is over restaurant capacity per hour"));
		}
	}
	
	@Test
	public void sendEmailToCustomerWithEmailWhenScheduleIsAdded() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITH_EMAIL);

		// when
		bookingScheduler.addSchedule(schedule);

		// then
		assertThat(mailSender.isSendMailMethodCalled(), is(true));
	}
	
	@Test
	public void doNotSendEmailToCustomerWithoutEmailWhenScheduleIsAdded() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(schedule);

		// then
		assertThat(mailSender.isSendMailMethodCalled(), is(false));
	}
	
	@Test
	public void sendSmsToCustomerWhenScheduleIsAdded() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(schedule);

		// then
		assertThat(smsSender.isSendMethodCalled(), is(true));
	}
}
