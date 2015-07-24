/**
 * Created by marvin on 15-7-24.
 */

import Util.Logging
import acceptor.Acceptor
import leader.Leader


object Main extends Logging{
  def main(args:Array[String]): Unit ={
    if(args.length == 0 || args.length >=2){
      println("usage: sbt \"run [argument]\"")
      println("   sbt \"run leader\"   :   开启一个新的leader")
      println("   sbt \"run acceptor\" :   开启一个新的acceptor")
    }else{
      args(0) match{
        case "leader" => Leader.startLeader("leaderOne")
        case "acceptor" => Acceptor.startAcceptor("acceptorOne")
        case "all" =>
          Leader.startLeader("leaderOne")
          Acceptor.startAcceptor("acceptorOne")
        case _ => logInfo("wrong arguments")
      }
    }
  }

}
