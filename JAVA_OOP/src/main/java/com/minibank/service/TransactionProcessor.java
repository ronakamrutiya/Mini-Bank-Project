package com.minibank.service;

import com.minibank.model.Account;
import java.util.concurrent.*;

public class TransactionProcessor implements AutoCloseable {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final ConcurrentHashMap<String, Account> sharedAccounts = new ConcurrentHashMap<>();
    public void register(Account account) { sharedAccounts.put(account.getAccountNumber(), account); }
    public ConcurrentHashMap<String, Account> getSharedAccounts() { return sharedAccounts; }
    public void submit(Runnable task) { executor.execute(task); }
    public void stop() throws InterruptedException { executor.shutdown(); if (!executor.awaitTermination(30, TimeUnit.SECONDS)) executor.shutdownNow(); }
    @Override public void close() { try { stop(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); executor.shutdownNow(); } }
}
