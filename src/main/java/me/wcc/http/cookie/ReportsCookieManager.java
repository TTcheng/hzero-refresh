package me.wcc.http.cookie;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chuncheng.wang@hand-china.com 19-7-22 上午11:48
 */
public class ReportsCookieManager implements CookieJar {
    private final CookieStore cookieStore;

    public ReportsCookieManager() {
        cookieStore = new OkHttpCookieStore();
    }

    @SuppressWarnings("unused")
    public ReportsCookieManager(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
    }

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        return cookieStore.get(httpUrl);
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> cookieList) {
        if (!CollectionUtils.isEmpty(cookieList)) {
            cookieList.forEach(cookie -> cookieStore.addCookie(httpUrl, cookie));
        }
    }
}
