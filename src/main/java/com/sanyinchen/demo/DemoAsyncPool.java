package com.sanyinchen.demo;

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

public class DemoAsyncPool extends BasicTaskDispatchPool<DemoAsyncPool.InputArgs, DemoAsyncPool.Response> {


    @Override
    protected Response runTask(InputArgs arg) {

        // interrupt test
        if (Double.compare(Math.random(), 0.5f) < 0) {
           // Thread.currentThread().interrupt();
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(100);
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
                System.out.println(inputArgs.arg + " has been interrupted");
            }

            @Override
            public void onFinished(Pair<InputArgs, Response> res) {
                StringBuilder itemTaskFinishMsg = new StringBuilder();
                if (res == null || res.first == null) {
                    System.out.println("error");
                }
                itemTaskFinishMsg
                        .append("input :" + res.first.getArg())
                        .append(" res: " + res.second);

                System.out.println("itemTaskFinishMsg==>" + itemTaskFinishMsg.toString());

            }
        };
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
