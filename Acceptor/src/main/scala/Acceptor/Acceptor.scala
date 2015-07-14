package Acceptor

import akka.actor._

object Acceptor {
  def main(args:Array[String]) {

    implicit val system = ActorSystem("Akka-Paxos")
    val localActor = system.actorOf(Props[Acceptor], name = "Acceptor") // the local actor
    localActor ! "START" // start the action

  }
}


class Acceptor {

  def start(acceptorId:String): Unit ={

  }

  class AcceptorActor extends Actor {

    // create the remote actor,akka.tcp is very important
    val remote = context.actorSelection("akka.tcp://Akka-Paxos@127.0.0.1:5150/user/Leader")
    var counter = 0

    def receive = {
      case "START" =>
        remote ! "Hello from the Acceptor"
      case msg: String =>
        println(s"Acceptor received message: '$msg'")
        if (counter < 5) {
          sender ! "Hello back to you"
          counter += 1
        }
    }
  }

}


