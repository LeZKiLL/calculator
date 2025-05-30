package com.example.calculator.logic;

import java.util.ArrayList;
import java.util.List;

// Assumes Polynomial.java and Term.java are in this package or correctly imported.

public class CalculusEvaluator {

    public CalculusEvaluator() {
        // Constructor, if needed for any setup
    }

    /**
     * Differentiates a polynomial with respect to its variable (assumed to be 'x' by Polynomial.parse).
     * @param expression The polynomial expression string (e.g., "3x^2 + 2x - 5").
     * @return The derivative as a polynomial string (e.g., "6x + 2").
     */
    public String differentiate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return "0"; // Derivative of empty or zero expression is 0
        }
        try {
            Polynomial poly = Polynomial.parse(expression);
            Polynomial derivative = new Polynomial();

            for (Term term : poly.getTerms()) {
                if (term.variable.isEmpty() || term.exponent == 0) { // Constant term or x^0
                    // Derivative of a constant is 0, so no term is added
                } else {
                    double newCoefficient = term.coefficient * term.exponent;
                    int newExponent = term.exponent - 1;
                    if (newCoefficient != 0) { // Only add if coefficient is not zero
                        derivative.addTerm(new Term(newCoefficient, term.variable, newExponent));
                    }
                }
            }
            return derivative.toString();
        } catch (IllegalArgumentException e) {
            return "Error (Diff): " + e.getMessage();
        } catch (Exception e) {
            // e.printStackTrace(); // For debugging
            return "Error: Could not differentiate.";
        }
    }

    /**
     * Computes the indefinite integral of a polynomial with respect to its variable (assumed 'x').
     * @param expression The polynomial expression string (e.g., "6x + 2").
     * @return The indefinite integral as a polynomial string, with "+ C" (e.g., "3x^2 + 2x + C").
     */
    public String integrate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            return "C"; // Integral of 0 is C
        }
        try {
            Polynomial poly = Polynomial.parse(expression);
            Polynomial integral = new Polynomial();

            for (Term term : poly.getTerms()) {
                // For a term ax^n, the integral is (a/(n+1))x^(n+1)
                double newCoefficient;
                int newExponent = term.exponent + 1;

                if (newExponent == 0) { // This case should not happen if n >= 0 for polynomials
                                        // but as a safeguard if term.exponent was -1 (e.g. 1/x)
                                        // For polynomial integration, n+1 will not be 0 unless original exponent was -1
                    // For 1/x, integral is ln|x|, not handled by Polynomial class.
                    // This calculator focuses on polynomial integration.
                    // If term.exponent was -1, this would be an issue.
                    // We assume standard polynomials where exponent >= 0.
                    if (term.exponent == -1) {
                         return "Error: Integral of 1/" + term.variable + " is ln|" + term.variable + "| + C (not polynomial)";
                    }
                     newCoefficient = term.coefficient; // Should not happen for typical poly
                } else {
                    newCoefficient = term.coefficient / newExponent;
                }

                if (Math.abs(newCoefficient) > 1e-9) { // Add term if coefficient is not effectively zero
                    integral.addTerm(new Term(newCoefficient, term.variable, newExponent));
                }
            }

            String integralStr = integral.toString();
            if (integralStr.equals("0") && poly.getTerms().stream().anyMatch(t -> t.coefficient !=0)) {
                // If original poly was not zero, but integral terms cancelled to 0 (unlikely for simple polys)
                // Still, the constant of integration is there.
                 return "C";
            } else if (integralStr.equals("0")) { // Integral of "0" is "C"
                return "C";
            }
            
            return integralStr + " + C";

        } catch (IllegalArgumentException e) {
            return "Error (Integ): " + e.getMessage();
        } catch (Exception e) {
            // e.printStackTrace(); // For debugging
            return "Error: Could not integrate.";
        }
    }
}
