package com.minibank.service;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.util.Repository;
import com.minibank.util.TransactionLog;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class BankService {
    private final Repository<Account> accounts;
    private final ArrayList<String> history = new ArrayList<>();
    private final Path logFile;
    public BankService(Repository<Account> accounts, Path logFile) { this.accounts = accounts; this.logFile = logFile; }
    public synchronized Account openAccount(String type, String owner, long openingBalance, long ruleValue) {
        Account a = switch (type.toUpperCase()) {
            case "S", "SAVINGS" -> new SavingsAccount(owner, openingBalance, ruleValue);
            case "C", "CURRENT" -> new CurrentAccount(owner, openingBalance, ruleValue);
            case "F", "FD", "FIXED" -> new FixedDepositAccount(owner, openingBalance);
            default -> throw new IllegalArgumentException("Unknown account type");
        };
        accounts.save(a); record("OPEN", a.getAccountNumber(), openingBalance); return a;
    }
    public Account get(String id) throws AccountNotFoundException { Account a = accounts.findById(id); if (a == null) throw new AccountNotFoundException(id); return a; }
    public void deposit(String id, long amount) throws BankException { Account a = get(id); a.deposit(amount); record("DEPOSIT", id, amount); }
    public void withdraw(String id, long amount) throws BankException { Account a = get(id); a.withdraw(amount); record("WITHDRAW", id, amount); }
    public void transfer(String from, String to, long amount) throws BankException { Account source = get(from), target = get(to); source.transfer(target, amount); record("TRANSFER_OUT", from, amount); record("TRANSFER_IN", to, amount); }
    public String statement(String id) throws AccountNotFoundException { Account a = get(id); return a + " | rate=" + a.interestRate() + "% | yearly interest estimate=₹" + String.format("%.2f", a.yearlyInterest(a.getBalance())); }
    public ArrayList<Account> listAccounts() { ArrayList<Account> result = accounts.findAll(); result.sort(Comparator.comparing(Account::getAccountNumber)); return result; }
    public ArrayList<Account> sortedByBalance() { ArrayList<Account> result = accounts.findAll(); result.sort(Comparator.comparingLong(Account::getBalance)); return result; }
    public void processBatchDeposits(Map<String, Long> deposits) throws BankException {
        TransactionProcessor processor = new TransactionProcessor();
        for (Account account : accounts.findAll()) processor.register(account);
        CountDownLatch done = new CountDownLatch(deposits.size()); AtomicReference<BankException> failure = new AtomicReference<>();
        deposits.forEach((id, amount) -> processor.submit(() -> { try { deposit(id, amount); } catch (BankException e) { failure.compareAndSet(null, e); } finally { done.countDown(); } }));
        try { done.await(); processor.stop(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); throw new BankException("Batch was interrupted"); }
        if (failure.get() != null) throw failure.get();
    }
    public synchronized ArrayList<String> history() { return new ArrayList<>(history); }
    private synchronized void record(String action, String id, long amount) { String line = LocalDateTime.now() + "|" + action + "|" + amount + "|" + id; history.add(line); try { TransactionLog.append(logFile, line); } catch (IOException e) { throw new IllegalStateException("Transaction succeeded but log could not be written: " + e.getMessage(), e); } }
}
