package Acceptor

import Util.Logging

/**
 * Created by marvin on 15-7-20.
 */
class Instance(backend:Acceptor,val instanceId:String) extends Logging{
  private var ballotId:Int = 0

  private var instanceValue:String = null



  def handlePrepareReq(reqBallotId:Int): Unit ={
    logInfo("handle prepare req")
    if(reqBallotId > ballotId){
      ballotId = reqBallotId
    }

    if(instanceValue != null){
      prepareAck(Some(instanceValue))
    }else{
      prepareAck(None)
    }
  }


  private def prepareAck(value:Option[String]): Unit ={
    println("prepareAck")
    backend.sendPrepareAck(instanceId,ballotId,value)
  }



}
