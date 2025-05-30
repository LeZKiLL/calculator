package com.example.calculator.logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class SymbolicEvaluator {

    private static final Pattern FOIL_PATTERN = Pattern.compile(
        "\\s*\\(([^()]+)\\)\\s*\\*?\\s*\\(([^()]+)\\)\\s*"
    );
    private static final Pattern SINGLE_TERM_BRACKET_PATTERN1 = Pattern.compile(
        "\\s*([^()\\s+*-]+)\\s*\\*?\\s*\\(([^()]+)\\)\\s*" // Term cannot contain operators here
    );
    private static final Pattern SINGLE_TERM_BRACKET_PATTERN2 = Pattern.compile(
        "\\s*\\(([^()]+)\\)\\s*\\*?\\s*([^()\\s+*-]+)\\s*"
    );

    // Pattern to detect an equation (contains one '=')
    private static final Pattern EQUATION_PATTERN = Pattern.compile("(.+)\\s*=\\s*(.+)");

    public String evaluate(String expression) {
        expression = expression.trim();
        Matcher equationMatcher = EQUATION_PATTERN.matcher(expression);

        if (equationMatcher.matches()) {
            String lhsStr = equationMatcher.group(1).trim();
            String rhsStr = equationMatcher.group(2).trim();
            return solveEquation(lhsStr, rhsStr);
        }

        // Try FOIL and term*bracket expansion if not an equation
        Matcher foilMatcher = FOIL_PATTERN.matcher(expression);
        if (foilMatcher.matches()) {
            String polyStr1 = foilMatcher.group(1);
            String polyStr2 = foilMatcher.group(2);
            try {
                Polynomial p1 = Polynomial.parse(polyStr1);
                Polynomial p2 = Polynomial.parse(polyStr2);
                return p1.multiply(p2).toString();
            } catch (IllegalArgumentException e) {
                return "Error (Expansion): " + e.getMessage();
            }
        }

        Matcher stbp1Matcher = SINGLE_TERM_BRACKET_PATTERN1.matcher(expression);
        if(stbp1Matcher.matches()){
            String termStr = stbp1Matcher.group(1);
            String polyStr = stbp1Matcher.group(2);
            try {
                Polynomial pTerm = Polynomial.parse(termStr);
                Polynomial pBracket = Polynomial.parse(polyStr);
                return pTerm.multiply(pBracket).toString();
            } catch (IllegalArgumentException e) {
                return "Error (Expansion): " + e.getMessage();
            }
        }

        Matcher stbp2Matcher = SINGLE_TERM_BRACKET_PATTERN2.matcher(expression);
         if(stbp2Matcher.matches()){
            String polyStr = stbp2Matcher.group(1);
            String termStr = stbp2Matcher.group(2);
            try {
                Polynomial pBracket = Polynomial.parse(polyStr);
                Polynomial pTerm = Polynomial.parse(termStr);
                return pBracket.multiply(pTerm).toString();
            } catch (IllegalArgumentException e) {
                return "Error (Expansion): " + e.getMessage();
            }
        }
        // If no specific symbolic pattern matches, it's not for this evaluator.
        // The GUI's fallback to numerical evaluator will handle it or show an error.
        throw new IllegalArgumentException("Expression not recognized for symbolic operation.");
    }

    private String solveEquation(String lhsStr, String rhsStr) {
        try {
            Polynomial lhsPoly = Polynomial.parse(lhsStr);
            Polynomial rhsPoly = Polynomial.parse(rhsStr);

            // Rearrange to P(x) = 0  =>  lhsPoly - rhsPoly = 0
            Polynomial equationPoly = lhsPoly.subtract(rhsPoly);
            equationPoly.getTerms().removeIf(term -> Math.abs(term.coefficient) < 1e-9 && term.exponent !=0); // Clean up near-zero non-constant terms
            if (equationPoly.getTerms().size() > 1) {
                equationPoly.getTerms().removeIf(term -> Math.abs(term.coefficient) < 1e-9 && term.exponent == 0); // Clean zero constant if other terms exist
            }
             if (equationPoly.getTerms().isEmpty()){ // e.g. x = x
                 equationPoly.addTerm(new Term(0.0)); // Represent as 0 = 0
             }


            int degree = equationPoly.getDegree();
            // Assuming single variable 'x' for now. A more robust system would identify the variable.
            String variable = "x"; 
            // Attempt to find the variable if not 'x' (simple heuristic)
            for(Term t : equationPoly.getTerms()){
                if(!t.variable.isEmpty()){
                    variable = t.variable;
                    break;
                }
            }


            if (degree == 1) { // Linear equation: ax + b = 0
                double a = equationPoly.getCoefficient(1, variable);
                double b = equationPoly.getConstantCoefficient();

                if (Math.abs(a) < 1e-9) { // a is effectively zero
                    if (Math.abs(b) < 1e-9) { // 0 = 0
                        return "Identity (Infinite solutions)";
                    } else { // b = 0, where b != 0
                        return "Contradiction (No solution)";
                    }
                }
                double solution = -b / a;
                return variable + " = " + formatDouble(solution);
            } else if (degree == 2) { // Quadratic equation: ax^2 + bx + c = 0
                double a = equationPoly.getCoefficient(2, variable);
                double b = equationPoly.getCoefficient(1, variable);
                double c = equationPoly.getConstantCoefficient();

                if (Math.abs(a) < 1e-9) { // Not actually quadratic, should have been caught by degree 1
                     // Re-evaluate as linear if 'a' is zero
                    equationPoly = new Polynomial(Arrays.asList(new Term(b, variable, 1), new Term(c)));
                    return solveEquation(equationPoly.toString(), "0"); // Recursive call for simplicity
                }

                double discriminant = b * b - 4 * a * c;

                if (discriminant > 1e-9) { // Two distinct real roots (added epsilon for FP)
                    double root1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                    double root2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                    return variable + "₁ = " + formatDouble(root1) + ", " + variable + "₂ = " + formatDouble(root2);
                } else if (Math.abs(discriminant) < 1e-9) { // One real root (repeated)
                    double root = -b / (2 * a);
                    return variable + " = " + formatDouble(root);
                } else { // No real roots (complex roots)
                    // Calculate and display complex roots
                    double realPart = -b / (2 * a);
                    double imaginaryPart = Math.sqrt(-discriminant) / (2 * a);
                    return variable + "₁ = " + formatDouble(realPart) + " + " + formatDouble(imaginaryPart) + "i, " +
                           variable + "₂ = " + formatDouble(realPart) + " - " + formatDouble(imaginaryPart) + "i";
                }
            } else if (degree == 0) { // Constant equation: c = 0
                 double c = equationPoly.getConstantCoefficient();
                 if (Math.abs(c) < 1e-9) { // 0 = 0
                     return "Identity (Infinite solutions)";
                 } else { // c = 0 where c != 0
                     return "Contradiction (No solution)";
                 }
            }
            else if (degree < 0 && equationPoly.getTerms().isEmpty()){ // 0 = 0 case after subtractions
                 return "Identity (Infinite solutions)";
            }

            return "Solution for degree " + degree + " not implemented. Equation: " + equationPoly.toString() + " = 0";

        } catch (IllegalArgumentException e) {
            return "Error (Solving): " + e.getMessage();
        } catch (Exception e) {
            // e.printStackTrace(); // For debugging
            return "Error: Could not solve equation.";
        }
    }

    private String formatDouble(double val) {
        if (Math.abs(val) < 1e-9) return "0"; // Treat very small numbers as 0
        if (val == (long) val) {
            return String.format("%d", (long) val);
        } else {
            // Attempt to convert to a simple fraction if possible for cleaner output
            try {
                Fraction f = ExpressionEvaluator.doubleToFraction(val, 1000); // Max denominator 1000 for display
                if (f.getDenominator() != 1 && ( (double)f.getNumerator()/f.getDenominator() == val ) ) { // Check if conversion is exact enough
                     // Check if fraction is simpler than decimal
                    String fracStr = f.toString();
                    String decStr = String.format("%.4f", val).replaceAll("\\.?0+$", "");
                    if (fracStr.length() <= decStr.length() + 2 || f.getDenominator() < 100) { // Heuristic for "simpler"
                        return fracStr;
                    }
                }
            } catch (Exception e) { /* Fall through to decimal formatting */ }
            
            String formatted = String.format("%.4f", val).replaceAll("\\.?0+$", "");
             if (formatted.isEmpty() && val == 0) formatted = "0";
             else if (formatted.equals(".")) formatted = "0";
            return formatted;
        }
    }
}
