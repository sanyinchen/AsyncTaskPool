package com.sanyinchen.async;

import com.sanyinchen.async.basic.BasicTaskDispatchPool;
import com.sanyinchen.async.basic.ThreadDisPatchManager;

/**
 * Created by sanyinchen on 19-8-31.
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-8-31
 */

public class DefaultTaskDispatchPool<T, O> extends BasicTaskDispatchPool<T, O> {
    @Override
    protected O runTask(T arg) {
        return null;
    }

    @Override
    protected ThreadDisPatchManager.ThreadTaskFinished<T, O> getFinishedCallback() {
        return null;
    }

    @Override
    protected ThreadDisPatchManager.JobTaskFinished<T, O> getItemTaskCallback() {
        return null;
    }
}
