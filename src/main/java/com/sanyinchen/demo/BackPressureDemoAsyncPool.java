package com.sanyinchen.demo;

import com.sanyinchen.async.BackPressureTaskDispatchPool;
import com.sanyinchen.async.basic.BasicTaskDispatchPool;
import com.sanyinchen.async.basic.ThreadDisPatchManager;
import com.sanyinchen.async.common.Pair;

import java.util.List;

/**
 * Created by sanyinchen on 19-8-17.
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-8-17
 */

public class BackPressureDemoAsyncPool extends BackPressureTaskDispatchPool<BackPressureDemoAsyncPool.InputArgs,
        BackPressureDemoAsyncPool.Response> {
    private volatile boolean isBusying = false;

    @Override
    protected int getMaxSingleThreadTask() {
        return 2;
    }

    @Override
    protected int getMaxThread() {
        return 3;
    }

    public boolean isBusying() {
        return isBusying;
    }

    @Override
    protected Response runTask(InputArgs arg) {
        super.runTask(arg);
        try {
            if (isBusying) {
                Thread.sleep(1000);
            } else {
                Thread.sleep(5000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new Response(Thread.currentThread().toString());

    }

    @Override
    protected ThreadDisPatchManager.ThreadTaskFinished<InputArgs, Response> getFinishedCallback() {
        return new ThreadDisPatchManager.ThreadTaskFinished<InputArgs, Response>() {
            @Override
            public void onFinished(List<Pair<InputArgs, Response>> finishedList,
                                   List<Pair<InputArgs, Response>> interruptedList) {
                System.out.println(finishedList.size() + "'s task has been finished and " + interruptedList.size() +
                        "'s " + "task has been interrupted");
            }

        };
    }

    @Override
    protected ThreadDisPatchManager.JobTaskFinished<InputArgs, Response> getItemTaskCallback() {
        return new ThreadDisPatchManager.JobTaskFinished<InputArgs, Response>() {
            @Override
            public void onInterrupted(InputArgs inputArgs) {

            }

            @Override
            public void onFinished(Pair<InputArgs, Response> res) {

            }
        };
    }

    @Override
    public void onFullBusy(int maxThreadLimit, int livingThread, int blockingJobs) {
        isBusying = true;
        System.out.println("maxThreadLimit:" + maxThreadLimit + " , livingThread:" + livingThread + " blockingJobs:" + blockingJobs);

        System.out.println("Stop ! Is full busing");
    }

    @Override
    public void onIDle(float busyRate, int maxThreadLimit, int livingThread) {
        isBusying = false;
        System.out.println("maxThreadLimit:" + maxThreadLimit + " , livingThread:" + livingThread + " busyRate:" + busyRate);

        System.out.println("Idle , Come on, faster !!");

    }


    public static class InputArgs {
        private String arg;

        public InputArgs(String arg) {
            this.arg = arg;
        }

        public String getArg() {
            return arg;
        }
    }

    public static class Response {
        private String res;

        public Response(String res) {
            this.res = res;
        }

        public String getRes() {
            return res;
        }

        @Override
        public String toString() {
            return res;
        }
    }
}
