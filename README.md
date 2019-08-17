## Describtion
This is a simple therad pool manager . But it's very useful , you can add a new task job in dynamic to this manager and it will dispatch and schedue your task . It will create several threads (based on your cpu numer ) and every thread has a 10 tasks queue by default . 

## Work Flow 
![](https://github.com/sanyinchen/AsyncTaskPool/blob/master/doc/threadpool.png)

## How To Use  
You need to extend the ``` BasicTaskDispatchPool ```and overwrite ```runTask``` ,  ```getFinishedCallback``` and ```getItemTaskCallback```.  
+ runTask : detail job will do in this method 
+ getFinishedCallback : callback of all task finished
+ getItemTaskCallback : callback of item task job finished

You can find detail in ```DemoAsyncPool``` 

```
public class DemoAsyncPool extends BasicTaskDispatchPool<DemoAsyncPool.InputArgs, DemoAsyncPool.Response> {


    @Override
    protected Response runTask(InputArgs arg) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // interrupt test
        if (Double.compare(Math.random(), 0.5f) < 0) {
            Thread.currentThread().interrupt();
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

```

Test in ```Foo.java```
```
public class Foo {
    public static void main(String[] args) {
        DemoAsyncPool demoAsyncPool = new DemoAsyncPool();
        for (int i = 0; i < 10; i++) {
            demoAsyncPool.addTask(new DemoAsyncPool.InputArgs("job:" + i));
        }
    }
}
```

![](https://github.com/sanyinchen/AsyncTaskPool/blob/master/doc/console.png)

