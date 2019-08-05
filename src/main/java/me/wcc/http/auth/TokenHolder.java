package me.wcc.http.auth;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午4:20
 */
@SuppressWarnings("WeakerAccess")
public class TokenHolder {
    private TokenHolder() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    private static ConcurrentHashMap<String, Token> tokens = new ConcurrentHashMap<>(4);

    public static Token token(String domain) {
        return tokens.get(domain);
    }

    public static void token(Token token, String domain) {
        tokens.put(domain, token);
    }
}
