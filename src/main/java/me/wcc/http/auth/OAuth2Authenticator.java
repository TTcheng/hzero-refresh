package me.wcc.http.auth;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午3:53
 */
@Slf4j
public class OAuth2Authenticator implements Authenticator {
    private static final String AUTH_HEADER = "Authorization";
    private final String authUrl;
    private final AuthParams authParams;

    public OAuth2Authenticator(String authUrl, AuthParams authParams) {
        this.authUrl = authUrl;
        this.authParams = authParams;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NotNull Response response) {
        Request request = response.request();
        if (request.header(AUTH_HEADER) != null) {
            // Give up, we've already tried
            log.warn("Authenticate failed for {}", request.url());
            return null;
        }
        String domain = request.url().host() + ':' + request.url().port();
        Token token = TokenHolder.token(domain);
        log.debug("Authenticating for {}, Challenges:{}", request.url(), response.challenges());
        if (null == token || token.isExpired()) {
            // get or renew token first
            Token newToken = requestForToken();
            if (null == newToken) {
                log.error("Failed to fetch token!");
                return null;
            }
            token = newToken;
            TokenHolder.token(token, domain);
        }
        return request.newBuilder()
                .header(AUTH_HEADER, token.toString())
                .build();
    }

    private Token requestForToken() {
        FormBody formBody = new FormBody.Builder()
                .add(AuthParams.KEY_USERNAME, authParams.getUsername())
                .add(AuthParams.KEY_PASSWORD, authParams.getPassword())
                .add(AuthParams.KEY_CLIENT_ID, authParams.getClientId())
                .add(AuthParams.KEY_CLIENT_SECRET, authParams.getClientSecret())
                .add(AuthParams.KEY_SCOPE, authParams.getScope())
                .add(AuthParams.KEY_GRANT_TYPE, AuthParams.GRANT_TYPE)
                .build();
        Request request = new Request.Builder().post(formBody).url(authUrl).build();
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            ResponseBody body = response.body();
            if (response.isSuccessful() && null != body) {
                String jsonString = body.string();
                return Token.fromJson(jsonString);
            }
            return null;
        } catch (IOException e) {
            log.error("Get token failed when executing token request!", e);
        }
        return null;
    }
}
