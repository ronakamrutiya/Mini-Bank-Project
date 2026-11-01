# MiniBank

MiniBank is a Java 17+ console banking project that brings the supplied Practical 1–14 requirements together in one runnable application.

## Features

- Dynamic console menu for opening savings, current, and fixed deposit accounts; deposits; withdrawals; transfers; statements; account listing; and end-of-day reports.
- Encapsulated account balances, account-specific withdrawal rules, checked banking exceptions, and synchronized account operations.
- Reflection based `@Id`, `@Positive`, and `@MaxLength` annotations plus a generic in-memory repository.
- Account serialization in `data/accounts.bin`, append-only transaction history in `logs/transactions.log`, and reports in `reports/end-of-day.txt`.
- Concurrent transaction processor and thread-safe account registry for service/batch use.

## Requirements

- JDK 17 or later
- Maven 3.8 or later

## Build and run

```powershell
mvn clean package
java -jar target/minibank-1.0.0.jar
```

The application creates its data, log, and report directories in the current working directory. Select **Exit** to save all accounts. Account types:

- **Savings:** set a minimum balance; withdrawals must preserve it. Interest rate: 4%.
- **Current:** set an overdraft limit. Interest rate: 0%.
- **Fixed deposit:** withdrawals are locked. Interest rate: 7%.

Amounts are whole rupees. Current accounts can have a negative balance within their overdraft limit. The report totals logged deposits and withdrawals; transfer legs are recorded as transfer events and are not counted as deposits or withdrawals.

## Requirement reconciliation

Practical 5 makes `Account` abstract, so the finished program represents accounts through its three concrete subclasses. Practical 8 replaces boolean failure flags with checked exceptions; successful withdrawal still returns `true`. The menu includes a report option in addition to the original five actions. Practical 4 was marked skipped and is not inferred.

The classroom requirements include experiments that intentionally demonstrate race conditions and deadlocks. The deliverable keeps the safe synchronized account behavior and deterministic lock ordering instead of making unsafe behavior part of normal execution. `TransactionProcessor` provides the managed four-thread executor and concurrent account registry for batch use.
