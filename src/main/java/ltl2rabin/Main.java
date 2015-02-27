package ltl2rabin;

public class Main {
    private static int stringPosition = 0;

    public static void main(String[] args) {
        System.out.println("Main method stub");
        System.out.println("1+1=2");
    }

    /* Not working trivial attempt to create a parser. Does not recognize operator priorities.

    public static LTLFormula parseLTLFormula(String s, LTLFormula carriedFormula) {
        System.out.println("carriedFormula: " + carriedFormula);
        if (stringPosition >= s.length()) {
            return carriedFormula;
        }
        char c = s.charAt(stringPosition++);
        if (Character.isWhitespace(c)) {
            return parseLTLFormula(s, carriedFormula);
        }
        System.out.println("Parsed character: " + c);
        switch (c) {
            case 'F':
                return new LTLFOperator(parseLTLFormula(s, carriedFormula));
            case 'G':
                return new LTLGOperator(parseLTLFormula(s, carriedFormula));
            case 'X':
                return new LTLXOperator(parseLTLFormula(s, carriedFormula));
            case '(':
                LTLFormula withinParentheses = parseLTLFormula(s, null);
                System.out.println("Within Parentheses: " + withinParentheses);
                return parseLTLFormula(s, withinParentheses);
            case ')':
                return carriedFormula;
            case 'U':
                return new LTLUOperator(carriedFormula, parseLTLFormula(s, null));
            case '&':
                return new LTLAnd(carriedFormula, parseLTLFormula(s, null));
            case '|':
                return new LTLOr(carriedFormula, parseLTLFormula(s, null));
            case '!':
                LTLVariable variable = new LTLVariable(parseVariableName(s), true);
                return parseLTLFormula(s, variable);
            default:
                --stringPosition;
                String nextToken = parseVariableName(s);
                if (nextToken.equals("tt")) {
                    if (carriedFormula == null) return parseLTLFormula(s, new LTLBoolean(true));
                    else return new LTLBoolean(true);
                }
                else if (nextToken.equals("ff")) {
                    if (carriedFormula == null) return parseLTLFormula(s, new LTLBoolean(false));
                    else return new LTLBoolean(false);
                }
                else {
                    if (carriedFormula == null) return parseLTLFormula(s, new LTLVariable(nextToken));
                    else return new LTLVariable(nextToken);
                }
        }
    }
     */

    private static String parseVariableName(String s) {
        int stringLength = s.length();
        String result = "";
        while (stringPosition < stringLength) {
            char c = s.charAt(stringPosition++);
            if (Character.isLetter(c)) result = result + c;
            else break;
        }
        return result;
    }

    /**
     * This method should be called whenever a formula has been parsed.
     */
    public static void resetStringPosition() {
        stringPosition = 0;
    }
}
