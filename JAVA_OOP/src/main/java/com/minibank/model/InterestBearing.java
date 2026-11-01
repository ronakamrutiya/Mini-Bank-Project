package com.minibank.model;

public interface InterestBearing {
    double interestRate();
    default double yearlyInterest(long balance) { return balance * interestRate() / 100.0; }
}
