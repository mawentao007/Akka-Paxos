package Leader

/**
 * Created by marvin on 15-7-23.
 */
object InstanceState extends Enumeration{
  val PREPARE,ACCEPT,CHOSEN = Value

  //类似c的typedef,将这个枚举类型重命名?
  type InstanceState = Value
}


