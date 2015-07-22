package Leader


import Util.Logging

/**
 * Created by marvin on 15-7-20.
 * 如果领导者收到的prepare返回值带有value，必须要将自己的value设置成返回ballot号
 * 最大的回复所带有的value值。
 * who now must set the value of its proposal to the value
 * associated with the highest proposal number reported by the Acceptors
 */
class Instance(backend:Leader,val instanceId:String) extends Logging {


private var ballotId:Int = 0

  private var instanceValue:String = " "

  private var prepareAckNum:Int = 0

  private var acceptAckNum:Int = 0

  def handlePrepareAck(ackBallotId:Int,ackVal:Option[String]): Unit ={
    ackVal match{
      case Some(ackValue) =>if(ackBallotId > ballotId){
          ballotId = ackBallotId
          instanceValue = ackValue
          resendPrepare
        }else if(ackBallotId == ballotId){
          instanceValue = ackValue
          acceptAckNum = acceptAckNum + 1
        }else{

        }

      case None =>if(ackBallotId > ballotId){
          ballotId = ackBallotId
          resendPrepare
        }else if(ackBallotId == ballotId){
          acceptAckNum = acceptAckNum + 1
        }else{

        }
    }
    if(ackBallotId > ballotId ){
      ballotId = ackBallotId
    }else if(ackBallotId == ballotId){
      ackVal match{
        case Some(ackValue) =>
        case None =>
      }
    }
  }


  private def resendPrepare: Unit ={
    upBallotId
    clearPrepareAckNum
    backend.reProposeInstance(instanceId,ballotId)
  }

  private def upBallotId = ballotId = ballotId + 1

  private def clearPrepareAckNum = prepareAckNum = 0



}
