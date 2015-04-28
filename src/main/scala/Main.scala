/**
 * Created by rilakkuma on 27/04/2015.
 */

import scala.pickling.Defaults._, scala.pickling.binary._

sealed trait MessageBase
case class Successful(blah: Map[Int, String]) extends MessageBase
case class Failed(blah: String) extends MessageBase

object Main extends App {

  val a = Successful(Map((1 -> "5"), (2 -> "hello")))
  val aOnWire = a.pickle.value

  val unpickledA = aOnWire.unpickle[MessageBase]
  // over the message bus
  process(unpickledA)


  val b = Failed("error!!!!!")
  val bOnWire = b.pickle.value
  // over the message bus
  val unpickledB = bOnWire.unpickle[MessageBase]

  process(unpickledB)

  def process(message: MessageBase) = message match {
    case Successful(blah) => println(blah)
    case Failed(blah) => println(blah)
  }
}
