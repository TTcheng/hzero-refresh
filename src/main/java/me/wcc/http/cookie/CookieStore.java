package me.wcc.http.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * @author chuncheng.wang@hand-china.com 19-7-22 下午1:55
 */
public interface CookieStore {
    /**
     * 添加cookie
     *
     * @param url    HttpUrl
     * @param cookie Cookie
     */
    void addCookie(HttpUrl url, Cookie cookie);

    /**
     * 获取cookie
     *
     * @param url HttpUrl
     * @return List<Cookie>
     */
    List<Cookie> get(HttpUrl url);
}
