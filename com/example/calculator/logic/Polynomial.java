package com.example.calculator.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// import java.util.stream.Collectors; // Not used in the provided snippet, can be removed

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
        simplify(); // Simplify after adding a single term
    }
    
    public Polynomial(List<Term> initialTerms) {
        this.terms = new ArrayList<>();
        for (Term t : initialTerms) {
            addTerm(t); // Use addTerm to handle combining like terms
        }
        // simplify() is called within addTerm, so not strictly needed again here
        // but an explicit call ensures the final state is simplified and sorted.
        simplify(); 
    }

    public List<Term> getTerms() {
        return new ArrayList<>(terms); // Return a copy to prevent external modification
    }

    public void addTerm(Term newTerm) {
        if (newTerm.coefficient == 0 && newTerm.exponent != 0) { 
            // Allow adding a "true zero" term (0 with exponent 0) if needed,
            // but generally skip terms that become 0x^n.
            // If it's a constant 0, it might be relevant in some contexts.
            // For simplification, if coefficient is 0, we usually remove it.
            boolean termExists = false;
            for(Term t : terms){
                if(t.variable.equals(newTerm.variable) && t.exponent == newTerm.exponent){
                    termExists = true;
                    break;
                }
            }
            if(!termExists && newTerm.exponent == 0 && newTerm.variable.isEmpty()){
                 // This case is tricky. If we are explicitly adding a Term(0,"",0)
                 // it might be intentional. For now, simplify will remove it if coeff is 0.
            } else {
                // If coeff is 0 and it's not a standalone constant 0, effectively do nothing
                // or ensure simplify removes it.
                // The original "if (newTerm.coefficient == 0) return;" is safer.
                if (newTerm.coefficient == 0) return;
            }
        }


        boolean found = false;
        for (int i = 0; i < terms.size(); i++) {
            Term existingTerm = terms.get(i);
            // Ensure variables match, or one of them is a constant (empty variable)
            if ((existingTerm.variable.equals(newTerm.variable) || existingTerm.variable.isEmpty() || newTerm.variable.isEmpty()) &&
                existingTerm.exponent == newTerm.exponent) {
                
                // If variables were different but exponents matched (e.g. constant + x^0)
                // We need to be careful. This logic is for combining LIKE terms.
                if (!existingTerm.variable.equals(newTerm.variable) && !(existingTerm.variable.isEmpty() && newTerm.variable.isEmpty()) ) {
                    if(existingTerm.exponent == 0 && newTerm.exponent == 0){ // both constants
                        // proceed to combine
                    } else {
                        continue; // Not like terms if variables differ and not both constants
                    }
                }

                double combinedCoeff = existingTerm.coefficient + newTerm.coefficient;
                // Use a small epsilon for floating point comparison to zero
                if (Math.abs(combinedCoeff) < 1e-9) { // Effectively zero
                    terms.remove(i); 
                } else {
                    terms.set(i, new Term(combinedCoeff, existingTerm.variable.isEmpty() ? newTerm.variable : existingTerm.variable, existingTerm.exponent));
                }
                found = true;
                break;
            }
        }
        if (!found && newTerm.coefficient != 0) { // Only add if not found and coefficient is not zero
            terms.add(newTerm);
        }
        simplify();
    }
    
    private void simplify() {
        terms.removeIf(term -> Math.abs(term.coefficient) < 1e-9 && term.exponent != 0); // Remove terms like 0x^n
        // If the only term left is 0x^0, keep it as "0".
        if (terms.size() > 1) {
             terms.removeIf(term -> Math.abs(term.coefficient) < 1e-9 && term.exponent == 0 && !term.variable.isEmpty());
        }
        if (terms.isEmpty() && !initialZeroConstantPresent()) {
            // If all terms were removed or cancelled, and there wasn't an explicit 0 constant,
            // we might not want to add Term(0). Let toString handle empty list as "0".
        } else if (terms.isEmpty() && initialZeroConstantPresent()) {
            // If it was explicitly new Polynomial(new Term(0)), keep it.
            // This case is tricky. For now, if terms list becomes empty, toString returns "0".
        }
        Collections.sort(terms);
    }

    private boolean initialZeroConstantPresent(){
        // This helper is a bit of a hack. The core issue is how "0" is represented.
        // A Polynomial representing "0" should ideally be new Polynomial(new Term(0.0))
        // For now, if terms is empty, toString() handles it.
        return false; 
    }


    public Polynomial add(Polynomial other) {
        Polynomial result = new Polynomial(new ArrayList<>(this.terms));
        for (Term termToAdd : other.terms) {
            result.addTerm(termToAdd);
        }
        return result;
    }

    public Polynomial subtract(Polynomial other) {
        Polynomial result = new Polynomial(new ArrayList<>(this.terms));
        for (Term termToSubtract : other.terms) {
            result.addTerm(new Term(-termToSubtract.coefficient, termToSubtract.variable, termToSubtract.exponent));
        }
        return result;
    }

    public Polynomial multiply(Polynomial other) {
        Polynomial result = new Polynomial();
        if (this.terms.isEmpty() || other.terms.isEmpty()) {
            // If one polynomial is "0" (empty terms or only Term(0)), result is "0"
            if ((this.terms.size() == 1 && this.terms.get(0).coefficient == 0) || 
                (other.terms.size() == 1 && other.terms.get(0).coefficient == 0) ||
                 this.terms.isEmpty() || other.terms.isEmpty()) {
                result.addTerm(new Term(0.0)); // Add a zero term
                return result;
            }
        }
        for (Term t1 : this.terms) {
            for (Term t2 : other.terms) {
                result.addTerm(t1.multiply(t2));
            }
        }
        return result;
    }

    public int getDegree() {
        if (terms.isEmpty()) {
            return -1; // Or 0 if representing the zero polynomial. -1 indicates no terms.
                       // For equation solving, degree of "0" is often considered -infinity or undefined.
                       // Let's return 0 if it's just a constant (including 0).
        }
        // Since terms are sorted by exponent descending, the first term has the highest exponent.
        // However, ensure that term is not a zero coefficient term that wasn't cleaned up.
        // Simplify should handle this.
        int maxDegree = 0; // Degree of a constant is 0
        boolean hasNonConstantTerm = false;
        for(Term t : terms){
            if(Math.abs(t.coefficient) > 1e-9 && !t.variable.isEmpty()){ // Non-zero coefficient and has a variable
                maxDegree = Math.max(maxDegree, t.exponent);
                hasNonConstantTerm = true;
            } else if (Math.abs(t.coefficient) > 1e-9 && t.variable.isEmpty()){ // It's a non-zero constant
                // maxDegree remains 0 if no other higher degree terms
            }
        }
        if (!hasNonConstantTerm && !terms.isEmpty()) return 0; // It's a constant (or zero polynomial)
        if (terms.isEmpty()) return 0; // Zero polynomial has degree 0 for simplicity here, or -1.

        return maxDegree;
    }

    // Gets coefficient of var^exp. Assumes single variable.
    public double getCoefficient(int exponent, String variableName) {
        for (Term t : terms) {
            if (t.variable.equals(variableName) && t.exponent == exponent) {
                return t.coefficient;
            }
            // Also handle constants if exponent is 0 and variableName is empty or matches term's empty var
            if (exponent == 0 && t.exponent == 0 && t.variable.isEmpty() && (variableName == null || variableName.isEmpty())) {
                return t.coefficient;
            }
        }
        return 0.0; // No term with that variable and exponent
    }
    
    // Simpler getCoefficient for constants (exponent 0)
    public double getConstantCoefficient() {
        for (Term t : terms) {
            if (t.variable.isEmpty() && t.exponent == 0) {
                return t.coefficient;
            }
        }
        return 0.0;
    }


    @Override
    public String toString() {
        simplify(); 
        if (terms.isEmpty()) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < terms.size(); i++) {
            Term t = terms.get(i);
            String termStr = t.toString();

            if (termStr.isEmpty() || (termStr.equals("0") && terms.size() > 1) ) {
                // if term is "0" and it's not the ONLY term, skip it.
                if (termStr.equals("0") && terms.size() == 1 && Math.abs(t.coefficient) < 1e-9) {
                    // It's the only term and it's zero, handled by terms.isEmpty() check earlier or should be "0"
                } else if (termStr.equals("0") && terms.size() > 1) {
                    continue;
                } else if (termStr.isEmpty()){
                    continue;
                }
            }


            if (i > 0) { 
                if (t.coefficient > 0) {
                    sb.append(" + ");
                } else { 
                    sb.append(" - ");
                    termStr = new Term(Math.abs(t.coefficient), t.variable, t.exponent).toString();
                }
            } else { 
                // First term, Term.toString() handles its sign.
                // However, if it's just "0" and there are other terms, it might have been skipped.
                // This case is tricky. The simplify() should ideally ensure the first term is not "0" unless it's the only term.
            }
            sb.append(termStr);
        }
        if (sb.length() == 0) return "0"; // If all terms were effectively zero and skipped
        return sb.toString();
    }

    public static Polynomial parse(String expressionPart) {
        expressionPart = expressionPart.trim();
        Polynomial p = new Polynomial();
        
        if(expressionPart.isEmpty()) return p; // Return empty polynomial for empty string

        // Improved regex to better handle signs and structure
        // Matches terms like: +3x^2, -2x, x, -5
        // It looks for an optional sign, then coefficient (optional if variable exists),
        // then variable (optional if constant), then exponent (optional if 1 or 0).
        //Pattern termPattern = Pattern.compile("([+-]?)\\s*(\\d*\\.?\\d*)?\\s*([a-zA-Z])?\\s*(\\^\\s*(\\d+))?");
        // This regex is still a simplification. A full parser is needed for robustness.
        // A simpler approach: split by '+' and then handle '-' within terms.

        String currentExpr = expressionPart.replaceAll("\\s+", ""); // Remove all spaces
        // Replace "-" with "+-" to make splitting by "+" easier, but be careful with leading "-"
        if (currentExpr.startsWith("-")) {
            currentExpr = currentExpr.substring(0,0) + currentExpr.substring(0); // Keep leading minus as part of first term
        }
        currentExpr = currentExpr.replace("-", "+-");


        String[] stringTerms = currentExpr.split("\\+");

        for (String sTerm : stringTerms) {
            if (sTerm.isEmpty()) continue;

            double coeff = 1.0;
            String var = ""; // Default to 'x' if a variable part is found without explicit name
            int exp = 0;
            boolean signIsNegative = false;

            sTerm = sTerm.trim(); // Should be redundant due to replaceAll
            if (sTerm.startsWith("-")) {
                signIsNegative = true;
                sTerm = sTerm.substring(1);
            }
            if (sTerm.startsWith("+")) { // From "+-" replacement
                sTerm = sTerm.substring(1);
            }
            if (sTerm.isEmpty()) continue;


            // Try to match variable and exponent first
            Matcher varExpMatcher = Pattern.compile("([a-zA-Z])(?:\\^(\\d+))?").matcher(sTerm);
            String coeffStrPart = sTerm;

            if (varExpMatcher.find()) { // Found variable part
                var = varExpMatcher.group(1); // The variable itself
                if (varExpMatcher.group(2) != null) { // Exponent exists
                    exp = Integer.parseInt(varExpMatcher.group(2));
                } else {
                    exp = 1; // No exponent means exponent is 1
                }
                coeffStrPart = sTerm.substring(0, varExpMatcher.start()); // Part before variable is coefficient
            } else { // No variable part, must be a constant
                var = "";
                exp = 0;
                coeffStrPart = sTerm;
            }

            // Parse coefficient part
            if (coeffStrPart.isEmpty()) {
                if (!var.isEmpty()) { // like "x" or "-x"
                    coeff = 1.0;
                } else { // Empty term, should not happen if split correctly
                    continue;
                }
            } else if (coeffStrPart.equals("-") && !var.isEmpty()){ // handles case from initial split like "-x"
                 coeff = -1.0; // signIsNegative already handled this
            }
            else {
                try {
                    coeff = Double.parseDouble(coeffStrPart);
                } catch (NumberFormatException e) {
                    // If it's just "x", coeffStrPart would be empty. If it's "-x", coeffStrPart is "-".
                    // This path should ideally not be hit if logic above is correct for "x" or "-x".
                    // If coeffStrPart is not empty and not parseable, it's an error.
                     if (!var.isEmpty() && coeffStrPart.equals("")){ // This was "x"
                        coeff = 1.0;
                     } else if (!var.isEmpty() && coeffStrPart.equals("-")) { // This was "-x"
                        coeff = -1.0;
                     }
                     else {
                        throw new IllegalArgumentException("Cannot parse coefficient from term: '" + sTerm + "' in '" + expressionPart + "'");
                     }
                }
            }

            if (signIsNegative) {
                coeff *= -1;
            }
            
            // Default variable to "x" if a variable was implied by exponent but not explicitly named
            if (exp != 0 && var.isEmpty()) {
                var = "x"; 
            }


            p.addTerm(new Term(coeff, var, exp));
        }
        return p;
    }
}
