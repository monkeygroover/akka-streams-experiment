import akka.stream.scaladsl.Flow
import shapeless.{CNil, :+:, Coproduct}

/**
 * Created by rilakkuma on 21/05/2015.
 */
package object Add {
  //inputs
  case class Add(data: String)

  type In = Add :+: CNil

  //outputs
  case class Accept()
  case class Success(message: String)
  case class Fail()

  // a coproduct of the valid outputs
  type Out = Accept :+: Success :+: Fail :+: CNil

  def addFlow() = Flow[Add].map[Out] {
    case Add("junk") => Coproduct[Out](Fail())
    case Add(x) => Coproduct[Out](Success(x))
  }
}
