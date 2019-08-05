package me.wcc.http.cookie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * @author chuncheng.wang@hand-china.com 19-7-22 下午1:57
 */
@SuppressWarnings("WeakerAccess")
public class OkHttpCookieStore implements CookieStore {
    Map<String, ConcurrentHashMap<String, Cookie>> cookies;

    public OkHttpCookieStore() {
        cookies = new HashMap<>();
    }

    @Override
    public void addCookie(HttpUrl url, Cookie cookie) {
        String cookieKey = getCookieKey(cookie);
        String host = url.host();
        // 将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.persistent()) {
            if (!cookies.containsKey(url.host())) {
                cookies.put(host, new ConcurrentHashMap<>(4));
            }
            cookies.get(host).put(cookieKey, cookie);
        } else {
            if (cookies.containsKey(url.host())) {
                cookies.get(host).remove(cookieKey);
            }
        }
    }

    @Override
    public List<Cookie> get(HttpUrl url) {
        if (!cookies.containsKey(url.host())) {
            return Collections.emptyList();
        }
        return new ArrayList<>(cookies.get(url.host()).values());
    }

    private String getCookieKey(Cookie cookie) {
        return cookie.name() + "@" + cookie.domain();
    }
}
