package Acceptor

import SystemMessage.{AcceptorRegistered, RegisterAcceptor}
import akka.actor._

object Acceptor {
  def main(args:Array[String]) {
    val leaderAddress = "akka.tcp://Akka-Paxos@127.0.0.1:5150/user/leader"
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



    override def preStart(): Unit ={
      leader ! RegisterAcceptor(acceptorName)
    }

    def receive = {
      case msg:String =>
        println(msg)

      case AcceptorRegistered(acceptorName) =>
        println("receiving Acceptor Registered message " + acceptorName)


    }
  }

}


