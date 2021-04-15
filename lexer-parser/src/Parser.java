
import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class ExpressionTree {
    Node root;
    static HashMap<String, Integer> precedence;
    static {
        precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);
        precedence.put("^", 2);
    }


    public ExpressionTree() {
        root = null;
    }




    public ExpressionTree(Token t) {
        this.root = new Node(t);
        this.root.left = null;
        this.root.right = null;
    }


    public static class Node {
        String type;
        String val;
        Node left;
        Node right;


        Node() {
            type = "";
            val = "";
            left = null;
            right = null;
        }


        public Node(String type, String val, Node left, Node right) {
            this.type = type;
            this.val = val;
            this.left = left;
            this.right = right;
        }


        Node(Token t) {
            this.type = t.type;
            this.val = t.value;
        }

        public String toString() {
            String printTree = this.type + ":" + this.val ;
            if (this.left != null){
                printTree += "\nLeft Tree: " + this.left.toString();
            }
            if (this.right != null){
                printTree += "\nRight Tree: " + this.right.toString();
            }
            return printTree;
        }


        public double eval(Lexeme table) {
            switch (this.type) {
                case Lexer.FLOAT:
                case Lexer.INT:
                    return Double.parseDouble(this.val);
                case Lexer.IDENTIFER:
                    if (table.has(this.val)) {
                        return table.getValue(this.val);
                    } else if (table.hasFunction(this.val)) {
                        return table.getFunction(this.val).evaluate(table);
                    } else {
                        throw new IllegalArgumentException(this.val + " is not in symbol table");
                    }
                case Lexer.OPERATOR:
                    double lhs = left.eval(table);
                    double rhs = right.eval(table);
                    switch (this.val) {
                        case "+":
                            return lhs + rhs;
                        case "-":
                            return lhs - rhs;
                        case "/":
                            return lhs / rhs;
                        case "*":
                            return lhs * rhs;
                        case "^":
                            return Math.pow(lhs, rhs);
                        default:
                            System.out.println("Unknown Operator");
                            return 0.0; // cb - probably better to throw an exception here.
                    }
                default:
                    throw new IllegalArgumentException("Evaluation error " + this.toString());
            }
        }


        @Override
        public boolean equals(Object other){
            if (other instanceof Node){
                Node object = (Node) other;
                boolean flag_left = true;
                boolean flag_right = true;
                if (this.left == null) {
                    if (object.left != null) {
                        flag_left = false;
                    }
                } else {
                    flag_left = this.left.equals(object.left);
                }
                if (this.right == null) {
                    if (object.right != null) {
                        flag_right = false;
                    }
                } else {
                    flag_right = this.right.equals(object.right);
                }
                return this.type.equals(object.type) && this.val.equals(object.val) && flag_left && flag_right;
            }  else {
                return false;
            }
        }
    }


    public static void parseIdentifier(Token t) {
        if (t == null){
            new Node();
        } else if (t.type.equals(Lexer.IDENTIFER)) {
            new Node(t);
        } else {
            throw new IllegalArgumentException("Parse error: " + t);
        }
    }


    public static void parseAssignmentOp(Token t) {
        if (t == null){
            new Node();
        } else if (t.type.equals(Lexer.ASSIGNMENT)) {
            new Node(t);
        } else {
            throw new IllegalArgumentException("Parse error: " + t);
        }
    }


    public static Node parseOperator(Token t) {
        if (t == null){
            return new Node();
        } else if (t.type.equals(Lexer.OPERATOR)) {
            return new Node(t);
        } else {
            throw new IllegalArgumentException("Parse error: " + t);
        }
    }


    public static void parseExprOperator(Token t) {
        if (t == null){
            new Node();
        } else if (t.type.equals(Lexer.EXPRASSIGNMENT)) {
            new Node(t);
        } else {
            throw new IllegalArgumentException("Parse error: " + t);
        }
    }


    public static Node parseNumber(Token t) {
        if (t == null){
            return new Node();
        } else if (t.type.equals(Lexer.FLOAT) || t.type.equals(Lexer.INT)){
            return new Node(t);
        } else {
            throw new IllegalArgumentException("Parse error: " + t);
        }
    }


    public static Node parseNumberOrIdentifier(Token t) {
        if (t == null){
            return new Node();
        } else if (t.type.equals(Lexer.FLOAT) || t.type.equals(Lexer.INT) || t.type.equals(Lexer.IDENTIFER)) {
            return new Node(t);
        } else {
            throw new IllegalArgumentException("Parse error: " + t);
        }
    }


    public static boolean hasPrecedence(String op1, String op2) {
        return precedence.get(op1) >= precedence.get(op2);
    }


    public static Node parseExpression(List<Token> tokenList) {
        Stack<Node> operators = new Stack<>();
        Stack<Node> operands = new Stack<>();
        for(Token t : tokenList) {
            if (t.type.equals(Lexer.FLOAT) || t.type.equals(Lexer.INT) || t.type.equals(Lexer.IDENTIFER)) {
                operands.push(parseNumberOrIdentifier(t));
            } else if (t.type.equals(Lexer.OPERATOR)){
                if (!operators.isEmpty() && !ExpressionTree.hasPrecedence(t.value, operators.peek().val)) {
                    while (!operators.isEmpty()) {
                        Node leftOperand = operands.pop();
                        Node rightOperand = operands.pop();
                        Node treeOperator = operators.pop();
                        treeOperator.left = leftOperand;
                        treeOperator.right = rightOperand;
                        operands.push(treeOperator);
                    }
                }
                operators.push(parseOperator(t));
            }
        }
        while (!operators.isEmpty()) {
            Node operator = operators.pop();
            Node rightOperand = operands.pop();
            operator.left = operands.pop();
            operator.right = rightOperand;
            operands.push(operator);
        }
        return operands.peek();
    }



    public static Node parseAssignment(List<Token> tokenList, Lexeme table) {
        Node n;
        parseIdentifier(tokenList.get(0));
        parseAssignmentOp(tokenList.get(1));
        n = parseExpression(tokenList.subList(2, tokenList.size()));
        Double result = n.eval(table);
        table.storeValue(tokenList.get(0).value, result);
        return n;
    }


    public static Node parseExprAssignment(List<Token> tokenList, Lexeme table) {
        Node n;
        parseIdentifier(tokenList.get(0));
        parseExprOperator(tokenList.get(1));
        n = parseExpression(tokenList.subList(2, tokenList.size()));
        ExpressionTree et = new ExpressionTree();
        et.root = n;
        table.storeFunction(tokenList.get(0).value, et);
        return n;
    }


    public static Node parseTokens(List<Token> tokenList, Lexeme table) {
        Node n;
        if (tokenList.size() == 1) {
            if (tokenList.get(0).type.equals(Lexer.INT) || tokenList.get(0).type.equals(Lexer.FLOAT) ||
                    tokenList.get(0).type.equals(Lexer.IDENTIFER)) {
                n = parseNumberOrIdentifier(tokenList.get(0));
            } else {
                throw new IllegalArgumentException("Unexpected token");
            }
        } else if (tokenList.get(0).type.equals(Lexer.IDENTIFER) && tokenList.get(1).type.equals(Lexer.ASSIGNMENT)) {
            n = parseAssignment(tokenList, table);
        } else if (tokenList.get(0).type.equals(Lexer.IDENTIFER) && tokenList.get(1).type.equals(Lexer.EXPRASSIGNMENT)){
            n = parseExprAssignment(tokenList, table);
        } else {
            n = parseExpression(tokenList);
        }
        if(n == null){
            System.out.println("Parse error");
        }
        return n;
    }

    public void parse(List<Token> tokenList, Lexeme table) {
        root = parseTokens(tokenList, table);
    }


    public double evaluate(Lexeme table) {
        return root.eval(table);
    }


    @Override
    public String toString() {
        return "\nRoot: " + root ;
    }
}
