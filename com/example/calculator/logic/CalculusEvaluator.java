package com.example.calculator.logic;

// This class does not use trigonometric functions directly,
// so it does not need to be aware of angle unit settings for its
// current differentiation/integration of polynomials.
// If you were to add calculus operations for trig functions,
// then it would need to handle angle units similarly to ExpressionEvaluator.

public class CalculusEvaluator {

    public CalculusEvaluator() {
        // Constructor
    }

    public String differentiate(String expression) {
        if (expression == null || expression.trim().isEmpty()) return "0";
        try {
            Polynomial poly = Polynomial.parse(expression);
            Polynomial derivative = new Polynomial();
            for (Term term : poly.getTerms()) {
                if (term.variable.isEmpty() || term.exponent == 0) { /* Derivative of constant is 0 */ }
                else {
                    double newCoefficient = term.coefficient * term.exponent;
                    int newExponent = term.exponent - 1;
                    if (newCoefficient != 0) {
                        derivative.addTerm(new Term(newCoefficient, term.variable, newExponent));
                    }
                }
            }
            return derivative.toString();
        } catch (IllegalArgumentException e) { return "Error (Diff): " + e.getMessage();
        } catch (Exception e) { return "Error: Could not differentiate."; }
    }

    public String integrate(String expression) {
        if (expression == null || expression.trim().isEmpty()) return "C";
        try {
            Polynomial poly = Polynomial.parse(expression);
            Polynomial integral = new Polynomial();
            for (Term term : poly.getTerms()) {
                double newCoefficient;
                int newExponent = term.exponent + 1;
                // For polynomial integration, newExponent (n+1) should not be 0
                // unless original exponent was -1, which is not a simple polynomial term.
                if (newExponent == 0) { // Should not happen for std polynomials (exp >=0)
                     if (term.exponent == -1) return "Error: Integral of 1/" + term.variable + " involves ln";
                     newCoefficient = term.coefficient; // Should be caught by parser if term is invalid
                } else {
                    newCoefficient = term.coefficient / newExponent;
                }
                if (Math.abs(newCoefficient) > 1e-9) {
                    integral.addTerm(new Term(newCoefficient, term.variable, newExponent));
                }
            }
            String integralStr = integral.toString();
            if (integralStr.equals("0")) return "C"; // Integral of 0 is C
            return integralStr + " + C";
        } catch (IllegalArgumentException e) { return "Error (Integ): " + e.getMessage();
        } catch (Exception e) { return "Error: Could not integrate.";}
    }
}
