package ch.awae.esgcal

trait ActivityReporter {
  def idle
  def busy(message: String = "")
  def working(progress: Int, message: String = "")
  def working(i: Int, of: Int, s: String): Unit = working(100 * i / of, s)

}

object ConsoleReporter extends ActivityReporter {
  def idle = println("System state: idle")
  def busy(s: String) = println(s"System state: busy ($s)")
  def working(i: Int, s: String): Unit = println(s"System state: working $i% ($s)")

}

class AsyncReporting(reporter: ActivityReporter, steps: Int) {

  private var state = 0

  def progress(msg: String) = synchronized {
    state += 1
    reporter.working(state, steps, s"$state/$steps: $msg");
  }

}