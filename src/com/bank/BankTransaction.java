package com.bank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

class BankAccount {
    private int balance = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final int id;

    public BankAccount(int id) {
        this.id = id;
    }

    // Deposit with lock protection
    public void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
            logTransaction("Deposited " + amount);
        } finally {
            lock.unlock();
        }
    }

    // Withdraw with lock protection
    public void withdraw(int amount) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                logTransaction("Withdraw " + amount);
            } else {
                logTransaction("Failed to withdraw " + amount + " due to insufficient funds");
            }
        } finally {
            lock.unlock();
        }
    }

    // Synchronized method to ensure only one thread logs at a time
    private synchronized void logTransaction(String message) {
        System.out.println("Account " + id + ": " + message);
    }

    public int getBalance() {
        return balance;
    }
}
class BalanceSumTask extends RecursiveTask<Long> {
    private static final int THRESHOLD = 2; // <= 2 accounts
    private final List<BankAccount> accounts;
    private final int start;
    private final int end;

    public BalanceSumTask(List<BankAccount> accounts, int start, int end) {
        this.accounts = accounts;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += accounts.get(i).getBalance();
            }
            return sum;
        }

        int mid = start + length / 2;
        BalanceSumTask left = new BalanceSumTask(accounts, start, mid);
        BalanceSumTask right = new BalanceSumTask(accounts, mid, end);

        left.fork();
        long rightResult = right.compute();
        long leftResult = left.join();

        return leftResult + rightResult;
    }
}

public class BankTransaction {
    private static final int NUM_ACCOUNTS = 3;
    private static final int NUM_THREADS = 5;
    private static final int NUM_TRANSACTIONS = 25;

    public static void main(String[] args) throws InterruptedException {
        // ConcurrentHashMap to store accounts
        ConcurrentHashMap<Integer, BankAccount> accounts = new ConcurrentHashMap<>();

        for (int i = 1; i <= NUM_ACCOUNTS; i++) {
            accounts.put(i, new BankAccount(i));
        }

        // Atomic counter
        AtomicInteger transactionCount = new AtomicInteger(0);

        // Thread pool
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        // random deposit/withdraw
        for (int i = 0; i < NUM_TRANSACTIONS; i++) {
            executor.submit(() -> {
                int accId = ThreadLocalRandom.current().nextInt(1, NUM_ACCOUNTS + 1);
                BankAccount account = accounts.get(accId);
                int amount = ThreadLocalRandom.current().nextInt(10, 100);

                if (ThreadLocalRandom.current().nextBoolean()) {
                    account.deposit(amount);
                } else {
                    account.withdraw(amount);
                }

                transactionCount.incrementAndGet();
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n=== Final Balances ===");
        accounts.forEach((id, account) -> {
            System.out.println("Account " + id + ": $" + account.getBalance());
        });

        System.out.println("Total Transactions: " + transactionCount.get());

        // Fork/join
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        List<BankAccount> accountList =new ArrayList<>(accounts.values());
        BalanceSumTask task =new BalanceSumTask(accountList,0,accountList.size());
        long totalBalance = forkJoinPool.invoke(task);
        System.out.println("\n Total bank balance: $"+ totalBalance);
    }
}
