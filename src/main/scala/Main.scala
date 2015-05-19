/**
 * Created by rilakkuma on 27/04/2015.
 */

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl._
import com.mfglabs.stream.extensions.shapeless.ShapelessStream
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import shapeless._

object Main extends App with Matchers with ScalaFutures {

  implicit val as = ActorSystem()
  implicit val fm = ActorFlowMaterializer()
  implicit override val patienceConfig =
    PatienceConfig(timeout = Span(5, Minutes), interval = Span(5, Millis))

  // 1 - Create a type alias for your coproduct
  type C = Int :+: String :+: Boolean :+: CNil

  // The sink to consume all output data
  val sink = Sink.fold[Seq[C], C](Seq())(_ :+ _)

  // 2 - a sample source wrapping incoming data in the Coproduct
  val f = FlowGraph.closed(sink) { implicit builder => sink =>
    import FlowGraph.Implicits._
    val s = Source(() => Seq(
      Coproduct[C](1),
      Coproduct[C]("foo"),
      Coproduct[C](2),
      Coproduct[C](false),
      Coproduct[C]("bar"),
      Coproduct[C](3),
      Coproduct[C](true)
    ).toIterator)

    // 3 - our typed flows
    val flowInt = Flow[Int].map{i => println("i:"+i); i}
    val flowString = Flow[String].map{s => println("s:"+s); s}
    val flowBool = Flow[Boolean].map{s => println("s:"+s); s}

    // >>>>>> THE IMPORTANT THING
    // 4 - build the coproductFlow in a 1-liner
    val fr = builder.add(ShapelessStream.coproductFlow(flowInt :: flowString :: flowBool :: HNil))
    // <<<<<< THE IMPORTANT THING

    // 5 - plug everything together using akkastream DSL
    s ~> fr.inlet
    fr.outlet ~> sink
  }

  // 6 - run it
  f.run().futureValue.toSet should equal (Set(
    Coproduct[C](1),
    Coproduct[C]("foo"),
    Coproduct[C](2),
    Coproduct[C](false),
    Coproduct[C]("bar"),
    Coproduct[C](3),
    Coproduct[C](true)
  ))
}
