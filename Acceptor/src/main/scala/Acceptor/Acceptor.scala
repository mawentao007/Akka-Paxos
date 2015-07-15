package Acceptor

import akka.actor._

object Acceptor {
  def main(args:Array[String]) {
    val leaderAddress = "akka.tcp://Akka-Paxos@127.0.0.1:5150/user/Leader"
    val systemName="Akka-Paxos"
    val acceptorName = "acceptorOne"
    val acceptor = new Acceptor
    acceptor.start(systemName,acceptorName,leaderAddress)

  }
}


class Acceptor {

  var acceptorActor:ActorRef = _
  var leaderAddress:String = _
  var acceptorName:String = _

  def start(systemName:String,_acceptorName:String,_leaderAddress:String): Unit ={
    leaderAddress = _leaderAddress
    acceptorName = _acceptorName
    implicit val system = ActorSystem(systemName)
    acceptorActor = system.actorOf(Props(new AcceptorActor), name = acceptorName)
  }




  class AcceptorActor extends Actor {

    // create the remote actor,akka.tcp is very important
    val leader = context.actorSelection(leaderAddress)
    var counter = 0

    leader ! "love"

    def receive = {
      case AcceptorRegistered(acceptorName) =>
        println("leader reply me " + acceptorName)

    }
  }

}


