package com.sds.cleancode.restaurant;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class BookingSchedulerTest {

	private static final int MAX_CAPACITY = 3;
	private static final int NUMBER_OF_PEOPLE = 3;
	private static final DateTime NOT_ON_THE_HOUR = new DateTime(2019, 10, 16, 11, 5);
	private static final Customer CUSTOMER = new Customer("Yu", "010-1234-5678");
	private static final DateTime ON_THE_HOUR = new DateTime(2019, 10, 16, 11, 0);
	private static final DateTime ANOTHER_TIME = new DateTime(2019, 10, 16, 12, 0);
	private static final Customer CUSTOMER_WITH_EMAIL = new Customer("Yu", "010-1234-5678", "test@test.com");
	private BookingScheduler bookingScheduler = new BookingScheduler(MAX_CAPACITY);
	private TestableSmsSender testableSmsSender = new TestableSmsSender();
	private TestableMailSender testableMailSender = new TestableMailSender();
	
	@Before
	public void setUp() {
		bookingScheduler.setMailSender(testableMailSender);
		bookingScheduler.setSmsSender(testableSmsSender);
	}
	
	@Test(expected = RuntimeException.class)
	public void 정시에_예약하지_않으면_예외발생() {

		// arrange
		Schedule schedule = new Schedule(NOT_ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER);

		// act
		bookingScheduler.addSchedule(schedule);
		fail();
		// assert

	}

	@Test
	public void 정시에_예약할_경우_예약성공() {

		// arrange
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER);

		// act
		bookingScheduler.addSchedule(schedule);

		// assert
		assertThat(bookingScheduler.hasSchedule(schedule), is(true));
	}

	@Test(expected = RuntimeException.class)
	public void 같은_시간대에_인원초과가_발생할경우_예외발생() {

		// arrange
		Schedule fullSchedule = new Schedule(ON_THE_HOUR, MAX_CAPACITY, CUSTOMER);
		Schedule newSchedule = new Schedule(ON_THE_HOUR, MAX_CAPACITY, CUSTOMER);
		bookingScheduler.addSchedule(fullSchedule);

		// act
		bookingScheduler.addSchedule(newSchedule);
		fail();
	}

	@Test
	public void 같은_시간대에_인원초과가_발생할경우_예외발생_Try_Catch사용() {

		// arrange
		Schedule fullSchedule = new Schedule(ON_THE_HOUR, MAX_CAPACITY, CUSTOMER);
		Schedule newSchedule = new Schedule(ON_THE_HOUR, MAX_CAPACITY, CUSTOMER);
		bookingScheduler.addSchedule(fullSchedule);

		// act
		try {
			bookingScheduler.addSchedule(newSchedule);
			fail();
		} catch (RuntimeException e) {
			assertThat(e.getMessage(), is("Number of people is over restaurant capacity per hour"));
		}
	}

	@Test
	public void 다른_시간대에_예약성공() {

		// arrange
		Schedule schedule = new Schedule(ON_THE_HOUR, MAX_CAPACITY, CUSTOMER);
		Schedule anotherSchedule = new Schedule(ANOTHER_TIME, MAX_CAPACITY, CUSTOMER);
		bookingScheduler.addSchedule(schedule);

		// act
		bookingScheduler.addSchedule(anotherSchedule);

		// assert
		assertThat(bookingScheduler.hasSchedule(schedule), is(true));
		assertThat(bookingScheduler.hasSchedule(anotherSchedule), is(true));
	}

	@Test
	public void 예약완료시_SMS는_무조건_발송() {

		// arrange
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER);
		
		// act
		bookingScheduler.addSchedule(schedule);

		// assert
		assertThat(testableSmsSender.isSmsSenderIsCalled(), is(true));
	}

	@Test
	public void 이메일이_있는_고객만_이메일_발송() {

		// arrange
		Schedule schedule = new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER_WITH_EMAIL);
		
		// act
		bookingScheduler.addSchedule(schedule);

		// assert
		assertThat(testableMailSender.isMailSenderIsCalled(), is(true));
	}
	
	@Test
	public void 일요일에_예약시도_할_경우_예외처리(){

	    // arrange
	    Schedule schedule= new Schedule(ON_THE_HOUR, NUMBER_OF_PEOPLE, CUSTOMER);
	    bookingScheduler= new SundayBookingScheduler(MAX_CAPACITY);

	    // act
	    try {
	        bookingScheduler.addSchedule(schedule);
	        fail();

	    } catch(RuntimeException e) {

	        // assert
	        assertThat(e.getMessage(), is("Booking system is not available on sunday"));
	    }
	}
}