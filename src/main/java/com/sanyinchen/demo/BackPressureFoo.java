package com.sanyinchen.demo;

/**
 * Created by sanyinchen on 19-8-17.
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-8-17
 */

public class BackPressureFoo {
    public static void main(String[] args) throws InterruptedException {
        BackPressureDemoAsyncPool demoAsyncPool = new BackPressureDemoAsyncPool();
        int i = 0;
        while (true) {
            if (i >= 30) {
                break;
            }
            if (demoAsyncPool.isBusying()) {
                Thread.sleep(10000);
            }
            demoAsyncPool.addTask(new BackPressureDemoAsyncPool.InputArgs("job:" + i));
            i++;
        }
    }
}
