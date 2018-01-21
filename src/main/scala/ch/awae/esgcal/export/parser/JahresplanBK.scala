package ch.awae.esgcal.export.parser

import ch.awae.esgcal.export.Calendar
import ch.awae.esgcal.export.Calendar.Bern
import ch.awae.esgcal.export.EventParser
import ch.awae.esgcal.export.Directive
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.export.Entry
import ch.awae.esgcal.xssf.Workbook

object JahresplanBK extends EventParser {

  type In = (Calendar, String)
  type Out = (Symbol, String)

  def calendars = Bern :: Nil

  val extractor: Extractor = Jahresplan.extractor
  val parser: Parser = Jahresplan.parser
  val merger: Merger = Jahresplan.merger

  val director: Director = {
    case ('Termin, x) => Directive(0, 2, x) :: Nil
  }

  def getEntries(range: DateRange, directives: Directives): Entries = {
    val baseRow = 5 - range._1.toEpochDay
    val baseCol = 3
    // process each day
    val filtered = (for {
      (date, dirs) <- directives
      if !date.isBefore(range._1)
      if !date.isAfter(range._2)
    } yield {
      date -> dirs
    }) sortBy (_._1.toEpochDay)

    (for {
      index <- (0 until filtered.size).toList
      (date, dirs) = filtered(index)
      dow = date.germanDoW
      dir <- dirs
    } yield {
      List(
        Entry(index, 0, dow),
        Entry(index, 1, date.getDayOfMonth + "." + date.getMonthValue + "." + date.getYear),
        dir.toEntry(index, 0))
    }).flatten
  }

  def fillExcel(book: Workbook, entries: Entries, range: DateRange): Unit = {
    entries foreach book("Jahresplan BE").process
  }

}