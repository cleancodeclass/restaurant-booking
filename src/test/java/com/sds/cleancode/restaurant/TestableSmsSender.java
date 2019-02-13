package com.sds.cleancode.restaurant;

public class TestableSmsSender extends SmsSender {
	
	private boolean isSendMethodCalled;

	@Override
	public void send(Schedule schedule) {
		super.send(schedule);
		
		isSendMethodCalled = true;
	}

	public boolean isSendMethodCalled() {
		return isSendMethodCalled;
	}
}
