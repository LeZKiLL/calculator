package com.example.calculator.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExpressionEvaluator {

    // ... (PRECEDENCE and FUNCTIONS maps remain the same)
    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
    static {
        PRECEDENCE.put("+", 1); PRECEDENCE.put("-", 1);
        PRECEDENCE.put("*", 2); PRECEDENCE.put("/", 2);
        PRECEDENCE.put("^", 3); PRECEDENCE.put("~", 4);
    }

    private static final Map<String, Integer> FUNCTIONS = new HashMap<>();
    static {
        FUNCTIONS.put("sin", 1); FUNCTIONS.put("cos", 1); FUNCTIONS.put("tan", 1);
        FUNCTIONS.put("log", 1); FUNCTIONS.put("ln", 1); FUNCTIONS.put("sqrt", 1);
    }


    // Evaluate method now takes angleUnit from SettingsManager via GUI
    public Object evaluate(String expression, boolean preferFractionResult, String angleUnit) throws IllegalArgumentException {
        if (expression == null || expression.trim().isEmpty()) {
            return preferFractionResult ? new Fraction(0) : 0.0;
        }
        List<String> tokens = tokenize(expression);
        if (tokens.isEmpty()) return preferFractionResult ? new Fraction(0) : 0.0;
        List<String> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix, preferFractionResult, angleUnit);
    }

    // Overload for existing calls that don't specify angle unit (default to degrees from SettingsManager)
    public Object evaluate(String expression, boolean preferFractionResult) throws IllegalArgumentException {
        return evaluate(expression, preferFractionResult, SettingsManager.loadAngleUnit());
    }
    // Overload for existing calls that specify neither (default to decimal, and default angle unit)
     public Object evaluate(String expression) throws IllegalArgumentException {
        return evaluate(expression, false, SettingsManager.loadAngleUnit());
    }


    // ... (tokenize, preprocessTokensForUnary, infixToPostfix remain largely the same)
    // Minor change in tokenizer if needed, but likely ok. The main change is in evaluation.
    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (Character.isDigit(c) || c == '.' ||
                (c == '/' && currentToken.length() > 0 && Character.isDigit(currentToken.charAt(currentToken.length()-1)) &&
                 i + 1 < expression.length() && Character.isDigit(expression.charAt(i+1)) &&
                 (i == 0 || !Character.isDigit(expression.charAt(i-1))) && // Heuristic start of fraction
                 (i > 0 && !isOperatorOrParen(expression.charAt(i-1))) 
                )
               ) {
                currentToken.append(c);
            } else if (c == 'E' || c == 'e') {
                if (currentToken.length() > 0 && (Character.isDigit(currentToken.charAt(currentToken.length() -1)) || currentToken.charAt(currentToken.length() -1) == '.' )) {
                    currentToken.append(c);
                    if (i + 1 < expression.length() && (expression.charAt(i+1) == '+' || expression.charAt(i+1) == '-')) {
                        currentToken.append(expression.charAt(++i));
                    }
                } else {
                    if (currentToken.length() > 0) tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                    tokens.add("e");
                }
            } else if (Character.isLetter(c)) {
                if (currentToken.length() > 0 && !Character.isLetter(currentToken.charAt(currentToken.length() - 1))) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                currentToken.append(c);
            } else {
                if (currentToken.length() > 0) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                tokens.add(String.valueOf(c));
            }
        }
        if (currentToken.length() > 0) {
            tokens.add(currentToken.toString());
        }
        return preprocessTokensForUnary(tokens);
    }
    private boolean isOperatorOrParen(char c) { return PRECEDENCE.containsKey(String.valueOf(c)) || c == '(' || c == ')'; }
    private List<String> preprocessTokensForUnary(List<String> rawTokens) {
        List<String> processed = new ArrayList<>();
        for (int i = 0; i < rawTokens.size(); i++) {
            String token = rawTokens.get(i);
            if (token.equals("-")) {
                boolean isUnary = (i == 0) || (isOperator(rawTokens.get(i-1)) && !rawTokens.get(i-1).equals(")")) || (rawTokens.get(i-1).equals("("));
                if (isUnary) {
                    if (i+1 < rawTokens.size() && (isPotentiallyNumericOrFraction(rawTokens.get(i+1)) || rawTokens.get(i+1).equals("pi") || rawTokens.get(i+1).equals("e"))) {
                        processed.add("-" + rawTokens.get(i+1)); i++;
                    } else { processed.add("~"); }
                } else { processed.add("-"); }
            } else { processed.add(token); }
        }
        return processed;
    }
    private boolean isPotentiallyNumericOrFraction(String token) {
        if (token == null || token.isEmpty()) return false;
        return Character.isDigit(token.charAt(0)) || (token.startsWith("-") && token.length() > 1 && Character.isDigit(token.charAt(1))) || token.contains("/");
    }
    private List<String> infixToPostfix(List<String> tokens) {
        List<String> postfix = new ArrayList<>(); Stack<String> operatorStack = new Stack<>();
        for (String token : tokens) {
            if (isOperandToken(token)) { postfix.add(token);
            } else if (FUNCTIONS.containsKey(token.toLowerCase())) { operatorStack.push(token.toLowerCase());
            } else if (token.equals("(")) { operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) postfix.add(operatorStack.pop());
                if (operatorStack.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses: ')'");
                operatorStack.pop(); 
                if (!operatorStack.isEmpty() && FUNCTIONS.containsKey(operatorStack.peek().toLowerCase())) postfix.add(operatorStack.pop());
            } else if (isOperator(token) || token.equals("~")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(") &&
                       ((FUNCTIONS.containsKey(operatorStack.peek().toLowerCase())) ||
                        (isOperator(operatorStack.peek()) && getPrecedence(operatorStack.peek()) > getPrecedence(token)) ||
                        (isOperator(operatorStack.peek()) && getPrecedence(operatorStack.peek()) == getPrecedence(token) && !token.equals("^")))) {
                    postfix.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else { throw new IllegalArgumentException("Invalid token: " + token); }
        }
        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(")) throw new IllegalArgumentException("Mismatched parentheses: '('");
            postfix.add(op);
        }
        return postfix;
    }


    // Updated evaluatePostfix to accept angleUnit
    private Object evaluatePostfix(List<String> postfixTokens, boolean preferFractionResult, String angleUnit) {
        Stack<Object> operandStack = new Stack<>();
        for (String token : postfixTokens) {
            // ... (operand parsing logic for pi, e, fractions, doubles as before)
            if (token.equalsIgnoreCase("pi")) { operandStack.push(Math.PI);
            } else if (token.equalsIgnoreCase("e")) { operandStack.push(Math.E);
            } else if (isOperandToken(token)) {
                try {
                    if (token.matches("-?\\d+/-?\\d+")) { operandStack.push(Fraction.parseFraction(token));
                    } else if (token.matches("-?\\d+") && preferFractionResult && !token.contains(".")) { // Integer string
                        operandStack.push(new Fraction(Long.parseLong(token)));
                    } else { operandStack.push(Double.parseDouble(token));}
                } catch (NumberFormatException e) { throw new IllegalArgumentException("Invalid number/fraction: " + token, e); }
            } else if (FUNCTIONS.containsKey(token.toLowerCase())) {
                if (operandStack.isEmpty()) throw new IllegalArgumentException("Operand missing for " + token);
                Object operandObj = operandStack.pop();
                double operandVal = convertToDouble(operandObj);
                operandStack.push(applyFunction(token.toLowerCase(), operandVal, angleUnit)); // Pass angleUnit
            } else if (token.equals("~")) {
                if (operandStack.isEmpty()) throw new IllegalArgumentException("Operand missing for unary minus");
                Object operandObj = operandStack.pop();
                if (operandObj instanceof Fraction && preferFractionResult) {
                    Fraction f = (Fraction) operandObj; operandStack.push(new Fraction(-f.getNumerator(), f.getDenominator()));
                } else { operandStack.push(-convertToDouble(operandObj)); }
            } else if (isOperator(token)) {
                if (operandStack.size() < 2) throw new IllegalArgumentException("Operands missing for " + token);
                Object bObj = operandStack.pop(); Object aObj = operandStack.pop();
                operandStack.push(applyOperator(token, aObj, bObj, preferFractionResult));
            } else { throw new IllegalArgumentException("Unknown RPN token: " + token); }
        }
        if (operandStack.size() != 1) throw new IllegalArgumentException("Invalid RPN. Stack: " + operandStack.size());
        Object finalResult = operandStack.pop();
        if (!preferFractionResult && finalResult instanceof Fraction) return ((Fraction) finalResult).toDouble();
        return finalResult;
    }

    // Updated applyFunction to accept angleUnit
    private double applyFunction(String funcName, double operand, String angleUnit) {
        double processedOperand = operand;
        if (SettingsManager.ANGLE_UNIT_DEGREES.equals(angleUnit) && (funcName.equals("sin") || funcName.equals("cos") || funcName.equals("tan"))) {
            processedOperand = Math.toRadians(operand);
        }
        // If angleUnit is radians, no conversion needed for Math.sin/cos/tan input

        switch (funcName) {
            case "sin": return Math.sin(processedOperand);
            case "cos": return Math.cos(processedOperand);
            case "tan":
                // Check for undefined cases based on the *original* operand if it was in degrees
                if (SettingsManager.ANGLE_UNIT_DEGREES.equals(angleUnit) && Math.abs(operand % 180) == 90) {
                     throw new ArithmeticException("Tan undefined for " + operand + " deg");
                }
                // For radians, tan is undefined at pi/2 + n*pi.
                // cos(processedOperand) being close to 0 indicates this.
                if (Math.abs(Math.cos(processedOperand)) < 1e-12) { // Check if cos is near zero
                    throw new ArithmeticException("Tan undefined (close to pi/2 + n*pi rad)");
                }
                return Math.tan(processedOperand);
            case "log":
                if (operand <= 0) throw new ArithmeticException("Log domain error");
                return Math.log10(operand);
            case "ln":
                if (operand <= 0) throw new ArithmeticException("Ln domain error");
                return Math.log(operand);
            case "sqrt":
                if (operand < 0) throw new ArithmeticException("Sqrt of negative");
                return Math.sqrt(operand);
            default: throw new IllegalArgumentException("Unknown function: " + funcName);
        }
    }

    // ... (convertToDouble, convertToFraction, applyOperator, isOperandToken, isOperator, getPrecedence, doubleToFraction remain mostly the same)
    // Ensure applyOperator handles preferFractionResult for its return type.
    private double convertToDouble(Object obj) { /* same as before */ 
        if (obj instanceof Fraction) return ((Fraction) obj).toDouble();
        if (obj instanceof Double) return (Double) obj;
        throw new IllegalArgumentException("Cannot convert to double: " + obj.getClass());
    }
    public static Fraction doubleToFraction(double d, long maxDenominator) { /* same as before */
        if (Double.isNaN(d) || Double.isInfinite(d)) throw new IllegalArgumentException("Cannot convert NaN or Infinity to Fraction.");
        if (d == 0.0) return new Fraction(0); long sign = (d < 0) ? -1 : 1; d = Math.abs(d);
        if (d == Math.floor(d)) return new Fraction(sign * (long)d);
        long bestNum = sign * (long)Math.round(d * maxDenominator); long bestDen = maxDenominator;
        double error = Double.MAX_VALUE;
        for (long den = 1; den <= maxDenominator; den++) {
            long num = Math.round(d * den); double currentError = Math.abs(d - ((double)num / den));
            if (currentError < error) { error = currentError; bestNum = sign * num; bestDen = den; }
            if (error == 0) break;
        }
        return new Fraction(bestNum, bestDen);
    }
    private Fraction convertToFraction(Object obj) { /* same as before */
        if (obj instanceof Fraction) return (Fraction) obj;
        if (obj instanceof Double) return doubleToFraction((Double) obj, 1000000);
        throw new IllegalArgumentException("Cannot convert to fraction: " + obj.getClass());
    }
    private Object applyOperator(String op, Object aObj, Object bObj, boolean preferFractionResult) { /* same as before (respects preferFractionResult) */
         if (preferFractionResult) {
            Fraction fracA = convertToFraction(aObj); Fraction fracB = convertToFraction(bObj);
            switch (op) {
                case "+": return fracA.add(fracB); case "-": return fracA.subtract(fracB);
                case "*": return fracA.multiply(fracB); case "/": return fracA.divide(fracB);
                case "^":
                    double baseDouble = fracA.toDouble(); double expDouble = fracB.toDouble();
                    if (fracB.getDenominator() == 1) {
                        long exponent = fracB.getNumerator(); if (exponent == 0) return new Fraction(1);
                        Fraction termA = fracA; // Use the original fraction for base
                        if (exponent > 0) {
                            Fraction res = new Fraction(1); for (int i=0; i<exponent; i++) res=res.multiply(termA); return res;
                        } else {
                            Fraction res = new Fraction(1); for (int i=0; i<Math.abs(exponent); i++) res=res.multiply(termA);
                            if (res.getNumerator() == 0) throw new ArithmeticException("0 to negative power");
                            return new Fraction(res.getDenominator(), res.getNumerator());
                        }
                    }
                    return Math.pow(baseDouble, expDouble);
                default: throw new IllegalArgumentException("Unknown op: " + op);
            }
        } else { /* decimal mode logic - same as before */
            double valA = convertToDouble(aObj); double valB = convertToDouble(bObj);
            switch (op) {
                case "+": return valA + valB; case "-": return valA - valB; case "*": return valA * valB;
                case "/": if (valB == 0) throw new ArithmeticException("Div by zero"); return valA / valB;
                case "^": return Math.pow(valA, valB);
                default: throw new IllegalArgumentException("Unknown op: " + op);
            }
        }
    }
    private boolean isOperandToken(String token) { /* same as before */
        if (token == null || token.isEmpty()) return false;
        if (token.equalsIgnoreCase("pi") || token.equalsIgnoreCase("e")) return true;
        return token.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?") || token.matches("-?\\d+/-?\\d+");
    }
    private boolean isOperator(String token) { return PRECEDENCE.containsKey(token); }
    private int getPrecedence(String token) { return PRECEDENCE.getOrDefault(token, 0); }
}
