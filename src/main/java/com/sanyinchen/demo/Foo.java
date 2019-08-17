package com.sanyinchen.demo;

/**
 * Created by sanyinchen on 19-8-17.
 *
 * @author sanyinchen
 * @version v0.1
 * @since 19-8-17
 */

public class Foo {
    public static void main(String[] args) {
        DemoAsyncPool demoAsyncPool = new DemoAsyncPool();
        for (int i = 0; i < 10; i++) {
            demoAsyncPool.addTask(new DemoAsyncPool.InputArgs("job:" + i));
        }
    }
}
