#基于Akka库的单Leader的Paxos算法实现
运行环境
-------
* scala 2.10.4
* jdk7
* sbt 0.13.5

使用库（详见build.sbt）
------
* Akka
* logback

相关配置
------
需要配置src/main/resources/application.conf文件中的ip地址和端口，端口冲突的话无法执行。

运行方法
-----
概工程包含若干个子工程，目前有leader和acceptor两个角色，分布式部署需要将每个角色对应的工程文件分别拷贝到不同节点运行，执行代码:
    
    usage: sbt "run [argument]"
           sbt "run leader"   :   开启一个新的leader
           sbt "run acceptor" :   开启一个新的acceptor

    
其它
----
支持多个Acceptor，只需要将工程文件配置多个副本即可。关于配置的问题稍后将会进行改进，实现单个配置文件配置多个节点的功能，敬请关注。
    


    
    
