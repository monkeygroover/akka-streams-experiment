/**
 * Created by rilakkuma on 27/04/2015.
 */

import shapeless._
import scala.pickling.Defaults._, scala.pickling.json._

case class DomainPayload(field1: String, field2: List[String])

sealed trait MessageBase
case class Successful(blah: Map[Int, String]) extends MessageBase
case class Failed(blah: String) extends MessageBase

object Main extends App {
//
//  val a = Successful(Map((1 -> "5"), (2 -> "hello")))
//  val aOnWire = a.pickle.value
//
//  val unpickledA = aOnWire.unpickle[MessageBase]
//  // over the message bus
//  process(unpickledA)
//
//
//  val b = Failed("error!!!!!")
//  val bOnWire = b.pickle.value
//  // over the message bus
//  val unpickledB = bOnWire.unpickle[MessageBase]
//
//  process(unpickledB)
//
//  def process(message: MessageBase) = message match {
//    case Successful(blah) => println(blah)
//    case Failed(blah) => println(blah)
//  }


  type PayloadIn[T] = T :: HNil
  type PayloadOut[T] = Int :: Int :: T :: HNil

  val documentIn= DomainPayload("hello", "hello" :: "Hello" :: Nil) :: HNil

  val documentWithId = 10 :: 1 :: documentIn


  def process(blah: HList) = blah match {
    case x: PayloadIn[_] => println("in")
    case id: PayloadOut[_] => println("out")
  }

  process(documentIn)
  process(documentWithId)
}
