package me.wcc.http;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import me.wcc.http.cookie.ReportsCookieManager;
import me.wcc.http.entity.ResponseEntity;
import me.wcc.http.listener.SpendTimeListener;
import okhttp3.*;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午2:53
 */
@Slf4j
public class HttpClient {
    private final String domain;
    private final OkHttpClient client;

    public HttpClient(String domain, Authenticator authenticator) {
        this.domain = domain;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .authenticator(authenticator)
                .cookieJar(new ReportsCookieManager())
                .eventListener(new SpendTimeListener())
                .build();
    }

    public ResponseEntity get(String uri) {
        Request get = new Request.Builder().url(getDomain() + uri).build();
        Response response = execute(get);
        return processResponse(response);
    }

    public ResponseEntity post(String uri, String body, String contentType) {
        RequestBody requestBody = RequestBody.create(body, MediaType.get(contentType));
        Request post = new Request.Builder().post(requestBody).url(getDomain() + uri).build();
        Response response = execute(post);
        return processResponse(response);
    }

    public ResponseEntity post(String uri) {
        return post(uri, "", "text/plain");
    }

    private ResponseEntity processResponse(Response response) {
        ResponseEntity.ResponseEntityBuilder builder = ResponseEntity.builder();
        builder.success(response.isSuccessful()).statusCode(response.code());
        String bodyString = Optional.ofNullable(response.body()).map(responseBody -> {
            try {
                return responseBody.string();
            } catch (IOException e) {
                log.error("Failed to parse response", e);
                builder.success(false).errorMessage("Error occurred when parsing response, error message:" + e.getMessage());
                return "";
            }
        }).orElse("");
        return builder.body(bodyString).build();
    }

    @SuppressWarnings("WeakerAccess")
    public Response execute(Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            log.error("request failed with uri{}", request.url(), e);
            throw new RuntimeException("request failed, error message:" + e.getMessage());
        }
    }

    @SuppressWarnings("WeakerAccess")
    public String getDomain() {
        return domain;
    }
}

