package com.minibank.model;

public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long minBalance;
    public SavingsAccount(String owner, long openingBalance, long minBalance) { super(owner, openingBalance); if (minBalance < 0 || openingBalance < minBalance) throw new IllegalArgumentException("Opening balance must meet the minimum balance"); this.minBalance = minBalance; }
    @Override public double interestRate() { return 4.0; }
    @Override public boolean canWithdraw(long amount) { return getBalance() - amount >= minBalance; }
    public long getMinBalance() { return minBalance; }
}
