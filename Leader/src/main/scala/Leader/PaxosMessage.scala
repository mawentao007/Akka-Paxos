package SystemMessage

/**
 * Created by marvin on 15-7-14.
 */
sealed trait PaxosMessage

case class RegisterAcceptor(acceptorName:String) extends PaxosMessage

case class AcceptorRegistered(acceptorName:String) extends PaxosMessage
