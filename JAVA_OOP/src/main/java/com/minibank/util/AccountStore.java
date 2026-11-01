package com.minibank.util;

import com.minibank.model.Account;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStore {
    private final Map<String, Account> accounts = new ConcurrentHashMap<>();
    private final List<String> transactionHistory = Collections.synchronizedList(new ArrayList<>());
    public void add(Account account) { accounts.put(account.getAccountNumber(), account); }
    public Account get(String accountNumber) { return accounts.get(accountNumber); }
    public ArrayList<Account> getAll() { return new ArrayList<>(accounts.values()); }
    public ArrayList<Account> sortedByBalance() { ArrayList<Account> result = getAll(); result.sort(Comparator.comparingLong(Account::getBalance)); return result; }
    public void addHistory(String line) { transactionHistory.add(line); }
    public ArrayList<String> getHistory() { synchronized (transactionHistory) { return new ArrayList<>(transactionHistory); } }
}
