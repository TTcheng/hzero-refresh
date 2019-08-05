package me.wcc.http.listener;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.EventListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author chuncheng.wang@hand-china.com 2019/8/3 下午2:56
 */
@Slf4j
public class SpendTimeListener extends EventListener {
    private long callStartMillis = 0L;

    @Override
    public void callStart(@NotNull Call call) {
        if (log.isDebugEnabled()) {
            callStartMillis = Instant.now().toEpochMilli();
        }
    }

    @Override
    public void callEnd(@NotNull Call call) {
        if (log.isDebugEnabled()) {
            log.debug("{} spent {} millis", call.request().url(), Instant.now().toEpochMilli() - callStartMillis);
        }
    }
}
