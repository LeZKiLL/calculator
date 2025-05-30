package com.example.calculator.logic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

// Ensure this class is public and in a file named ExpressionEvaluator.java
public class ExpressionEvaluator {

    private static final Map<String, Integer> PRECEDENCE = new HashMap<>();
    static {
        PRECEDENCE.put("+", 1);
        PRECEDENCE.put("-", 1);
        PRECEDENCE.put("*", 2);
        PRECEDENCE.put("/", 2);
        PRECEDENCE.put("^", 3);
        PRECEDENCE.put("~", 4); // Unary minus precedence (higher than power for prefix)
    }

    private static final Map<String, Integer> FUNCTIONS = new HashMap<>();
    static {
        FUNCTIONS.put("sin", 1);
        FUNCTIONS.put("cos", 1);
        FUNCTIONS.put("tan", 1);
        FUNCTIONS.put("log", 1);
        FUNCTIONS.put("ln", 1);
        FUNCTIONS.put("sqrt", 1);
    }

    public double evaluate(String expression) throws IllegalArgumentException {
        if (expression == null || expression.trim().isEmpty()) {
            // Return 0 or throw error for empty expression if display is empty
            // For calculator, an empty press on equals might do nothing or show 0
            return 0.0;
        }
        List<String> tokens = tokenize(expression);
        if (tokens.isEmpty()) return 0.0; // Or handle as error
        List<String> postfix = infixToPostfix(tokens);
        return evaluatePostfix(postfix);
    }

    private List<String> tokenize(String expression) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) continue;

            if (Character.isDigit(c) || c == '.') {
                currentToken.append(c);
            } else if (c == 'E' || c == 'e') { // Scientific notation
                if (currentToken.length() > 0 && Character.isDigit(currentToken.charAt(currentToken.length() -1))) {
                    currentToken.append(c);
                    if (i + 1 < expression.length() && (expression.charAt(i+1) == '+' || expression.charAt(i+1) == '-')) {
                        currentToken.append(expression.charAt(++i));
                    }
                } else { // 'e' as constant
                    if (currentToken.length() > 0) tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                    tokens.add("e"); // Let 'e' be a token for Math.E
                }
            } else if (Character.isLetter(c)) {
                if (currentToken.length() > 0 && !Character.isLetter(currentToken.charAt(currentToken.length() - 1))) {
                    tokens.add(currentToken.toString());
                    currentToken.setLength(0);
                }
                currentToken.append(c);
            } else { // Operators, parentheses
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

    private List<String> preprocessTokensForUnary(List<String> rawTokens) {
        List<String> processed = new ArrayList<>();
        for (int i = 0; i < rawTokens.size(); i++) {
            String token = rawTokens.get(i);
            if (token.equals("-")) {
                boolean isUnary = (i == 0) ||
                                  (isOperator(rawTokens.get(i - 1)) && !rawTokens.get(i-1).equals(")")) || // after operator but not after )
                                  (rawTokens.get(i - 1).equals("("));
                if (isUnary) {
                    // If next token is a number, merge it
                    if (i + 1 < rawTokens.size() && (isNumeric(rawTokens.get(i+1)) || rawTokens.get(i+1).equals("pi") || rawTokens.get(i+1).equals("e")) ) {
                        processed.add("-" + rawTokens.get(i+1));
                        i++; // skip next token
                    } else {
                        processed.add("~"); // Unary minus operator
                    }
                } else {
                    processed.add("-"); // Binary minus
                }
            } else {
                processed.add(token);
            }
        }
        return processed;
    }


    private List<String> infixToPostfix(List<String> tokens) {
        List<String> postfix = new ArrayList<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (isNumeric(token) || token.equalsIgnoreCase("pi") || token.equalsIgnoreCase("e")) {
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
                operatorStack.pop(); // Pop '('
                if (!operatorStack.isEmpty() && FUNCTIONS.containsKey(operatorStack.peek().toLowerCase())) {
                    postfix.add(operatorStack.pop());
                }
            } else if (isOperator(token) || token.equals("~")) {
                // For right-associative ^
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

    private double evaluatePostfix(List<String> postfixTokens) {
        Stack<Double> operandStack = new Stack<>();
        for (String token : postfixTokens) {
            if (isNumeric(token)) {
                operandStack.push(Double.parseDouble(token));
            } else if (token.equalsIgnoreCase("pi")) {
                operandStack.push(Math.PI);
            } else if (token.equalsIgnoreCase("e")) {
                operandStack.push(Math.E);
            } else if (FUNCTIONS.containsKey(token.toLowerCase())) {
                if (operandStack.isEmpty()) throw new IllegalArgumentException("Operand missing for function " + token);
                operandStack.push(applyFunction(token.toLowerCase(), operandStack.pop()));
            } else if (token.equals("~")) { // Unary minus
                if (operandStack.isEmpty()) throw new IllegalArgumentException("Operand missing for unary minus");
                operandStack.push(-operandStack.pop());
            }
            else if (isOperator(token)) {
                if (operandStack.size() < 2) throw new IllegalArgumentException("Operands missing for operator " + token);
                double b = operandStack.pop();
                double a = operandStack.pop();
                operandStack.push(applyOperator(token, a, b));
            } else {
                throw new IllegalArgumentException("Unknown token in RPN: " + token);
            }
        }
        if (operandStack.size() != 1) throw new IllegalArgumentException("Invalid RPN expression structure.");
        return operandStack.pop();
    }

    private double applyFunction(String funcName, double operand) {
        switch (funcName) {
            case "sin": return Math.sin(Math.toRadians(operand));
            case "cos": return Math.cos(Math.toRadians(operand));
            case "tan":
                if (Math.abs(operand % 180) == 90) throw new ArithmeticException("Tan undefined for " + operand + " deg");
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

    private double applyOperator(String op, double a, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            case "^": return Math.pow(a, b);
            default: throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        // Handles negative numbers that were merged by preprocessTokensForUnary
        if (str.startsWith("-") && str.length() > 1) {
            try {
                Double.parseDouble(str.substring(1)); // Check if the rest is numeric
                return true; // If it doesn't throw, then "-number" is valid
            } catch (NumberFormatException e) {
                // It might be just "-" or "-non_numeric"
            }
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(String token) {
        return PRECEDENCE.containsKey(token);
    }

    private int getPrecedence(String token) {
        return PRECEDENCE.getOrDefault(token, 0);
    }
}
