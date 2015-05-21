
import akka.stream.scaladsl.FlexiRoute
import akka.stream.{FanOutShape2, FanOutShape, OperationAttributes}


/**
 * Created by rilakkuma on 21/05/2015.
 */
package object Pipeline {
  import FanOutShape._

  class AcceptRejectShape[I, A, R](_init: Init[I] = Name[I]("AcceptReject"))
    extends FanOutShape[I](_init) {
    val outA = newOutlet[A]("accepted")
    val outB = newOutlet[R]("rejected")
    protected override def construct(i: Init[I]) = new AcceptRejectShape(i)
  }

  object Acceptor{
    def apply[I, A, R](f: I => Either[A, R]) = new Acceptor(f)
  }

  class Acceptor[I, A, R](f: I => Either[A, R]) extends FlexiRoute[I, AcceptRejectShape[I, A, R]](
    new AcceptRejectShape, OperationAttributes.name("Accept")) {

    import FlexiRoute._

    override def createRouteLogic(p: PortT) = new RouteLogic[I] {
      override def initialState =
        State[Any](DemandFromAll(p.outA, p.outB)) {
          (ctx, _, element) =>

            //nicer way to do this????
            if (f(element).isLeft) {
              ctx.emit(p.outA)(f(element).left.get)
            }
            else {
              ctx.emit(p.outB)(f(element).right.get)
            }
            SameState
        }

      override def initialCompletionHandling = eagerClose
    }
  }


  class UnzipShape[A, B](_init: Init[(A, B)] = Name[(A, B)]("Unzip"))
    extends FanOutShape[(A, B)](_init) {
    val outA = newOutlet[A]("outA")
    val outB = newOutlet[B]("outB")
    protected override def construct(i: Init[(A, B)]) = new UnzipShape(i)
  }
  class Unzip[A, B] extends FlexiRoute[(A, B), UnzipShape[A, B]](
    new UnzipShape, OperationAttributes.name("Unzip")) {
    import FlexiRoute._

    override def createRouteLogic(p: PortT) = new RouteLogic[(A, B)] {
      override def initialState =
        State[Any](DemandFromAll(p.outA, p.outB)) {
          (ctx, _, element) =>
            val (a, b) = element
            ctx.emit(p.outA)(a)
            ctx.emit(p.outB)(b)
            SameState
        }

      override def initialCompletionHandling = eagerClose
    }
  }
}
