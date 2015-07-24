package acceptor

import Util.Logging


/**
 * Created by marvin on 15-7-20.
 */
class Instance(backend:Acceptor,val instanceId:String) extends Logging{
  private var ballotId:Int = -1

  private var instanceValue:String = null

  def handlePrepareReq(reqBallotId:Int) = {
    logInfo("handle prepare req ballotId " + reqBallotId)
    if(reqBallotId > ballotId){
      ballotId = reqBallotId
    }

    if(instanceValue != null){
      sendPrepareAck(Some(instanceValue))
    }else{
      sendPrepareAck(None)
    }
  }

  def handleAcceptReq(reqBallotId:Int,value:String)={
    logInfo("handle accept req ballotId " + reqBallotId)
    //当前的ballotId等于消息的，可以接受value
    //当前的ballotId大于消息的，不接受，并通知leader
    //当前的ballotId小于消息的，更新自身的ballot，不接受。
    if(reqBallotId == ballotId){
      instanceValue = value
      sendAcceptAck
    }else if(reqBallotId > ballotId){
      ballotId = reqBallotId
    }else{
      sendAcceptAck
    }
  }

  def handleSubmitReq(value:String)={
    instanceValue = value
    logInfo("instance submitted " + "[" + instanceId +"," + value +"]")
  }


  private def sendPrepareAck(value:Option[String]): Unit ={
    logInfo("sendPrepareAck [" + instanceId + "," + ballotId + "]" )
    backend.sendPrepareAck_backend(instanceId,ballotId,value)
  }

  private def sendAcceptAck = {
    logInfo("sendAcceptAck [" + instanceId + "," + ballotId + "]" )
    backend.sendAcceptAck_backend(instanceId,ballotId)

  }



}
