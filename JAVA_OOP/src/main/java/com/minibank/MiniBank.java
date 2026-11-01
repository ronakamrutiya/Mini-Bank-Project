package com.minibank;

import com.minibank.exception.BankException;
import com.minibank.model.*;
import com.minibank.service.BankService;
import com.minibank.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class MiniBank {
    private static final Path DATA = Path.of("data", "accounts.bin");
    private static final Path LOG = Path.of("logs", "transactions.log");
    private static final Path REPORT = Path.of("reports", "end-of-day.txt");
    private enum MenuOption { OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, STATEMENT, LIST_ACCOUNTS, REPORT, BATCH_DEPOSITS, EXIT }
    private record BankInfo(String name, String branch) { }
    public static void main(String[] args) {
        System.out.println("========================================\n" + new BankInfo("MiniBank", "Main Branch") + "\n========================================");
        Repository<Account> repository = new Repository<>();
        try { for (Account a : StatePersister.load(DATA)) repository.save(a); } catch (IOException e) { System.out.println("Could not load saved accounts: " + e.getMessage()); }
        BankService bank = new BankService(repository, LOG);
        try (Scanner input = new Scanner(System.in)) {
            boolean running = true;
            while (running) {
                showMenu(); int choice = readInt(input, "Choose: ");
                MenuOption option = switch (choice) { case 1 -> MenuOption.OPEN_ACCOUNT; case 2 -> MenuOption.DEPOSIT; case 3 -> MenuOption.WITHDRAW; case 4 -> MenuOption.TRANSFER; case 5 -> MenuOption.STATEMENT; case 6 -> MenuOption.LIST_ACCOUNTS; case 7 -> MenuOption.REPORT; case 8 -> MenuOption.BATCH_DEPOSITS; case 9 -> MenuOption.EXIT; default -> null; };
                if (option == null) { System.out.println("Choose a number from 1 to 9."); continue; }
                try { switch (option) {
                    case OPEN_ACCOUNT -> open(input, bank);
                    case DEPOSIT -> bank.deposit(readLine(input, "Account number: "), readLong(input, "Amount (₹): "));
                    case WITHDRAW -> bank.withdraw(readLine(input, "Account number: "), readLong(input, "Amount (₹): "));
                    case TRANSFER -> bank.transfer(readLine(input, "From account: "), readLine(input, "To account: "), readLong(input, "Amount (₹): "));
                    case STATEMENT -> System.out.println(bank.statement(readLine(input, "Account number: ")));
                    case LIST_ACCOUNTS -> { if (bank.listAccounts().isEmpty()) System.out.println("No accounts yet."); else bank.listAccounts().forEach(System.out::println); }
                    case REPORT -> System.out.println(ReportGenerator.generate(LOG.getParent(), REPORT));
                    case BATCH_DEPOSITS -> batchDeposits(input, bank);
                    case EXIT -> { StatePersister.save(bank.listAccounts().toArray(Account[]::new), DATA); System.out.println("Accounts saved. Thank you for banking with MiniBank."); running = false; }
                } } catch (BankException | IllegalArgumentException | IllegalStateException | IOException e) { System.out.println("Unable to complete operation: " + e.getMessage()); }
                catch (Exception e) { System.out.println("Unexpected error: " + e.getMessage()); }
            }
        } catch (NoSuchElementException e) { System.out.println("Input ended. Saving accounts before exit."); try { StatePersister.save(bank.listAccounts().toArray(Account[]::new), DATA); } catch (IOException io) { System.out.println(io.getMessage()); } }
    }
    private static void showMenu() { System.out.println("\n1. Open account\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Account statement\n6. List accounts\n7. End-of-day report\n8. Batch deposits\n9. Exit"); }
    private static void batchDeposits(Scanner in, BankService bank) throws BankException {
        int count = readInt(in, "Number of deposits: "); if (count < 1 || count > 1000) throw new IllegalArgumentException("Batch size must be from 1 to 1000");
        Map<String, Long> deposits = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) deposits.put(readLine(in, "Account number: "), readLong(in, "Amount (₹): "));
        bank.processBatchDeposits(deposits); System.out.println("Processed " + deposits.size() + " deposits.");
    }
    private static void open(Scanner in, BankService bank) { String type = readLine(in, "Type (S=savings, C=current, F=fixed deposit): "); String owner = readLine(in, "Owner name: "); long opening = readLong(in, "Opening balance (₹): "); long rule = switch (type.toUpperCase()) { case "S", "SAVINGS" -> readLong(in, "Minimum balance (₹): "); case "C", "CURRENT" -> readLong(in, "Overdraft limit (₹): "); default -> 0; }; System.out.println("Opened " + bank.openAccount(type, owner, opening, rule)); }
    private static String readLine(Scanner in, String prompt) { System.out.print(prompt); return in.nextLine().trim(); }
    private static int readInt(Scanner in, String prompt) { while (true) try { return Integer.parseInt(readLine(in, prompt)); } catch (NumberFormatException e) { System.out.println("Enter a whole number."); } }
    private static long readLong(Scanner in, String prompt) { while (true) try { return Long.parseLong(readLine(in, prompt)); } catch (NumberFormatException e) { System.out.println("Enter a whole number of rupees."); } }
}
