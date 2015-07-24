package acceptor


import akka.actor._
import com.typesafe.config.ConfigFactory

import scala.collection.mutable.HashMap
import Util.Logging
import util._

object Acceptor {
  def main(args:Array[String]) {
    val leaderAddress = "akka.tcp://Akka-Paxos@127.0.0.1:5150/user/leader"
    val systemName="Akka-Paxos"
    val acceptorName = "acceptorOne"
    val acceptor = new Acceptor(acceptorName)
    acceptor.start(systemName,leaderAddress)

  }
}


class Acceptor(val acceptorName:String) extends Logging{

  var acceptorActor:ActorRef = _
  var leaderAddress:String = _
  var leader:ActorSelection = _

  val instanceIdToInstance = new HashMap[String,Instance]

  val finishedInstanceIdToValue = new HashMap[String,String]

  def start(systemName:String,_leaderAddress:String): Unit ={
    leaderAddress = _leaderAddress
    //通过载入不同的conf文件来更改系统配置
    val system =
      ActorSystem(systemName, ConfigFactory.load("test"))
    acceptorActor = system.actorOf(Props(new AcceptorActor), name = acceptorName)
  }


  class AcceptorActor extends Actor {

    // create the remote actor,akka.tcp is very important
    val leaderActor = context.actorSelection(leaderAddress)
    leader = leaderActor

    override def preStart(): Unit ={
      leaderActor ! util.RegisterAcceptor(acceptorName)
    }

    def receive = {
      case msg:String =>
        println(msg)

      case AcceptorRegistered(acceptorName) =>
        logInfo("receiving Acceptor Registered message " + acceptorName)

      case Prepare(instanceId,ballotId) =>
        logInfo("receive Prepare [" + instanceId + "," + ballotId + "]")
        //如果实例未提交，则开始处理流程，创建新实例或处理当前实例
        if(!finishedInstanceIdToValue.contains(instanceId)) {
          if (instanceIdToInstance.contains(instanceId)) {
            instanceIdToInstance(instanceId).handlePrepareReq(ballotId)
          } else {
            handleNewInstance(instanceId,ballotId).handlePrepareReq(ballotId)
          }
        }

      case Accept(instanceId,ballotId,value) =>
        logInfo("receive Accept [" + instanceId + "," + ballotId + "," + value +  "]")
        //TODO 异常验证，例如实例不存在在当前列表中
        if(!finishedInstanceIdToValue.contains(instanceId)) {
            instanceIdToInstance.get(instanceId) match{
              case Some(instance) => instance.handleAcceptReq(ballotId,value)
              case None => logWarn(instanceId + " not in list")
            }
        }

      case Submit(instanceId,value) =>
        finishedInstanceIdToValue.put(instanceId,value)
        instanceIdToInstance.remove(instanceId)
        logInfo("instance submitted [" + instanceId + "," + value + "]")

    }
  }

  //收到的Instance以前从未遇到过
  private def handleNewInstance(instanceId:String,ballotId:Int): Instance ={
    logInfo("handle new Instance [" + instanceId + "," + ballotId + "]")
    val newInstance = new Instance(this,instanceId)
    instanceIdToInstance.put(instanceId,newInstance)
    newInstance
  }


  def sendPrepareAck_backend(instanceId:String,ballotId:Int,value:Option[String]): Unit ={
    leader ! util.Prepare_ack(instanceId,ballotId,value)
  }

  def sendAcceptAck_backend(instanceId:String,ballotId:Int): Unit ={
    leader ! util.Accept_ack(instanceId,ballotId)
  }



}


