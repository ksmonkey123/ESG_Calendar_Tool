package ch.awae.esgcal.export.parser

import ch.awae.esgcal.Implicit._

import ch.awae.esgcal.export.Calendar
import ch.awae.esgcal.export.Calendar.Zürich
import ch.awae.esgcal.export.EventParser
import ch.awae.esgcal.xssf.CellMagnet._
import ch.awae.esgcal.xssf.Workbook

object JahresplanZH extends EventParser {

  type In = (Calendar, String)
  type Out = (Symbol, String)

  def calendars = Zürich :: Nil

  val extractor: Extractor = Jahresplan.extractor
  val parser: Parser = Jahresplan.parser
  val merger: Merger = Jahresplan.merger
  val director: Director = JahresplanBK.director
  def getEntries(range: DateRange, directives: Directives): Entries = JahresplanBK.getEntries(range, directives)

  def fillExcel(book: Workbook, entries: Entries, range: DateRange): Unit = {
    entries foreach book("Jahresplan ZH").process
  }

}