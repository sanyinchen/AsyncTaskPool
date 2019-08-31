package com.sanyinchen.async.strategy;

/**
 * Created by sanyinchen on 19-8-31.
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-8-31
 */

public interface IBackPressureStrategy {
    void onFullBusy(int maxThreadLimit, int livingThread, int blockingJobs);

    void onIDle(float busyRate, int maxThreadLimit, int livingThread);
}
