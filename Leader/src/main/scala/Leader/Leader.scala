package Leader

import SystemMessage.RegisterAcceptor
import akka.actor._
import scala.collection.mutable._

object Leader{
  def main(args:Array[String]) {
    val systemName="Akka-Paxos"
    val leaderName = "leader"
    val leader = new Leader
    leader.start(systemName,leaderName)
  }
}

class Leader {

  var leaderActor:ActorRef = _

  val acceptorIdToActorRef = new HashMap[String,ActorRef]

  def start(systemName:String,leaderName:String): Unit ={
    val system = ActorSystem(systemName)
    leaderActor = system.actorOf(Props(new LeaderActor),name = leaderName)
    leaderActor ! "self"
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
        actorRef ! name
    }
  }

}




