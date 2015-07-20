package Leader

import SystemMessage._
import akka.actor._
import scala.collection.mutable._

object Leader{
  def main(args:Array[String]) {
    val systemName="Akka-Paxos"
    val name = "leader"
    val leader = new Leader(name)
    leader.start(systemName)
  }
}

class Leader(val name:String) {

  var leaderActor:ActorRef = _

  var instanceIdInt = 0

  val instanceIdToBallotId = new HashMap[String,Int]

  val acceptorIdToActorRef = new HashMap[String,ActorRef]

  def start(systemName:String): Unit ={
    val system = ActorSystem(systemName)
    leaderActor = system.actorOf(Props(new LeaderActor),name = name)
  }

  class LeaderActor extends Actor {

    def receive = {
      case RegisterAcceptor(acceptorName) =>
        acceptorIdToActorRef.put(acceptorName,sender)
        replyAcceptor

      case msg:String => println(msg)

      case Prepare_ack(instanceId,ballotId,value) =>
        value match{
          case None => 
        }

    }
  }

  def replyAcceptor: Unit ={
    acceptorIdToActorRef.foreach{case (senderName,actorRef) =>
        actorRef ! AcceptorRegistered(senderName)
        proposeNewInstance
        proposeNewInstance
        val iId:String = instanceIdInt.toString() + name
        proposeInstance(iId)
    }
  }

  def proposeNewInstance = {
    instanceIdInt = instanceIdInt + 1
    val instanceId:String = instanceIdInt.toString + name
    val ballotId = 0
    instanceIdToBallotId.update(instanceId,ballotId)

    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Prepare(instanceId,ballotId)
    }
  }

  def proposeInstance(instanceId:String): Unit ={
    val ballotId = instanceIdToBallotId(instanceId) + 1
    instanceIdToBallotId.update(instanceId,ballotId)

    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Prepare(instanceId,ballotId)
    }
  }

}




