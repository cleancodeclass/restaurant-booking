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
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BookingSchedulerTestWithMock {

	private static final int CAPACITY_PER_HOUR = 3;
	private static final int NUMBER_OF_PEOPLE = 1;

	@Mock
	private Customer CUSTOMER_WITHOUT_EMAIL;

	@Mock(answer = Answers.RETURNS_MOCKS)
	private Customer CUSTOMER_WITH_EMAIL;

	private static final DateTime ON_THE_HOUR = new DateTime(2019, 2, 13, 9, 0);
	private static final DateTime NOT_ON_THE_HOUR = new DateTime(2019, 2, 13, 9, 10);

	@InjectMocks
	@Spy
	private BookingScheduler bookingScheduler = new BookingScheduler(CAPACITY_PER_HOUR);

	@Spy
	private List<Schedule> schedules = new ArrayList<Schedule>();

	@Spy
	private SmsSender smsSender = new SmsSender();

	@Spy
	private MailSender mailSender = new MailSender();

	@Before
	public void setup() {
		DateTime weekDay = new DateTime(2019, 2, 13, 0, 0);
		when(bookingScheduler.getNow()).thenReturn(weekDay);
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
		verify(mailSender).sendMail(any(Schedule.class));
	}

	@Test
	public void doNotSendEmailToCustomerWithoutEmailWhenScheduleIsAdded() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(schedule);

		// then
		verify(mailSender, times(0)).sendMail(any(Schedule.class));
	}

	@Test
	public void sendSmsToCustomerWhenScheduleIsAdded() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(schedule);

		// then
		verify(smsSender, times(1)).send(any(Schedule.class));
	}

	@Test
	public void throwExceptionIsOnSunday() {
		// given
		DateTime sunday = new DateTime(2019, 2, 17, 0, 0);
		when(bookingScheduler.getNow()).thenReturn(sunday);

		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		try {
			// when
			bookingScheduler.addSchedule(schedule);
			fail();
		} catch (RuntimeException e) {
			// then
			assertThat(e.getMessage(), is("Booking system is not available on sunday"));
		}
	}

	@Test
	public void sendSmsTwiceWhenscheduleIsAddedTwice() {
		// given
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);
		bookingScheduler.addSchedule(schedule);

		Schedule newSchedule = new Schedule(ON_THE_HOUR.plusHours(1), NUMBER_OF_PEOPLE, CUSTOMER_WITHOUT_EMAIL);

		// when
		bookingScheduler.addSchedule(newSchedule);

		// then
		verify(smsSender, times(2)).send(any(Schedule.class));
		// assertThat(bookingScheduler.hasSchedule(newSchedule), is(true));
	}
}
