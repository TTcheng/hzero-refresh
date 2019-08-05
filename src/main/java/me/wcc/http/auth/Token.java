package me.wcc.http.auth;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;
import me.wcc.util.JsonUtils;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午4:18
 */
@SuppressWarnings("WeakerAccess")
@Getter
public class Token {
    private String value;
    private String type;
    private String refreshToken;
    private Long expiresIn;
    private String scope;
    private final Instant startTime;

    public Token(String value, String type, String refreshToken, Long expiresIn, String scope) {
        this.value = value;
        this.type = type;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.scope = scope;
        this.startTime = Instant.now().minusMillis(1000);
    }

    @SuppressWarnings("unchecked")
    public static Token fromJson(String jsonString) {
        Map<String, Object> map = JsonUtils.GSON.fromJson(jsonString, Map.class);
        String token = (String) map.get("access_token");
        String tokenType = (String) map.get("token_type");
        String refreshToken = (String) map.get("refresh_token");
        String expiresIn = String.valueOf(map.get("expires_in"));
        int index = expiresIn.indexOf('.');
        expiresIn = expiresIn.substring(0, Optional.of(index).filter(i -> i > 0).orElse(expiresIn.length() - 1));
        String scope = (String) map.get("scope");
        if (null != token) {
            return new Token(token, tokenType, refreshToken, Long.parseLong(expiresIn), scope);
        }
        return null;
    }

    @Override
    public String toString() {
        return type + ' ' + value;
    }

    public Boolean isExpired() {
        return startTime.plusSeconds(expiresIn).isBefore(Instant.now());
    }
}
