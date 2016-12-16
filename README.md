# MyStepCounter
一个计步器的演示

![alt tag](https://github.com/IssacChueng/MyStepCounter/blob/master/design/2016-12-16-12-53-51.png?raw=true)
![alt tag](https://github.com/IssacChueng/MyStepCounter/blob/master/design/2016-12-16-12-53-59.png?raw=true)
![alt tag](https://github.com/IssacChueng/MyStepCounter/blob/master/design/2016-12-16-12-54-07.png?raw=true)
![alt tag](https://github.com/IssacChueng/MyStepCounter/blob/master/design/2016-12-16-12-54-14.png?raw=true)

后端用bmob，orm用ormlite，分两个进程，一个主进程用于完成主要显示功能和其他的逻辑，子进程为一个service,负责接收加速度传感器的数值，实时计算出步数，分为4张表，每小时存储一次，每天存储一次，每周存储一次，每月存储一次。
