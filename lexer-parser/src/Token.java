
public class Token {
    String type;
    String value;

    public Token() {
        type = "";
        value = "";
    }

    public Token(String t, String v) {
        this.type = t;
        this.value = v;
    }

    public int length() {
        return value.length();
    }

    @Override
    public String toString() {
        return this.type + " " + this.value;
    }
}
