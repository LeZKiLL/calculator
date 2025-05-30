package com.example.calculator.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class ExpressionEvaluator {

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

    // New: evaluate method now takes a mode preference
    public Object evaluate(String expression, boolean preferFractionResult) throws IllegalArgumentException {
        if (expression == null || expression.trim().isEmpty()) {
            return preferFractionResult ? new Fraction(0) : 0.0;
        }
        List<String> tokens = tokenize(expression);
        if (tokens.isEmpty()) return preferFractionResult ? new Fraction(0) : 0.0;
        List<String> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix, preferFractionResult);
    }

    // Overload for backward compatibility or if mode is not specified (defaults to decimal)
    public Object evaluate(String expression) throws IllegalArgumentException {
        return evaluate(expression, false); // Default to not preferring fraction result
    }


    private List<String> tokenize(String expression) {
        // Tokenizer logic remains largely the same as the one that handles "n/m" strings
        // The distinction of "3/4" as a fraction vs. division operator happens more in evaluatePostfix
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isWhitespace(c)) continue;

            if (Character.isDigit(c) || c == '.' ||
                (c == '/' && currentToken.length() > 0 && Character.isDigit(currentToken.charAt(currentToken.length()-1)) &&
                 i + 1 < expression.length() && Character.isDigit(expression.charAt(i+1)) &&
                 !isOperatorOrParen(expression.charAt(i-1)) && // Heuristic: not (expr)/(expr)
                 (i + 2 >= expression.length() || !isOperatorOrParen(expression.charAt(i+2))) // Heuristic
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
    
    private boolean isOperatorOrParen(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '(' || c == ')';
    }


    private List<String> preprocessTokensForUnary(List<String> rawTokens) {
        List<String> processed = new ArrayList<>();
        for (int i = 0; i < rawTokens.size(); i++) {
            String token = rawTokens.get(i);
            if (token.equals("-")) {
                boolean isUnary = (i == 0) ||
                                  (isOperator(rawTokens.get(i - 1)) && !rawTokens.get(i-1).equals(")")) ||
                                  (rawTokens.get(i - 1).equals("("));
                if (isUnary) {
                    if (i + 1 < rawTokens.size() && (isPotentiallyNumericOrFraction(rawTokens.get(i+1)) || rawTokens.get(i+1).equals("pi") || rawTokens.get(i+1).equals("e")) ) {
                        processed.add("-" + rawTokens.get(i+1));
                        i++;
                    } else {
                        processed.add("~");
                    }
                } else {
                    processed.add("-");
                }
            } else {
                processed.add(token);
            }
        }
        return processed;
    }

    private boolean isPotentiallyNumericOrFraction(String token) {
        if (token == null || token.isEmpty()) return false;
        return Character.isDigit(token.charAt(0)) ||
               (token.startsWith("-") && token.length() > 1 && Character.isDigit(token.charAt(1))) ||
               token.contains("/");
    }

    private List<String> infixToPostfix(List<String> tokens) {
        // Infix to Postfix logic remains the same
        List<String> postfix = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();
        for (String token : tokens) {
            if (isOperandToken(token)) {
                postfix.add(token);
            } else if (FUNCTIONS.containsKey(token.toLowerCase())) {
                operatorStack.push(token.toLowerCase());
            } else if (token.equals("(")) {
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    postfix.add(operatorStack.pop());
                }
                if (operatorStack.isEmpty()) throw new IllegalArgumentException("Mismatched parentheses: ')' without '('.");
                operatorStack.pop(); 
                if (!operatorStack.isEmpty() && FUNCTIONS.containsKey(operatorStack.peek().toLowerCase())) {
                    postfix.add(operatorStack.pop());
                }
            } else if (isOperator(token) || token.equals("~")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(") &&
                       ( (FUNCTIONS.containsKey(operatorStack.peek().toLowerCase())) ||
                         (isOperator(operatorStack.peek()) && getPrecedence(operatorStack.peek()) > getPrecedence(token)) ||
                         (isOperator(operatorStack.peek()) && getPrecedence(operatorStack.peek()) == getPrecedence(token) && !token.equals("^"))
                       )
                      ) {
                    postfix.add(operatorStack.pop());
                }
                operatorStack.push(token);
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }
        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(")) throw new IllegalArgumentException("Mismatched parentheses: '(' without ')'.");
            postfix.add(op);
        }
        return postfix;
    }

    // New: evaluatePostfix now takes a mode preference
    private Object evaluatePostfix(List<String> postfixTokens, boolean preferFractionResult) {
        Stack<Object> operandStack = new Stack<>();

        for (String token : postfixTokens) {
            if (token.equalsIgnoreCase("pi")) {
                operandStack.push(Math.PI);
            } else if (token.equalsIgnoreCase("e")) {
                operandStack.push(Math.E);
            } else if (isOperandToken(token)) {
                try {
                    if (token.matches("-?\\d+/-?\\d+")) { // Explicit fraction string "n/m" or "-n/m" etc.
                        operandStack.push(Fraction.parseFraction(token));
                    } else if (token.matches("-?\\d+([eE][+-]?\\d+)?") && preferFractionResult && !token.contains(".")) { // Integer string, prefer fraction
                        operandStack.push(new Fraction(Long.parseLong(token.split("[eE]")[0]))); // Handle potential E notation for large integers
                    }
                    else { // Decimal or integer string in decimal mode
                        operandStack.push(Double.parseDouble(token));
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number/fraction format: " + token, e);
                }
            } else if (FUNCTIONS.containsKey(token.toLowerCase())) {
                if (operandStack.isEmpty()) throw new IllegalArgumentException("Operand missing for function " + token);
                Object operandObj = operandStack.pop();
                double operandVal = convertToDouble(operandObj);
                operandStack.push(applyFunction(token.toLowerCase(), operandVal)); // Functions return Double
            } else if (token.equals("~")) { // Unary minus
                if (operandStack.isEmpty()) throw new IllegalArgumentException("Operand missing for unary minus");
                Object operandObj = operandStack.pop();
                if (operandObj instanceof Fraction && preferFractionResult) {
                    Fraction f = (Fraction) operandObj;
                    operandStack.push(new Fraction(-f.getNumerator(), f.getDenominator()));
                } else {
                    operandStack.push(-convertToDouble(operandObj));
                }
            } else if (isOperator(token)) {
                if (operandStack.size() < 2) throw new IllegalArgumentException("Operands missing for operator " + token);
                Object bObj = operandStack.pop();
                Object aObj = operandStack.pop();
                operandStack.push(applyOperator(token, aObj, bObj, preferFractionResult));
            } else {
                throw new IllegalArgumentException("Unknown token in RPN: " + token);
            }
        }
        if (operandStack.size() != 1) throw new IllegalArgumentException("Invalid RPN. Stack size: " + operandStack.size());
        
        Object finalResult = operandStack.pop();
        // If in decimal mode and result is fraction, convert
        if (!preferFractionResult && finalResult instanceof Fraction) {
            return ((Fraction) finalResult).toDouble();
        }
        return finalResult;
    }

    private double convertToDouble(Object obj) {
        if (obj instanceof Fraction) return ((Fraction) obj).toDouble();
        if (obj instanceof Double) return (Double) obj;
        throw new IllegalArgumentException("Cannot convert to double: " + obj.getClass());
    }

    // New: applyOperator now takes mode preference
    private Object applyOperator(String op, Object aObj, Object bObj, boolean preferFractionResult) {
        if (preferFractionResult) {
            Fraction fracA = convertToFraction(aObj);
            Fraction fracB = convertToFraction(bObj);
            switch (op) {
                case "+": return fracA.add(fracB);
                case "-": return fracA.subtract(fracB);
                case "*": return fracA.multiply(fracB);
                case "/": return fracA.divide(fracB);
                case "^": // Power with fractions
                    double baseDouble = fracA.toDouble(); // For non-integer exponents, use double
                    double expDouble = fracB.toDouble();
                    if (fracB.getDenominator() == 1) { // Integer exponent
                        long exponent = fracB.getNumerator();
                        if (exponent == 0) return new Fraction(1);
                        if (exponent > 0) {
                            Fraction res = new Fraction(1);
                            for (int i = 0; i < exponent; i++) res = res.multiply(fracA);
                            return res;
                        } else { // Negative integer exponent
                            Fraction res = new Fraction(1);
                            for (int i = 0; i < Math.abs(exponent); i++) res = res.multiply(fracA);
                            if (res.getNumerator() == 0) throw new ArithmeticException("0 to negative power");
                            return new Fraction(res.getDenominator(), res.getNumerator());
                        }
                    }
                    return Math.pow(baseDouble, expDouble); // Fallback to double for non-integer fractional exponent
                default: throw new IllegalArgumentException("Unknown operator: " + op);
            }
        } else { // Decimal mode
            double valA = convertToDouble(aObj);
            double valB = convertToDouble(bObj);
            switch (op) {
                case "+": return valA + valB;
                case "-": return valA - valB;
                case "*": return valA * valB;
                case "/":
                    if (valB == 0) throw new ArithmeticException("Division by zero");
                    return valA / valB;
                case "^": return Math.pow(valA, valB);
                default: throw new IllegalArgumentException("Unknown operator: " + op);
            }
        }
    }
    
    // Static helper for converting double to fraction (used by GUI too)
    // maxDenominator helps control precision/complexity of the resulting fraction
    public static Fraction doubleToFraction(double d, long maxDenominator) {
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            throw new IllegalArgumentException("Cannot convert NaN or Infinity to Fraction.");
        }
        if (d == 0.0) return new Fraction(0);

        long sign = (d < 0) ? -1 : 1;
        d = Math.abs(d);

        // Continued fraction method (simplified version)
        // For a more robust solution, a library or more complex algorithm is needed.
        // This is a basic approach.
        if (d == Math.floor(d)) { // It's a whole number
            return new Fraction(sign * (long)d);
        }

        // Simple approach: multiply by a large power of 10 up to maxDenominator
        long bestNum = sign * (long)Math.round(d * maxDenominator);
        long bestDen = maxDenominator;
        
        // Try to find a simpler fraction by iterating denominators
        // This can be computationally intensive if maxError is too small or maxDenominator too large
        double error = Double.MAX_VALUE;
        for (long den = 1; den <= maxDenominator; den++) {
            long num = Math.round(d * den);
            double currentError = Math.abs(d - ((double)num / den));
            if (currentError < error) {
                error = currentError;
                bestNum = sign * num;
                bestDen = den;
            }
            if (error == 0) break; // Perfect match
        }
        return new Fraction(bestNum, bestDen); // Will be simplified
    }


    private Fraction convertToFraction(Object obj) {
        if (obj instanceof Fraction) return (Fraction) obj;
        if (obj instanceof Double) return doubleToFraction((Double) obj, 1000000); // Max Denom for internal conversion
        throw new IllegalArgumentException("Cannot convert to fraction: " + obj.getClass());
    }

    private double applyFunction(String funcName, double operand) {
        // Function application logic remains the same (operates on and returns doubles)
        switch (funcName) {
            case "sin": return Math.sin(Math.toRadians(operand));
            case "cos": return Math.cos(Math.toRadians(operand));
            case "tan":
                if (Math.abs(operand % 180) == 90) throw new ArithmeticException("Tan undefined");
                return Math.tan(Math.toRadians(operand));
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

    private boolean isOperandToken(String token) {
        if (token == null || token.isEmpty()) return false;
        if (token.equalsIgnoreCase("pi") || token.equalsIgnoreCase("e")) return true;
        return token.matches("-?\\d+(\\.\\d+)?([eE][+-]?\\d+)?") ||
               token.matches("-?\\d+/-?\\d+");
    }

    private boolean isOperator(String token) {
        return PRECEDENCE.containsKey(token);
    }

    private int getPrecedence(String token) {
        return PRECEDENCE.getOrDefault(token, 0);
    }
}
