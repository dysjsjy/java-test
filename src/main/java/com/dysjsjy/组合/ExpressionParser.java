package com.dysjsjy.组合;


import java.util.*;

// 中缀表达式转后缀表达式
public class ExpressionParser {

    private final String infixExpression;
    int point = 0;


    public ExpressionParser(String infixExpression) {
        this.infixExpression = infixExpression;
    }

    public List<String> toSuffix() {
        List<String> suffix = new ArrayList<>();
        LinkedList<String> stack = new LinkedList<>();

        while (point < infixExpression.length()) {
            char c = infixExpression.charAt(point);
            if (c == '(') {
                stack.addLast(c + "");
            } else if (c == ')') {
                while (!stack.getLast().equals("(")) {
                    suffix.add(stack.removeLast());
                }
                stack.removeLast();
            } else if (c == '*' || c == '/') {
                while ((!stack.isEmpty()) && (stack.getLast().equals("*") || stack.getLast().equals("/"))) {
                    suffix.add(stack.removeLast());
                }
                stack.addLast(c + "");
            } else if (c == '+' || c == '-') {
                while (topIsOperator(stack)) {
                    suffix.add(stack.removeLast());
                }
                stack.addLast(c + "");
            } else if (Character.isDigit(c)) {
                StringBuilder stringBuilder = new StringBuilder();
                while (point < infixExpression.length() && Character.isDigit(infixExpression.charAt(point))) {
                    stringBuilder.append(infixExpression.charAt(point));
                    point++;
                }
                point--;
                suffix.add(stringBuilder.toString());
            } else {
                throw new IllegalStateException("非法字符！");
            }
            point++;
        }

        while (!stack.isEmpty()) {
            suffix.add(stack.removeLast());
        }

        return suffix;
    }

    public Expression parse() {
        List<String> suffix = this.toSuffix();
        LinkedList<Expression> stack = new LinkedList<>();
        for (String item : suffix) {
            if (item.equals("+")) {
                Expression right = stack.removeLast();
                stack.addLast(new AddExpression(stack.removeLast(), right));
            } else if (item.equals("-")) {
                Expression right = stack.removeLast();
                stack.addLast(new MinusExpression(stack.removeLast(), right));
            } else if (item.equals("*")) {
                Expression right = stack.removeLast();
                stack.addLast(new MutiplyExpression(stack.removeLast(), right));
            } else if (item.equals("/")) {
                Expression right = stack.removeLast();
                stack.addLast(new DevisionExpression(stack.removeLast(), right));
            } else {
                int value = Integer.parseInt(item);
                stack.addLast(new NumberExpression(value));
            }
        }
        return stack.getLast();
    }

    private boolean topIsOperator(LinkedList<String> stack) {
        if (stack.isEmpty()) {
            return false;
        }

        Set<String> operators = new HashSet<>();
        operators.add("+");
        operators.add("-");
        operators.add("*");
        operators.add("/");
        return operators.contains(stack.getLast());
    }
}
