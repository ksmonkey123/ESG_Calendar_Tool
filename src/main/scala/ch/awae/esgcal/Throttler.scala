package ch.awae.esgcal

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * Invocation Throttler regulating the rate of code execution.
 *
 * This throttler can be used to limit the number of code block
 * executions per second if e.g. an API is rate limited. In the
 * case of the Google Calendar API there exists a limit of 500
 * API calls per 100 seconds, therefore throttling all API methods
 * to 5 calls per second prevents the API calls from exceeding
 * any rate limits.
 *
 * @author Andreas Wälchli <andreas.waelchli@me.com>
 * @version 1.2 (2018-01-03)
 *
 * @param cps the number of supported calls per second
 */
class Throttler(cps: Int) {

  private val lock = new ReentrantLock(true)
  // milliseconds between calls
  private val delay = 1000 / cps
  private var lastCall = 0L

  /**
   * Evaluates and the body code block only once the throttler is ready
   * to accept another call
   */
  def apply[T](body: => T)(implicit context: ExecutionContext) = Future {
    lock.lock()
    val myEntry = lastCall + delay
    // busy wait until ready
    while (myEntry > System.currentTimeMillis) {}
    lastCall = System.currentTimeMillis
    lock.unlock()
    body
  }

}