
## Work Flow 
![](work.jpg)
## How To Use  
You need to extend the ``` BasicTaskDispatchPool ```and overwrite ```runTask``` ,  ```getFinishedCallback``` and ```getItemTaskCallback```.  
+ runTask : detail job will do in this method 
+ getFinishedCallback : callback of all task finished
+ getItemTaskCallback : callback of item task job finished

You can find detail in ```DemoAsyncPool``` , it will show you have to do .