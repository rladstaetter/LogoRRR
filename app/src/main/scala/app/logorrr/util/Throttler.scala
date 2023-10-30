package app.logorrr.util

import scala.concurrent.{CancellationException, ExecutionContext, Future, Promise}
import scala.util.{Failure, Success}

/**
 * Simple implementation to throttle signals for an api
 *
 * @param api the (blocking) api to call
 * @param ec  Execution Context to run on
 * @tparam A input type
 * @tparam B output type
 */
class Throttler[A, B](api: A => B)(implicit ec: ExecutionContext)  {
  @volatile private var ongoingComputation: Option[(Promise[B], Future[B])] = None

  def process(input: A): Future[B] = {
    // Cancel any ongoing computation
    ongoingComputation.foreach { case (promise, _) =>
      if (!promise.isCompleted) {
        promise.failure(new CancellationException("Cancelled due to new input"))
      }
    }

    val newPromise = Promise[B]()
    val newComputation = Future {
      api(input)
    }

    newComputation.onComplete {
      case Success(result) =>
        if (!newPromise.isCompleted) newPromise.success(result)
      case Failure(e) =>
        if (!newPromise.isCompleted) newPromise.failure(e)
    }

    ongoingComputation = Some((newPromise, newComputation))

    newPromise.future
  }
}


