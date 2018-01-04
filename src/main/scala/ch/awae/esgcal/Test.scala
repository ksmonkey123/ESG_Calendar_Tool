package ch.awae.esgcal

object Test extends App {
  /*
  implicit val reporter = ConsoleReporter

  val token = new LoginAgent().authenticate(8080)

  val tok = Await.result(token, Duration.Inf)

  val calendar = new CalendarAgent(tok) with CompoundCalendarJobs

  val res = Await.result(calendar.getCalendarPairs(" - Planung"), Duration.Inf)

  val start = (new Calendar.Builder()).setDate(2018, 0, 1).build.getTime
  val end = (new Calendar.Builder()).setDate(2019, 0, 1).build.getTime

  val x = for ((c0, c1) <- res) yield {
    println(c0.getSummary + " >> " + c1.getSummary)
    val events = Await.result(calendar.getEventsOfCalendar(c0)(start -> end), Duration.Inf)
    events -> (c0 -> c1)
  }

  Await.result(calendar.moveEvents(x), Duration.Inf)
  */

  new GUI

}