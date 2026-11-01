package com.minibank.model;

public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;
    public CurrentAccount(String owner, long openingBalance, long overdraftLimit) { super(owner, openingBalance); if (overdraftLimit < 0) throw new IllegalArgumentException("Overdraft cannot be negative"); this.overdraftLimit = overdraftLimit; }
    @Override public double interestRate() { return 0.0; }
    @Override public boolean canWithdraw(long amount) { return getBalance() - amount >= -overdraftLimit; }
    public long getOverdraftLimit() { return overdraftLimit; }
}
