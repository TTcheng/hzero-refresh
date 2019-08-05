package me.wcc.http.auth;

import java.util.Base64;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午5:03
 */
@Getter
@AllArgsConstructor
public class AuthParams {
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_CLIENT_SECRET = "client_secret";
    public static final String KEY_SCOPE = "scope";
    public static final String KEY_GRANT_TYPE = "grant_type";
    /**
     * 只支持用户名密码的认证模式
     */
    public static final String GRANT_TYPE = KEY_PASSWORD;

    public AuthParams(String username, String password, String clientId, String clientSecret) {
        this.username = username;
        byte[] encode = Base64.getEncoder().encode(password.getBytes());
        this.password = new String(encode);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private String scope = "default";
}
