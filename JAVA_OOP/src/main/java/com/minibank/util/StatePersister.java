package com.minibank.util;

import com.minibank.model.Account;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public final class StatePersister {
    private StatePersister() { }
    public static void save(Account[] accounts, Path file) throws IOException { if (file.getParent() != null) Files.createDirectories(file.getParent()); try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) { out.writeObject(accounts); } }
    public static Account[] load(Path file) throws IOException {
        if (!Files.exists(file)) return new Account[0];
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) { return (Account[]) in.readObject(); }
        catch (ClassNotFoundException e) { throw new IOException("Saved bank data is incompatible", e); }
    }
    public static Account[] loadInto(Path file, Repository<Account> repository) throws IOException { Account[] accounts = load(file); for (Account a : accounts) repository.save(a); return accounts; }
}
