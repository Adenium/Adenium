package org.wolkenproject.core.papaya.compiler;

public class Token {
    private final String    tokenValue;
    private final TokenType tokenType;
    private final int       line;
    private final int       offset;

    public Token(String value, TokenType type, int line, int offset) {
        this.tokenValue = value;
        this.tokenType  = type;
        this.line       = line;
        this.offset     = offset;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenValue='" + tokenValue + '\'' +
                ", tokenType=" + tokenType +
                ", line=" + line +
                ", offset=" + offset +
                '}';
    }
}
