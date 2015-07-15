package SystemMessage

/**
 * Created by marvin on 15-7-14.
 */

/*注意在所有实例中消息必须属于同一个包和类,否则实例不同,无法识别
 */
sealed trait PaxosMessage

case class RegisterAcceptor(acceptorName:String) extends PaxosMessage

case class AcceptorRegistered(acceptorName:String) extends PaxosMessage


