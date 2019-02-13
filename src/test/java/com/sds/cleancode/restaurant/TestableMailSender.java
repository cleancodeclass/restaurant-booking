package com.sds.cleancode.restaurant;

public class TestableMailSender extends MailSender {

	private boolean isSendMailMethodCalled = false;

	@Override
	public void sendMail(Schedule schedule) {
		super.sendMail(schedule);
		
		isSendMailMethodCalled  = true;
	}

	public boolean isSendMailMethodCalled() {
		return isSendMailMethodCalled;
	}
}
