package ch.awae.esgcal.export.parser

import ch.awae.esgcal.export.Calendar
import ch.awae.esgcal.export.Calendar.Bern
import ch.awae.esgcal.export.EventParser
import ch.awae.esgcal.export.Directive
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.Implicit._
import ch.awae.esgcal.export.Entry
import ch.awae.esgcal.xssf.Workbook
import java.time.LocalDate

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

    def formatDate(date: LocalDate) = {
      (date.getDayOfMonth, date.getMonthValue, date.getYear - 2000) match {
        case (x, y, z) if (x < 10) && (y < 10) => s"0$x.0$y.$z"
        case (x, y, z) if (x < 10)             => s"0$x.$y.$z"
        case (x, y, z) if (y < 10)             => s"$x.0$y.$z"
        case (x, y, z)                         => s"$x.$y.$z"
      }
    }

    val entries: List[Entry[_]] = (for {
      index <- (0 until filtered.size).toList
      (date, dirs) = filtered(index)
      dow = date.germanDoW
      dir <- dirs
    } yield {
      List(
        Entry(index, 0, dow),
        Entry(index, 1, formatDate(date)),
        dir.toEntry(index, 0))
    }).flatten

    val fillers = (for {
      index <- (0 until 100).toList
      row = index + filtered.size
    } yield {
      List(
        Entry(row, 0, "    "),
        Entry(row, 1, "    "),
        Entry(row, 2, "    "))
    }).flatten

    entries ::: fillers
  }

  def fillExcel(book: Workbook, entries: Entries, range: DateRange): Unit = {
    entries foreach book("Jahresplan BE").process
  }

}