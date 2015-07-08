import akka.stream.scaladsl.Flow
import shapeless.{Coproduct, CNil, :+:}

/**
 * Created by rilakkuma on 21/05/2015.
 */
package object Get {
  //inputs
  case class Get()

  //outputs
  case class Accept()
  case class Success(message:String)
  case class Fail()

  // a coproduct of the valid outputs
  type Out = Accept :+: Success :+: Fail :+: CNil

  def getFlow() = Flow[Get].map[Out] { a => Coproduct[Out](Success("got it!"))}
}
