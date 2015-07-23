#基于Akka库的单Leader的Paxos算法实现
运行环境
-------
* scala 2.10.4
* jdk7
* sbt 0.13.5

使用库（详见build.sbt）
------
*Akka
*logback

运行方法
-----
概工程包含若干个子工程，目前有leader和acceptor两个角色，分布式部署需要将每个角色对应的工程文件分别拷贝到不同节点运行，执行代码：

    cd projectDir
    sbt run
    

    
    
