package ch.awae.esgcal.export.parser

import ch.awae.esgcal.export.EventParser
import ch.awae.esgcal.export.Calendar
import ch.awae.esgcal.export.Calendar._
import ch.awae.esgcal.Implicit._
import com.google.api.services.calendar.model.Event
import java.time.ZoneOffset
import ch.awae.esgcal.export.Directive
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.export.Entry
import ch.awae.esgcal.xssf.Workbook

object GanztagListe extends EventParser {

  type In = (Calendar, String)
  type Out = (String, String)

  def calendars = List(Bern, Zürich, Konzerte)

  val extractor: Extractor = {
    case (calendar, event) if isFullDay(event) => calendar -> event.getSummary
  }

  val parser: Parser = {
    case (cal, event) => cal.toString -> event
  }

  val merger: Merger = {
    case ((a, x), (b, y)) if x == y => s"$a / $b" -> x
    case ((a, x), (b, y))           => s"$a / $b" -> s"$x / $y"
  }

  val director: Director = {
    case (calendar, event) => Directive(0, 1, calendar) :: Directive(0, 2, event) :: Nil
  }

  def isFullDay(event: Event) = {
    val start = event.getStart.toLocal.toEpochSecond(ZoneOffset.UTC)
    val end = event.getEnd.toLocal.toEpochSecond(ZoneOffset.UTC)
    (end - start) == (24 * 60 * 60)
  }

  def getEntries(range: DateRange, directives: Directives): Entries =
    for {
      index <- (0 until directives.size).toList
      (date, dirs) = directives(index)
      _ = println(dirs)
      if !dirs.isEmpty
      entry <- new Entry(index + 1, 0, date.toString) :: dirs.map(_.toEntry(index + 1, 0))
    } yield {
      entry
    }

  def fillExcel(book: Workbook, entries: Entries, range: DateRange) = {
    val sheet = book("Übersicht")
    entries foreach sheet.process
    sheet(0, 0) = "Datum"
    sheet(0, 1) = "Kalender"
    sheet(0, 2) = "Termin"
    sheet(0, 3) = "von"
    sheet(0, 4) = "bis"
  }

}