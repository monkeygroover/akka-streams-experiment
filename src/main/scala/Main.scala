/**
 * Created by rilakkuma on 27/04/2015.
 */

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.mfglabs.stream.extensions.shapeless._
import shapeless._


object Main extends App {

  implicit val as = ActorSystem()
  implicit val fm = ActorFlowMaterializer()

  // type aliases for the allowed input and outputs (conjunctions)
  type In = Add.Add :+: Get.Get :+: CNil
  type Out = Add.States :+: Get.Out :+: CNil

  //a sample source wrapping incoming data in the Coproduct[In]
  val s = Source(() => Seq(
    Coproduct[In](Add.Add("Hello"))
    //Coproduct[In](Add.Add("Junk"))
  ).toIterator)

  // The sink to consume all output data
  val sink = Sink.foreach[Out](println _)

  val f = FlowGraph.closed(sink) { implicit builder => sink =>
    import FlowGraph.Implicits._

    val fr = builder.add(ShapelessStream.coproductFlow(Add.addFlow() :: Get.getFlow() :: HNil))

    s ~> fr.inlet
    fr.outlet ~> sink
  }

  f.run()
}
