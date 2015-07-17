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

    }
  }

  def replyAcceptor: Unit ={
    acceptorIdToActorRef.foreach{case (name,actorRef) =>
        actorRef ! AcceptorRegistered(name)
    }
  }

  def proposeInstance(): Unit ={
    val instanceId:String = instanceIdInt.toString + name
    acceptorIdToActorRef.foreach{case (name,actorRef) =>
      actorRef ! Prepare(instanceId,)
    }
  }

}




