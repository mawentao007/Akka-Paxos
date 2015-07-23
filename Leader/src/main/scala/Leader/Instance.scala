package Leader


import Util.Logging
import InstanceState._

/**
 * Created by marvin on 15-7-20.
 * 如果领导者收到的prepare返回值带有value，必须要将自己的value设置成返回ballot号
 * 最大的回复所带有的value值。
 * who now must set the value of its proposal to the value
 * associated with the highest proposal number reported by the Acceptors
 */


class Instance(backend:Leader,val instanceId:String) extends Logging {

  private var quorums:Int = 0

  private var ballotId:Int = 0

  private var instanceValue:String = null

  private var prepareAckNum:Int = 0

  private var acceptAckNum:Int = 0

  private var instanceState:InstanceState = PREPARE


  def  sendPrepare = {
    instanceState = PREPARE
    backend.sendPrepare_backend(instanceId,ballotId)
  }

  def handlePrepareAck(ackBallotId:Int,ackVal:Option[String]) = {
    //状态是CHOSEN,则协商结束,忽略消息
    if(instanceState != CHOSEN) {
      ackVal match {
        //如果领导者收到的prepare返回值带有value，必须要将自己的value设置成返回ballot号
        //最大的回复所带有的value值。
        //只处理比当前ballot大或者相等的回复,小的ballot被自动忽略
        case Some(ackValue) => if (ackBallotId > ballotId) {
          ballotId = ackBallotId
          instanceValue = ackValue
          reSendPrepare
        } else if (ackBallotId == ballotId) {
          if (instanceValue == null) {
            instanceValue = ackValue
          }
          acceptAckNum = acceptAckNum + 1
        }

        case None => if (ackBallotId > ballotId) {
          ballotId = ackBallotId
          reSendPrepare
        } else if (ackBallotId == ballotId) {
          acceptAckNum = acceptAckNum + 1
        }
      }
    }
  }


  private def reSendPrepare: Unit ={
    instanceState = PREPARE
    upBallotId
    clearPrepareAckNum
    backend.sendPrepare_backend(instanceId,ballotId)
  }

  private def sendAccept = {
    //只有在ACCEPT状态下才会进行accept相关操作
    if(instanceState == ACCEPT){
      backend.sendAccept_backend(instanceId,ballotId,instanceValue)
    }
  }

  private def handleAcceptAck(ackBallotId:Int,ackVal:Option[String]) = {
    //只有在ACCEPT状态下才会进行accept相关操作
    if(instanceState == ACCEPT)

  }



  private def checkAcceptNum = {
    if(acceptAckNum >= quorums){
      instanceState = ACCEPT
      sendAccept
    }
  }

  private def upBallotId = ballotId = ballotId + 1

  private def clearPrepareAckNum = prepareAckNum = 0

  def setQuorum(num:Int) = {
    quorums = num / 2 + 1
  }







}
