package ch.awae.esgcal

class Throttler(cps: Int) {
  // milliseconds between calls
  private val delay = 1000 / cps
  private var lastCall = 0L

  def apply[T](body: => T): T = {
    this synchronized {
      val myEntry = lastCall + delay
      while (myEntry > System.currentTimeMillis) {}
      lastCall = System.currentTimeMillis
    }
    body
  }

}