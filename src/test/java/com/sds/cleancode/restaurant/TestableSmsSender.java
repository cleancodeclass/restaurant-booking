package com.sds.cleancode.restaurant;

public class TestableSmsSender extends SmsSender {

	private boolean smsSenderIsCalled;

	public TestableSmsSender() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void send(Schedule schedule) {
		this.smsSenderIsCalled = true;
	}

	public boolean isSmsSenderIsCalled() {
		return smsSenderIsCalled;
	}
}
