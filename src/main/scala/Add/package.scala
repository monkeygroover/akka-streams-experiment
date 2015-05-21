import Pipeline.Acceptor
import akka.stream.scaladsl._
import shapeless.{:+:, CNil, Coproduct}

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

    val accept = builder.add(Acceptor((a:Add) => if (a.data != "Junk") Left(Accepted()) else Right(Rejected())))

    val fRejected = Flow[Rejected].map[Out]{a => Coproduct[Out](Rejected())}
    val fPerformed = Flow[Accepted].map[Out](a => Coproduct[Out](Success("yay")))

    val merge = builder.add(Merge[Out](2))

    builder.addEdge(accept.rejected, fRejected, merge.in(0))
    builder.addEdge(accept.accepted, fPerformed, merge.in(1))

//    import FlowGraph.Implicits._
//    accept ~> fRejected ~> merge
//    accept ~> fPerformed ~> merge

    (accept.in, merge.out)
  }




}
