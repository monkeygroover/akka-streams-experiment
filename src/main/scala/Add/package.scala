import Pipeline.Acceptor
import akka.stream.scaladsl.FlowGraph.Implicits._
import akka.stream.scaladsl._
import shapeless.{:+:, CNil, Coproduct}

package object Add {

  //inputs
  case class Add(data: String)

  case class Accepted()
  case class Rejected()
  case class Success()
  case class Failed()
  type States = Accepted :+: Rejected :+: Success :+: Failed :+: CNil

  // internal flow events
  case class Perform()

  def addFlow() = Flow() { implicit builder =>

    val accept = (a: Add) => if (a.data != "Junk") Left(Coproduct[States](Accepted())) else Right(Coproduct[States](Rejected()))
    val acceptor = builder.add(Acceptor(accept))

    val perform = Flow[States].map[States]{ a => Coproduct[States](Success())}

    val dup = builder.add(Broadcast[States](2))

    val outputMerge = builder.add(Merge[States](3))

    acceptor.rejected ~> outputMerge
    acceptor.accepted ~> dup ~> outputMerge
                         dup ~> perform ~> outputMerge


    (acceptor.in, outputMerge.out)
  }
}
