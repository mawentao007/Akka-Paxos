package leader


import Util.Logging
import util.InstanceState._

/**
 * Created by marvin on 15-7-20.
 * 如果领导者收到的prepare返回值带有value，必须要将自己的value设置成返回ballot号
 * 最大的回复所带有的value值。
 * who now must set the value of its proposal to the value
 * associated with the highest proposal number reported by the Acceptors
 */


class LeaderInstance(backend:Leader,val instanceId:String) extends Logging {

  private var quorums:Int = 0

  private var ballotId:Int = 0

  private var instanceValue:String = null

  private var prepareAckNum:Int = 0

  private var acceptAckNum:Int = 0

  private var instanceState:InstanceState = PREPARE


  def  sendPrepare = {
    instanceState = PREPARE
    clearPrepareAckNum
    clearAcceptAckNum
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
            ballotId = ackBallotId + 1
            instanceValue = ackValue
            sendPrepare
          } else if (ackBallotId == ballotId) {
           if (instanceValue == null) {
             instanceValue = ackValue
           }
            prepareAckNum = prepareAckNum + 1
            checkPrepareNum
          }

        case None => if (ackBallotId > ballotId) {
          ballotId = ackBallotId + 1
          sendPrepare
        } else if (ackBallotId == ballotId) {
          prepareAckNum = prepareAckNum + 1
          checkPrepareNum
        }
      }
    }
  }



  private def sendAccept = {
    //只有在ACCEPT状态下才会进行accept相关操作
    if(instanceState == ACCEPT){
      backend.sendAccept_backend(instanceId,ballotId,instanceValue)
    }
  }

  def handleAcceptAck(ackBallotId:Int) = {
    //只有在ACCEPT状态下才会进行accept相关操作
    if(instanceState == ACCEPT){
      if(ackBallotId > ballotId){
        ballotId = ackBallotId + 1
        instanceState = PREPARE
        clearAcceptAckNum
        sendPrepare
      }else if(ackBallotId ==  ballotId){
        acceptAckNum = acceptAckNum + 1
        checkAcceptNum
      }
    }
  }

  private def sendSubmit = {
    backend.sendSubmit_backend(instanceId,instanceValue)
  }



  private def checkPrepareNum = {
    if(prepareAckNum >= quorums){
      instanceState = ACCEPT
      sendAccept
      clearPrepareAckNum
    }
  }

  private def checkAcceptNum = {
    if(acceptAckNum >= quorums){
      instanceState = CHOSEN
      sendSubmit
    }

  }


  private def clearPrepareAckNum = prepareAckNum = 0

  private def clearAcceptAckNum = acceptAckNum = 0


  //Acceptor数量改变的时候，对于未提交的实例要重新设置多数派数量
  def setQuorum(num:Int) = {
    quorums = num / 2 + 1
  }

}
