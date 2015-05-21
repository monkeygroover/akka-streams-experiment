import akka.stream.scaladsl._
import akka.stream._

/**
 * Created by rilakkuma on 21/05/2015.
 */
package object Pipeline {
  import FanOutShape._

  class AcceptRejectShape[I, A, R](_init: Init[I] = Name[I]("AcceptReject"))
    extends FanOutShape[I](_init) {
    val accepted = newOutlet[A]("accepted")
    val rejected = newOutlet[R]("rejected")
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
        State[Any](DemandFromAll(p.accepted, p.rejected)) {
          (ctx, _, element) =>

            //nicer way to do this????
            if (f(element).isLeft) {
              ctx.emit(p.accepted)(f(element).left.get)
            }
            else {
              ctx.emit(p.rejected)(f(element).right.get)
            }
            SameState
        }

      override def initialCompletionHandling = eagerClose
    }
  }

}
