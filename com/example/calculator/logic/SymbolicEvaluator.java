package com.example.calculator.logic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SymbolicEvaluator {

    // Pattern to detect expressions like (poly1)(poly2) or (poly1)*(poly2)
    // It captures the content inside the parentheses.
    private static final Pattern FOIL_PATTERN = Pattern.compile(
        "\\s*\\(([^()]+)\\)\\s*\\*?\\s*\\(([^()]+)\\)\\s*"
    );
    // For single term * bracket: 3(x+1) or x(x+1)
    private static final Pattern SINGLE_TERM_BRACKET_PATTERN1 = Pattern.compile(
        "\\s*([^()\\s]+)\\s*\\*?\\s*\\(([^()]+)\\)\\s*" // e.g. x(x+1) or 3(x+1)
    );
    // For bracket * single term: (x+1)x or (x+1)3
    private static final Pattern SINGLE_TERM_BRACKET_PATTERN2 = Pattern.compile(
        "\\s*\\(([^()]+)\\)\\s*\\*?\\s*([^()\\s]+)\\s*" // e.g. (x+1)x
    );


    public String evaluate(String expression) {
        expression = expression.trim();
        Matcher foilMatcher = FOIL_PATTERN.matcher(expression);

        if (foilMatcher.matches()) {
            String polyStr1 = foilMatcher.group(1);
            String polyStr2 = foilMatcher.group(2);
            try {
                Polynomial p1 = Polynomial.parse(polyStr1);
                Polynomial p2 = Polynomial.parse(polyStr2);
                return p1.multiply(p2).toString();
            } catch (IllegalArgumentException e) {
                return "Error (FOIL): " + e.getMessage();
            }
        }

        Matcher stbp1Matcher = SINGLE_TERM_BRACKET_PATTERN1.matcher(expression);
        if(stbp1Matcher.matches()){
            String termStr = stbp1Matcher.group(1);
            String polyStr = stbp1Matcher.group(2);
            try {
                Polynomial pTerm = Polynomial.parse(termStr); // Parse the single term as a (potentially simple) polynomial
                Polynomial pBracket = Polynomial.parse(polyStr);
                return pTerm.multiply(pBracket).toString();
            } catch (IllegalArgumentException e) {
                return "Error (Term*Bracket): " + e.getMessage();
            }
        }

        Matcher stbp2Matcher = SINGLE_TERM_BRACKET_PATTERN2.matcher(expression);
         if(stbp2Matcher.matches()){
            String polyStr = stbp2Matcher.group(1);
            String termStr = stbp2Matcher.group(2);
            try {
                Polynomial pBracket = Polynomial.parse(polyStr);
                Polynomial pTerm = Polynomial.parse(termStr); // Parse the single term
                return pBracket.multiply(pTerm).toString();
            } catch (IllegalArgumentException e) {
                return "Error (Bracket*Term): " + e.getMessage();
            }
        }


        // If no specific symbolic pattern matches, it's not for this evaluator
        // Or, it could be a numerical expression. The GUI will decide.
        throw new IllegalArgumentException("Expression not in a recognized symbolic format for expansion.");
    }
}
