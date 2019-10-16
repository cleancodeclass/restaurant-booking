package com.sds.cleancode.restaurant;

public class TestableMailSender extends MailSender {

	private boolean mailSenderIsCalled;

	public TestableMailSender() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void sendMail(Schedule schedule) {
		this.mailSenderIsCalled = true;
	}

	public boolean isMailSenderIsCalled() {
		return mailSenderIsCalled;
	}
}
