package Leader

import akka.actor._
import scala.collection.mutable._

object Leader{
  def main(args:Array[String]) {
    val leader = new Leader
    leader.start("leader")
  }
}

class Leader {

  var leaderActor:ActorRef = _

  val acceptorIdToActorRef = new HashMap[String,ActorRef]

  def start(leaderName:String): Unit ={
    val system = ActorSystem("Akka-Paxos")
    leaderActor = system.actorOf(Props[LeaderActor],name = leaderName)
  }

  class LeaderActor extends Actor {
    def receive = {
      case RegisterAcceptor(acceptorId) =>
        acceptorIdToActorRef.put(acceptorId,sender)

      case msg: String =>
        println(s"Leader received message '$msg'")
        sender ! "Hello from the Acceptor"
    }
  }

}




