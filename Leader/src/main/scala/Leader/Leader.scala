package Leader

import SystemMessage._
import akka.actor._
import scala.collection.mutable._
import Util.Logging

object Leader{
  def main(args:Array[String]) {
    val systemName="Akka-Paxos"
    val name = "leader"
    val leader = new Leader(name)
    leader.start(systemName)
  }
}

class Leader(val name:String) extends Logging{

  var leaderActor:ActorRef = _

  var instanceIdInt = 0

  val acceptorIdToActorRef = new HashMap[String,ActorRef]

  val instanceIdToInstance = new HashMap[String,Instance]

  val request:Stack[String] = new Stack[String]()


  def start(systemName:String): Unit ={
    val system = ActorSystem(systemName)
    leaderActor = system.actorOf(Props(new LeaderActor),name = name)
    logInfo("start")

  }

  //leader的消息接口，负责首发消息
  class LeaderActor extends Actor {

    def receive = {
      case RegisterAcceptor(acceptorName) =>
        logInfo("RegisterAcceptor " + acceptorName)
        acceptorIdToActorRef.put(acceptorName,sender)
        replyAcceptor
        proposeNewInstance
        proposeNewInstance


      case msg:String => println(msg)

      case Prepare_ack(instanceId,ballotId,value) =>
        println("receive prepare_ack " + instanceId + " " + ballotId)
        println(value)
        value match{
          case None => println("No value of " + "[ " + instanceId + " , " + ballotId + " ]")
          case Some(s) => "string is " + s
        }

    }
  }

  def replyAcceptor: Unit ={
    acceptorIdToActorRef.foreach{case (senderName,actorRef) =>
        actorRef ! AcceptorRegistered(senderName)

    }
  }

  def proposeNewInstance = {
    //创建新的实例并添加到map中记录
    val instance = new Instance(this,generateNewInstanceId)
    instanceIdToInstance.put(instance.instanceId,instance)

    //将该实例发送给所有的Acceptor进行准备工作
    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Prepare(instance.instanceId,0)
    }
  }

  def reProposeInstance(instanceId:String,ballotId:Int): Unit ={


    //向所有Acceptor重新发送带有新ballotId的prepare请求
    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Prepare(instanceId,ballotId)
    }
  }

  private def generateNewInstanceId:String = {
    instanceIdInt = instanceIdInt + 1
    instanceIdInt.toString + name
  }

}




