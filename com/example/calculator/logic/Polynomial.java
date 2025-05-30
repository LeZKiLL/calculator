package com.example.calculator.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Polynomial {
    private List<Term> terms;

    public Polynomial() {
        this.terms = new ArrayList<>();
    }

    public Polynomial(Term term) {
        this();
        if (term.coefficient != 0) {
            this.terms.add(term);
        }
    }
    
    public Polynomial(List<Term> initialTerms) {
        this.terms = new ArrayList<>();
        for (Term t : initialTerms) {
            addTerm(t); // Use addTerm to handle combining like terms
        }
        simplify();
    }


    public void addTerm(Term newTerm) {
        if (newTerm.coefficient == 0) return; // Don't add zero terms

        boolean found = false;
        for (int i = 0; i < terms.size(); i++) {
            Term existingTerm = terms.get(i);
            if (existingTerm.variable.equals(newTerm.variable) && existingTerm.exponent == newTerm.exponent) {
                double combinedCoeff = existingTerm.coefficient + newTerm.coefficient;
                if (combinedCoeff == 0) {
                    terms.remove(i); // Remove if they cancel out
                } else {
                    terms.set(i, new Term(combinedCoeff, existingTerm.variable, existingTerm.exponent));
                }
                found = true;
                break;
            }
        }
        if (!found) {
            terms.add(newTerm);
        }
        simplify();
    }
    
    private void simplify() {
        // Remove terms with zero coefficient
        terms.removeIf(term -> term.coefficient == 0);
        // Sort terms by exponent (descending) then by variable name for consistent output
        Collections.sort(terms);
    }

    public Polynomial add(Polynomial other) {
        Polynomial result = new Polynomial(new ArrayList<>(this.terms)); // Create a new polynomial with current terms
        for (Term termToAdd : other.terms) {
            result.addTerm(termToAdd);
        }
        return result;
    }

    public Polynomial multiply(Polynomial other) {
        Polynomial result = new Polynomial();
        if (this.terms.isEmpty() || other.terms.isEmpty()) {
            return result; // Multiplying by zero polynomial
        }
        for (Term t1 : this.terms) {
            for (Term t2 : other.terms) {
                result.addTerm(t1.multiply(t2));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        simplify(); // Ensure sorted and simplified
        if (terms.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terms.size(); i++) {
            Term t = terms.get(i);
            String termStr = t.toString();

            if (termStr.isEmpty()) continue; // Skip empty strings from zero terms like 0x^2

            if (i > 0) { // Not the first term
                if (t.coefficient > 0) {
                    sb.append(" + ");
                } else { // Coefficient is negative
                    sb.append(" - ");
                    // Term.toString() already handles its own sign for the first part of the term.
                    // We need to present the absolute value part of the term here.
                    termStr = new Term(Math.abs(t.coefficient), t.variable, t.exponent).toString();
                }
            } else { // First term
                // Term.toString() handles its own sign
            }
            sb.append(termStr);
        }
        return sb.toString();
    }

    // A VERY basic parser for expressions like "x+2" or "2x-1" or just "x" or "3"
    // It assumes a single variable 'x' or constants.
    public static Polynomial parse(String expressionPart) {
        expressionPart = expressionPart.trim();
        Polynomial p = new Polynomial();
        // Regex to find terms like: -3x^2, 2x, -x, 5, x
        // Pattern: optional sign, optional coefficient, optional variable, optional ^exponent
        Pattern termPattern = Pattern.compile("([+-]?\\s*\\d*\\.?\\d*)\\s*([a-zA-Z]?)\\s*(\\^\\s*\\d+)?");
        // This regex is still quite naive. A proper tokenizer/parser is complex.
        // Let's try a simpler split based on + and then process parts starting with -

        String processedExpression = expressionPart.replaceAll("\\s+", "").replace("-", "+-");
        if (processedExpression.startsWith("+-")) {
            // Handle expressions like "-x+2" -> treat first term as negative
             if(processedExpression.length() > 2 && processedExpression.charAt(2) == '+'){
                // This is to avoid cases like "+-x" becoming "-x" if original was just "-x"
             } else {
                processedExpression = processedExpression.substring(1); // "-x+2" becomes "-x", "+2"
             }
        }


        String[] stringTerms = processedExpression.split("\\+");

        for (String sTerm : stringTerms) {
            if (sTerm.isEmpty()) continue;

            double coeff = 1.0;
            String var = "";
            int exp = 0;
            String originalSTerm = sTerm; // for error reporting

            sTerm = sTerm.trim();
            if (sTerm.startsWith("-")) {
                coeff = -1.0;
                sTerm = sTerm.substring(1);
            }

            Matcher matcher = Pattern.compile("(\\d*\\.?\\d*)\\s*([a-zA-Z]?)\\s*(\\^\\s*(\\d+))?").matcher(sTerm);
            // Example breakdown of "2x^3": coeffPart="2", varPart="x", expPart="^3", actualExp="3"
            // Example "x": coeffPart="", varPart="x", expPart=null
            // Example "5": coeffPart="5", varPart="", expPart=null

            if (sTerm.matches("[a-zA-Z](\\^\\d+)?")) { // Matches "x", "y^2"
                var = String.valueOf(sTerm.charAt(0));
                if (sTerm.contains("^")) {
                    exp = Integer.parseInt(sTerm.substring(sTerm.indexOf('^') + 1));
                } else {
                    exp = 1;
                }
                // Coeff remains 1.0 or -1.0
            } else if (sTerm.matches("\\d*\\.?\\d+[a-zA-Z]?(\\^\\d+)?")) { // Matches "2x^3", "3.5y", "4"
                String coeffStr = sTerm.replaceAll("[a-zA-Z].*", ""); // Get numbers at the start
                if (!coeffStr.isEmpty()) {
                    coeff *= Double.parseDouble(coeffStr);
                }

                if (sTerm.matches(".*[a-zA-Z].*")) { // Contains a variable
                    var = sTerm.replaceAll("[^a-zA-Z]", ""); // Extract variable part
                    var = var.substring(0,1); // take first char as var for simplicity
                    if (sTerm.contains("^")) {
                        exp = Integer.parseInt(sTerm.substring(sTerm.indexOf('^') + 1));
                    } else {
                        exp = 1; // like 2x means 2x^1
                    }
                } else { // Just a number (constant)
                    var = "";
                    exp = 0;
                }
            } else if (sTerm.matches("\\d*\\.?\\d*")) { // Just a number (constant)
                 coeff *= Double.parseDouble(sTerm);
                 var = "";
                 exp = 0;
            }
             else {
                throw new IllegalArgumentException("Cannot parse term: '" + originalSTerm + "' from '" + expressionPart +"'");
            }
            p.addTerm(new Term(coeff, var, exp));
        }
        return p;
    }
}
