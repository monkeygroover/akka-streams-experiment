/**
 * Created by rilakkuma on 27/04/2015.
 */

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import com.mfglabs.stream.extensions.shapeless._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time._
import shapeless._


object Main extends App with Matchers with ScalaFutures {

  implicit val as = ActorSystem()
  implicit val fm = ActorFlowMaterializer()
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Minutes), interval = Span(5, Millis))

  //domain
  case class Add(data: String)
  case class Get()

  // 1 - Create a type alias for your coproduct
  type C = Add :+: Get :+: CNil

  // The sink to consume all output data
  val sink = Sink.fold[Seq[C], C](Seq())(_ :+ _)

  // 2 - a sample source wrapping incoming data in the Coproduct
  val f = FlowGraph.closed(sink) { implicit builder => sink =>
    import FlowGraph.Implicits._

    val s = Source(() => Seq(
      Coproduct[C](Add("hello")),
      Coproduct[C](Get())
    ).toIterator)

    // 3 - our typed flows
    val add = Flow[Add].map {
      a => println(s"stage1 $a"); a}.map {
      a => println(s"stage2 $a"); a}.map {
      a => println(s"stage3 $a"); a}

    val get = Flow[Get].map {
      g => println(s"stage1 $g"); g}.map {
      g => println(s"stage2 $g"); g}.map {
      g => println(s"stage3 $g"); g}


    // >>>>>> THE IMPORTANT THING
    // 4 - build the coproductFlow in a 1-liner
    val fr = builder.add(ShapelessStream.coproductFlow(add :: get :: HNil))
    // <<<<<< THE IMPORTANT THING

    // 5 - plug everything together using akkastream DSL
    s ~> fr.inlet
    fr.outlet ~> sink
  }

  // 6 - run it
  f.run()
//    .futureValue.toSet should equal (Set(
//    Coproduct[C](Add("Hello"))
//  ))
}
