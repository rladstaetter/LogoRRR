package app.logorrr.util

import scala.concurrent.CancellationException
import scala.util.{Failure, Success}

// Example usage
object ThrottlerTestApp extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val throttler = new Throttler[Int, Int](i => {
    Thread.sleep(1000) // Simulate a blocking API call
    i * 2
  })

  val inputs = 1 to 5

  inputs.foreach { i =>
    throttler.process(i).onComplete {
      case Success(result) => println(s"Processed $i, result: $result")
      case Failure(e: CancellationException) => println(s"Computation for $i was cancelled")
      case Failure(e) => println(s"Error processing $i: $e")
    }

    Thread.sleep(200) // Simulate rapid input signals
  }

  // Wait for all computations to finish
  Thread.sleep(10000)
}
