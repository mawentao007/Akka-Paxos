package Acceptor

import SystemMessage._
import akka.actor._

import scala.collection.mutable.HashMap
import Util.Logging

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

  def start(systemName:String,_leaderAddress:String): Unit ={
    leaderAddress = _leaderAddress
    implicit val system = ActorSystem(systemName)
    acceptorActor = system.actorOf(Props(new AcceptorActor), name = acceptorName)
  }


  class AcceptorActor extends Actor {

    // create the remote actor,akka.tcp is very important
    val leaderActor = context.actorSelection(leaderAddress)
    leader = leaderActor

    override def preStart(): Unit ={
      leaderActor ! RegisterAcceptor(acceptorName)
    }

    def receive = {
      case msg:String =>
        println(msg)

      case AcceptorRegistered(acceptorName) =>
        println("receiving Acceptor Registered message " + acceptorName)

      case Prepare(instanceId,ballotId) =>
        println("receive Prepare " + instanceId + " " + ballotId)
        if(instanceIdToInstance.contains(instanceId)){
          instanceIdToInstance(instanceId).handlePrepareReq(ballotId)
        } else{
          handleNewInstance(instanceId,ballotId).handlePrepareReq(ballotId)
        }
    }
  }


  def sendPrepareAck(instanceId:String,ballotId:Int,value:Option[String]): Unit ={
    println("send prepare ack value is " + value)
    leader ! Prepare_ack(instanceId,ballotId,value)
  }

  //收到的Instance以前从未遇到过
  private def handleNewInstance(instanceId:String,ballotId:Int): Instance ={
    println("handle new Instance")
    val newInstance = new Instance(this,instanceId)
    instanceIdToInstance.put(instanceId,newInstance)
    newInstance
  }

}


