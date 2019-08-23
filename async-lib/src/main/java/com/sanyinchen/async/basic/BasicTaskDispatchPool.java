package com.sanyinchen.async.basic;


import com.sun.istack.internal.NotNull;

/**
 * Created by sanyinchen on 19-3-23.
 * <p>
 * <p>
 * Basic Task dispatcher
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-3-23
 */
public abstract class BasicTaskDispatchPool<T, O> {


    private static final int JOB_MAX_TASKS = 10;
    private ThreadDisPatchManager<T, O> mThreadPool;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * when the max thread number is not reached , it will create a new thread
     * when the max thread number has been reached , it will be added to thread's task queue
     *
     * @see #getMaxThread
     * @see #getMaxSingleThreadTask
     */
    protected BasicTaskDispatchPool() {
        System.out.println("CPU_COUNT:" + CPU_COUNT);
        mThreadPool = new ThreadDisPatchManager<T, O>(getMaxThread(), getMaxSingleThreadTask()
                , arg -> BasicTaskDispatchPool.this.runTask(arg), getFinishedCallback(), getItemTaskCallback());

    }

    protected int getMaxThread() {
        return CPU_COUNT;
    }

    protected int getMaxSingleThreadTask() {
        return JOB_MAX_TASKS;
    }

    /**
     * export addTask API
     *
     * @param task
     * @return
     */
    public BasicTaskDispatchPool addTask(T task) {
        if (task != null) {
            mThreadPool.createWhenIdle(task);
        }
        return this;
    }

    /**
     * interrupt job
     */
    public void interrupt() {
        mThreadPool.interrupt();
    }

    protected abstract O runTask(T arg);

    /**
     * callback of when all tasks have been finished
     *
     * @return
     */
    @NotNull
    protected abstract ThreadDisPatchManager.ThreadTaskFinished<T, O> getFinishedCallback();

    /**
     * call back of item task has been finished
     *
     * @return
     */
    @NotNull
    protected abstract ThreadDisPatchManager.JobTaskFinished<T, O> getItemTaskCallback();

}
