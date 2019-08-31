package com.sanyinchen.async;

import com.sanyinchen.async.basic.BasicTaskDispatchPool;
import com.sanyinchen.async.strategy.IBackPressureStrategy;

/**
 * Created by sanyinchen on 19-8-31.
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-8-31
 */

public abstract class BackPressureTaskDispatchPool<T, O> extends BasicTaskDispatchPool<T, O> implements IBackPressureStrategy {

    @Override
    public BasicTaskDispatchPool addTask(T task) {
        strategyInvoke();
        return super.addTask(task);
    }

    @Override
    protected O runTask(T arg) {
        strategyInvoke();
        return null;
    }

    private void strategyInvoke() {
        int blockingJobs = getBlockingJobs();
        int livingThread = getLivingThread();
        int maxThreadLimit = getMaxThreadLimit();
        float busyRate = (float) livingThread / (float) maxThreadLimit;
        if (maxThreadLimit == livingThread) {
            onFullBusy(maxThreadLimit, livingThread, blockingJobs);
        } else {
            onIDle(busyRate, maxThreadLimit, livingThread);
        }
    }
}
