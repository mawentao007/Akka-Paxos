package Acceptor

import akka.actor._

object Acceptor {
  def main(args:Array[String]) {
    val acceptor = new Acceptor
    acceptor.start("acceptorOne")
    

  }
}


class Acceptor {


  var acceptorActor:ActorRef = _
  def start(acceptorId:String): Unit ={
    implicit val system = ActorSystem("Akka-Paxos")
    acceptorActor = system.actorOf(Props[Acceptor], name = acceptorId)
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


