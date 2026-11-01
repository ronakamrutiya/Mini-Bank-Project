package com.minibank.service;

import com.minibank.exception.InvalidAmountException;
import com.minibank.model.Account;

public class AccountWorker implements Runnable {
    private final Account account; private final int times; private final long amount;
    public AccountWorker(Account account, int times, long amount) { this.account = account; this.times = times; this.amount = amount; }
    @Override public void run() { System.out.println(Thread.currentThread().getName() + " started"); for (int i=0; i<times; i++) try { account.deposit(amount); } catch (InvalidAmountException e) { throw new IllegalStateException(e); } System.out.println(Thread.currentThread().getName() + " finished"); }
}
