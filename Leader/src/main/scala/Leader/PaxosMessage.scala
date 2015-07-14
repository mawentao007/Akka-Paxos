package Leader

/**
 * Created by marvin on 15-7-14.
 */
sealed trait PaxosMessage

case class RegisterAcceptor(acceptorId:String) extends PaxosMessage
