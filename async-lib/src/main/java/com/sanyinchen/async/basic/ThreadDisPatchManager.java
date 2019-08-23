package com.sanyinchen.async.basic;


import com.sanyinchen.async.common.Pair;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sanyinchen on 19-3-23.
 * <p>
 * Thread dispatch manager
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-3-23
 */
public class ThreadDisPatchManager<T, O> {
    private volatile int maxThread;
    private volatile int maxThreadJob;
    private volatile List<JobThread> mThreadList = new CopyOnWriteArrayList<>();
    private volatile Queue<T> blockingJobs = new ConcurrentLinkedQueue<>();
    private JobDetail<T, O> mJobDetail;
    private ThreadTaskFinished<T, O> callback;
    private volatile List<Pair<T, O>> mFinishedPairs = new CopyOnWriteArrayList<>();
    private volatile List<Pair<T, O>> mInterruptPairs = new CopyOnWriteArrayList<>();
    private JobTaskFinished<T, O> mJobTaskFinished = new JobTaskFinished<T, O>() {
        @Override
        public void onInterrupted(T inputArgs) {
            // ignore
        }

        @Override
        public void onFinished(Pair<T, O> res) {
            // ignore
        }
    };


    private static class InnerSingleQueueFinishIndicator extends Pair {

        public InnerSingleQueueFinishIndicator(Object first, Object second) {
            super(first, second);
        }
    }


    /**
     * get current active thread
     *
     * @return
     */
    public synchronized int livingThreadSize() {
        int sum = 0;
        for (Thread thread : mThreadList) {
            if (thread.isAlive() && !thread.isInterrupted()) {
                sum++;
            }
        }
        return sum;
    }


    public ThreadDisPatchManager(int maxThread, int maxThreadJob, JobDetail<T, O> jobDetail,
                                 @NotNull ThreadTaskFinished<T, O> callback,
                                 @Nullable JobTaskFinished<T, O> jobTaskFinished) {
        this.maxThread = maxThread;
        this.maxThreadJob = maxThreadJob;
        this.mJobDetail = jobDetail;
        this.callback = callback;
        this.mJobTaskFinished = jobTaskFinished;
    }

    /**
     * create a new task job
     *
     * @param job
     */
    public void createWhenIdle(T job) {
        create(job);
    }

    private void create() {
        if (!blockingJobs.isEmpty()) {
            create(blockingJobs.poll());
        } else {
            if (livingThreadSize() == 0) {
                callback.onFinished(mFinishedPairs, mInterruptPairs);
            }
        }
    }

    /**
     * create a new task
     *
     * @param job
     * @return
     */
    private synchronized boolean create(@NotNull T job) {

        if (mThreadList.size() < maxThread) {
            JobTask tJobTask = new JobTask(new JobTaskFinished<T, O>() {
                @Override
                public void onInterrupted(T inputArgs) {

                    mJobTaskFinished.onInterrupted(inputArgs);

                }

                @Override
                public void onFinished(Pair<T, O> res) {
                    if (res instanceof InnerSingleQueueFinishIndicator) {
                        Thread thread = Thread.currentThread();
                        thread.interrupt();
                        mThreadList.remove(thread);
                    } else {
                        mJobTaskFinished.onFinished(res);
                    }
                    create();
                }
            });
            tJobTask.addJob(job);
            JobThread jobThread = new JobThread(tJobTask);
            mThreadList.add(jobThread);
            jobThread.start();
            return true;
        } else {
            for (JobThread thread : mThreadList) {
                if (thread.isIdle()) {
                    thread.addNewJob(job);
                    return true;
                }
            }
            if (blockingJobs.contains(job)) {
                System.out.println("repeat task!! :" + job + " in " + Thread.currentThread().getId());
            } else {
                blockingJobs.add(job);
            }
            return false;
        }
    }


    private class JobThread extends Thread {
        private JobTask mTJobTask;

        public JobThread(@NotNull JobTask TJobTask) {
            super(TJobTask);
            mTJobTask = TJobTask;
        }

        boolean isIdle() {
            return taskSize() < maxThreadJob;
        }

        void addNewJob(T job) {
            mTJobTask.addJob(job);
        }

        int taskSize() {
            return mTJobTask.jobSize();
        }

    }

    /**
     * job task wrap class
     */
    private class JobTask implements Runnable {

        private volatile Queue<T> taskQueue = new LinkedList<>();
        private JobTaskFinished<T, O> innerJobTaskFinished;


        public JobTask(@NotNull JobTaskFinished<T, O> jobTaskFinished) {
            innerJobTaskFinished = jobTaskFinished;
        }

        public void addJob(@NotNull T job) {
            taskQueue.add(job);
        }

        public int jobSize() {
            return taskQueue.size();
        }

        @Override
        public void run() {
            while (true) {
                T arg = null;
                if (!taskQueue.isEmpty()) {
                    arg = taskQueue.poll();
                    Pair<T, O> toPair = new Pair<>(arg, runTask(arg));
                    if (!Thread.interrupted()) {
                        mFinishedPairs.add(toPair);
                        innerJobTaskFinished.onFinished(toPair);
                    } else {
                        mInterruptPairs.add(toPair);
                        innerJobTaskFinished.onInterrupted(arg);
                    }
                } else {
                    if (!taskQueue.isEmpty() && Thread.interrupted()) {
                        throw new RuntimeException(" taskQueue is not empty:" + taskQueue.size() + " in:" + Thread.currentThread());
                    }
                    removeAll();
                    innerJobTaskFinished.onFinished(new InnerSingleQueueFinishIndicator(null, null));
                    break;
                }

            }

        }

        private void removeAll() {
            while (!taskQueue.isEmpty()) {
                taskQueue.poll();
            }
        }


    }

    private O runTask(T arg) {
        return mJobDetail.runTask(arg);
    }

    /**
     * 描述任务调度具体做什么TASK
     *
     * @param <T>
     * @param <O>
     */
    public interface JobDetail<T, O> {

        O runTask(T arg);
    }


    /**
     * thread interrupt
     */
    public synchronized void interrupt() {
        for (Thread thread : mThreadList) {
            thread.interrupt();
            mThreadList.remove(thread);
        }
    }

    /**
     * single job task finish callback
     *
     * @param <T>
     * @param <O>
     */
    public interface JobTaskFinished<T, O> {
        void onInterrupted(@NotNull T inputArgs);

        void onFinished(@NotNull Pair<T, O> res);
    }

    /**
     * whole task finished
     *
     * @param <T>
     * @param <O>
     */
    public interface ThreadTaskFinished<T, O> {
        void onFinished(@NotNull List<Pair<T, O>> finishedList, @NotNull List<Pair<T, O>> interruptedList);
    }

}
