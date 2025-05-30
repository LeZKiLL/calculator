package com.example.calculator.logic;

import java.util.Objects;

public class Fraction {
    private long numerator;
    private long denominator;

    public Fraction(long numerator, long denominator) {
        if (denominator == 0) {
            throw new IllegalArgumentException("Denominator cannot be zero.");
        }
        this.numerator = numerator;
        this.denominator = denominator;
        simplify();
    }

    public Fraction(long wholeNumber) {
        this(wholeNumber, 1);
    }

    // Getter methods
    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    // Simplify the fraction
    private void simplify() {
        if (denominator < 0) { // Ensure denominator is positive
            numerator = -numerator;
            denominator = -denominator;
        }
        if (numerator == 0) {
            denominator = 1; // Normalize 0/x to 0/1
            return;
        }
        long commonDivisor = gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= commonDivisor;
        denominator /= commonDivisor;
    }

    // Greatest Common Divisor (GCD) using Euclidean algorithm
    private static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    // Arithmetic operations
    public Fraction add(Fraction other) {
        long newNumerator = this.numerator * other.denominator + other.numerator * this.denominator;
        long newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction subtract(Fraction other) {
        long newNumerator = this.numerator * other.denominator - other.numerator * this.denominator;
        long newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction multiply(Fraction other) {
        long newNumerator = this.numerator * other.numerator;
        long newDenominator = this.denominator * other.denominator;
        return new Fraction(newNumerator, newDenominator);
    }

    public Fraction divide(Fraction other) {
        if (other.numerator == 0) {
            throw new ArithmeticException("Cannot divide by zero fraction.");
        }
        long newNumerator = this.numerator * other.denominator;
        long newDenominator = this.denominator * other.numerator;
        return new Fraction(newNumerator, newDenominator);
    }

    public double toDouble() {
        return (double) numerator / denominator;
    }

    @Override
    public String toString() {
        if (denominator == 1) {
            return String.valueOf(numerator); // Whole number
        }
        return numerator + "/" + denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fraction fraction = (Fraction) o;
        // Fractions are equal if their simplified forms have the same numerator and denominator
        return numerator == fraction.numerator && denominator == fraction.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    // Static method to parse a string like "n/d" or "n" into a Fraction
    public static Fraction parseFraction(String token) throws NumberFormatException {
        if (token == null || token.isEmpty()) {
            throw new NumberFormatException("Cannot parse empty string to Fraction.");
        }
        if (token.contains("/")) {
            String[] parts = token.split("/");
            if (parts.length == 2) {
                try {
                    long num = Long.parseLong(parts[0].trim());
                    long den = Long.parseLong(parts[1].trim());
                    return new Fraction(num, den);
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Invalid fraction format: " + token);
                }
            } else {
                throw new NumberFormatException("Invalid fraction format (multiple slashes): " + token);
            }
        } else {
            // Try to parse as a whole number (which becomes numerator/1)
            // Or could be a decimal to convert, but for now, only whole numbers.
            try {
                 // If it's a decimal string, ExpressionEvaluator should handle it as double first.
                 // This parseFraction is mainly for "n/d" or "n" integer strings.
                if (token.contains(".")) { // If it's a decimal string, it's not a simple fraction string for this parser
                    throw new NumberFormatException("Decimal string '" + token + "' should be handled as Double first.");
                }
                long wholeNumber = Long.parseLong(token.trim());
                return new Fraction(wholeNumber);
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Cannot parse as whole number or fraction: " + token);
            }
        }
    }
}
