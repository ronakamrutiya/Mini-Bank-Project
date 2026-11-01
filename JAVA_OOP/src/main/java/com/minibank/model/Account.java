package com.minibank.model;

import com.minibank.exception.InsufficientFundsException;
import com.minibank.exception.InvalidAmountException;
import com.minibank.model.annotation.Id;
import com.minibank.model.annotation.Positive;
import java.io.Serializable;
import java.util.Objects;

public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;
    private static long accountCounter;
    @Id private final String accountNumber;
    private final String ownerName;
    @Positive private long balance;
    private boolean active = true;

    protected Account(String ownerName, long openingBalance) {
        if (openingBalance < 0) throw new IllegalArgumentException("Opening balance cannot be negative");
        this.accountNumber = generateAccountNumber(); this.ownerName = Objects.requireNonNull(ownerName); this.balance = openingBalance;
    }
    private static synchronized String generateAccountNumber() { return "AC" + String.format("%06d", ++accountCounter); }
    public abstract boolean canWithdraw(long amount);
    @Override public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Deposit must be greater than zero");
        if (!active) throw new InvalidAmountException("Account is closed");
        try { balance = Math.addExact(balance, amount); } catch (ArithmeticException e) { throw new InvalidAmountException("Amount exceeds supported balance"); }
    }
    @Override public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Withdrawal must be greater than zero");
        if (!active) throw new InvalidAmountException("Account is closed");
        if (!canWithdraw(amount)) throw new InsufficientFundsException("Withdrawal exceeds account limit", Math.max(0, amount - Math.max(0, balance)));
        balance -= amount; return true;
    }
    public void transfer(Account to, long amount) throws com.minibank.exception.BankException {
        Objects.requireNonNull(to); if (to == this) throw new InvalidAmountException("Cannot transfer to the same account");
        Account first = compareTo(to) < 0 ? this : to, second = first == this ? to : this;
        synchronized (first) { synchronized (second) {
            if (amount <= 0) throw new InvalidAmountException("Transfer must be greater than zero");
            if (to.balance > Long.MAX_VALUE - amount) throw new InvalidAmountException("Recipient balance would exceed supported range");
            withdraw(amount); to.deposit(amount);
        } }
    }
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        String suffix = accountNumber.substring(2);
        try { long restored = Long.parseLong(suffix); synchronized (Account.class) { accountCounter = Math.max(accountCounter, restored); } }
        catch (NumberFormatException ignored) { }
    }
    public synchronized long getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public synchronized boolean isActive() { return active; }
    public synchronized void close() { active = false; }
    @Override public int compareTo(Account other) { return accountNumber.compareTo(other.accountNumber); }
    @Override public boolean equals(Object o) { return o instanceof Account a && accountNumber.equals(a.accountNumber); }
    @Override public int hashCode() { return accountNumber.hashCode(); }
    @Override public synchronized String toString() { return getClass().getSimpleName() + "[" + accountNumber + ", owner=" + ownerName + ", balance=₹" + balance + (active ? ", active]" : ", closed]"); }
}
