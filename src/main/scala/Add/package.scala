import Pipeline.Acceptor
import akka.stream.scaladsl._
import com.mfglabs.stream.extensions.shapeless.CoproductFlexiRoute
import shapeless.{CNil, :+:, Coproduct}

/**
 * Created by rilakkuma on 21/05/2015.
 */
package object Add {
  //inputs
  case class Add(data: String)

  type In = Add :+: CNil

  //outputs
  case class Accepted()
  case class Success(message: String)
  case class Rejected()

  // a coproduct of the valid outputs
  type Out = Accepted :+: Success :+: Rejected :+: CNil

  type Accept = Accepted :+: Rejected :+: CNil


  def addFlow() = Flow() { implicit builder =>
    import FlowGraph.Implicits._

    val accept = builder.add(Acceptor((a:Add) => if (a.data != "Junk") Left(Accepted()) else Right(Rejected())))
    val unzip = builder.add(new Unzip)

    val bcast = builder.add(Broadcast[Rejected](2))
    val merge = builder.add(Merge[Out](2))

    val fRejected = Flow[Rejected].map[Out]{a => Coproduct[Out](Rejected())}
    val fPerformed = Flow[Accepted].map[Out](a => Coproduct[Out](Success("yay")))

    accept ~> fRejected ~> merge
    //accept ~> fPerformed ~> merge

    (accept.in, merge.out)
  }




}
