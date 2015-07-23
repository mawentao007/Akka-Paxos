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


  private  var acceptorNum:Int = 0

  private var leaderActor:ActorRef = _

  private var instanceIdInt = 0

  private val acceptorIdToActorRef = new HashMap[String,ActorRef]

  private val instanceIdToInstance = new HashMap[String,Instance]

  //实例号到实例值映射
  private val finishedInstance = new HashMap[String,String]

  private val request:Stack[String] = new Stack[String]


  private def start(systemName:String): Unit ={
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
        acceptorNum = acceptorNum + 1
        //TODO 新加入acceptor之后,所有未被选中(chosen)的提案中的quorum要被重新设置
        replyAcceptor
        proposeNewInstance
        proposeNewInstance

      case Prepare_ack(instanceId,ballotId,value) =>
        logInfo("receive prepare_ack [" + instanceId + "," + ballotId + "]")
        if(!finishedInstance.contains(instanceId)) {
          instanceIdToInstance.get(instanceId) match{
            case Some(instance) => instance.handlePrepareAck(ballotId,value)
            case None =>  logWarn(instanceId + "not in list")
          }
        }

      case Accept_ack(instanceId,ballotId)=>
        if(!finishedInstance.contains(instanceId)) {
          instanceIdToInstance.get(instanceId) match {
            case Some(instance) => instance.handleAcceptAck(ballotId)
            case None => logWarn(instanceId + "not in list")
          }
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
    instance.setQuorum(acceptorNum)

    instanceIdToInstance.put(instance.instanceId,instance)
    sendPrepare_backend(instance.instanceId,0)

  }

  def sendPrepare_backend(instanceId:String,ballotId:Int) = {
    //向所有Acceptor重新发送带有新ballotId的prepare请求
    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Prepare(instanceId,ballotId)
    }
  }

  def sendAccept_backend(instanceId:String,ballotId:Int,value:String) = {
    //向所有Acceptor重新发送带有新ballotId的prepare请求
    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Accept(instanceId,ballotId,value)
    }
  }

  def sendSubmit_backend(instanceId:String,value:String) = {

    finishedInstance.put(instanceId,value)
    logInfo("proposal finished [ " + instanceId + " , " + value + " ]" )
    //TODO 除了acceptor，应该还有learner，以及其它proposer
    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Submit(instanceId,value)
    }
    instanceIdToInstance.remove(instanceId)

  }


  private def generateNewInstanceId:String = {
    instanceIdInt = instanceIdInt + 1
    instanceIdInt.toString + name
  }

}




