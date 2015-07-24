package util

/**
 * Created by marvin on 15-7-14.
 */
sealed trait PaxosMessage

case class RegisterAcceptor(acceptorName:String) extends PaxosMessage

case class AcceptorRegistered(acceptorName:String) extends PaxosMessage

case class Prepare(instanceId:String,ballotId:Int) extends PaxosMessage

case class Prepare_ack(instanceId:String,ballotId:Int,value:Option[String]) extends PaxosMessage

case class Accept(instanceId:String,ballotId:Int,value:String) extends PaxosMessage

case class Accept_ack(instanceId:String,ballotId:Int) extends PaxosMessage

case class Submit(instanceId:String,value:String) extends PaxosMessage
