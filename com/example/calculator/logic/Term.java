package com.example.calculator.logic;

import java.util.Objects;

public class Term implements Comparable<Term> {
    public double coefficient; // Made public for easier access from Polynomial, consider getters/setters for stricter encapsulation
    public String variable;    // e.g., "x", or "" for constants
    public int exponent;

    public Term(double coefficient, String variable, int exponent) {
        if (coefficient == 0.0) {
            this.coefficient = 0.0;
            this.variable = ""; // Normalize zero terms
            this.exponent = 0;
        } else {
            this.coefficient = coefficient;
            // If variable is empty but exponent is not 0, it's not a constant, default to "x"
            this.variable = (variable == null || variable.isEmpty()) && exponent != 0 ? "x" : (variable == null ? "" : variable);
            // If variable is empty, it's a constant, so exponent must be 0
            this.exponent = (this.variable.isEmpty()) ? 0 : exponent;
        }
    }

    public Term(double constantValue) {
        this(constantValue, "", 0);
    }

    public Term multiply(Term other) {
        if (!this.variable.equals(other.variable) && !this.variable.isEmpty() && !other.variable.isEmpty()) {
            // This simple model doesn't combine different variables like x*y into xy.
            // It's primarily for single-variable polynomials or constants.
            throw new IllegalArgumentException("Multiplication of terms with different variables (" + this.variable + ", " + other.variable + ") is not directly supported in this simplified model.");
        }
        String resultVariable = this.variable.isEmpty() ? other.variable : this.variable;
        return new Term(this.coefficient * other.coefficient, resultVariable, this.exponent + other.exponent);
    }

    @Override
    public String toString() {
        if (coefficient == 0 && exponent != 0) return ""; // Don't print "0x^2"
        if (coefficient == 0) return "0";


        StringBuilder sb = new StringBuilder();
        boolean coeffIsOne = Math.abs(coefficient) == 1.0;
        boolean expIsZero = exponent == 0;
        boolean expIsOne = exponent == 1;

        if (coefficient < 0) {
            sb.append("-");
        }
        // Append coefficient if it's not 1 or -1, OR if it's a constant term
        if (!coeffIsOne || expIsZero) {
            if (Math.abs(coefficient) == Math.floor(Math.abs(coefficient))) { // Integer coefficient
                sb.append((int) Math.abs(coefficient));
            } else {
                sb.append(String.format("%.2f", Math.abs(coefficient)).replaceAll("\\.?0+$", ""));
            }
        } else if (coeffIsOne && variable.isEmpty()) { // Edge case for constant 1 or -1
             sb.append("1");
        }


        if (!variable.isEmpty() && !expIsZero) {
            // If coefficient was 1 or -1 and it wasn't printed, but there's a variable,
            // make sure something is there before the variable (e.g. for "x" or "-x")
            if (sb.length() == 0 || sb.toString().equals("-") && coeffIsOne) {
                // If sb is empty (coeff 1) or just "-" (coeff -1), we need to ensure '1' is not printed before var
                // This part is tricky, the above condition `!coeffIsOne || expIsZero` should handle it mostly.
                // The goal is to avoid "1x" and just have "x".
            }
            sb.append(variable);
            if (!expIsOne) {
                sb.append("^").append(exponent);
            }
        }
        
        // If after all this, sb is empty, it means it was a term like "x" (coeff 1, exp 1)
        if (sb.length() == 0 && coefficient == 1.0 && !variable.isEmpty() && !expIsZero) {
            sb.append(variable);
            if (!expIsOne) {
                 sb.append("^").append(exponent);
            }
        }
        // If it was -x
         if (sb.toString().equals("-") && coefficient == -1.0 && !variable.isEmpty() && !expIsZero) {
            sb.append(variable);
            if (!expIsOne) {
                 sb.append("^").append(exponent);
            }
        }

        return sb.toString();
    }

    @Override
    public int compareTo(Term other) { // Sort by exponent (descending), then by variable
        if (this.exponent != other.exponent) {
            return Integer.compare(other.exponent, this.exponent);
        }
        return this.variable.compareTo(other.variable);
    }

    // For combining: terms are "equal" if they have the same variable and exponent
     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Term term = (Term) o;
        return exponent == term.exponent && Objects.equals(variable, term.variable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, exponent);
    }
}
