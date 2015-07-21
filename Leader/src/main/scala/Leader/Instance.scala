package Leader

/**
 * Created by marvin on 15-7-20.
 */
class Instance(backend:Leader,val instanceId:String) {
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
