import Pipeline.{Acceptor, Performer}
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

  // internal flow events
  case class Perform()

  def addFlow() = Flow() { implicit builder =>

    val acceptor = builder.add(Acceptor((a:Add) => if (a.data != "Junk") Left(Perform()) else Right(Rejected())))

    val performer = builder.add(Performer( (a:Perform) => Coproduct[Out](Success("Whoop!")), Coproduct[Out](Accepted())))

    val fRejectPipe = Flow[Rejected].map[Out]{a => Coproduct[Out](Rejected())}

    val outputMerge = builder.add(Merge[Out](3))

    builder.addEdge(acceptor.rejected, fRejectPipe, outputMerge.in(0))
    builder.addEdge(acceptor.accepted, performer.in)

    builder.addEdge(performer.accepted, outputMerge.in(1))
    builder.addEdge(performer.result, outputMerge.in(2))


    import FlowGraph.Implicits._
//    accept ~> fRejected ~> merge
//    accept ~> fPerformed ~> merge

    (acceptor.in, outputMerge.out)
  }




}
