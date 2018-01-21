package ch.awae.esgcal.export

import com.google.api.services.calendar.model.Event
import java.time.LocalDate
import ch.awae.esgcal.agent.CalendarAgent
import scala.concurrent.ExecutionContext
import ch.awae.esgcal.xssf.Workbook

trait EventParser {

  // internal types
  type In
  type Out

  // list of considered calendars
  def calendars: List[Calendar]

  // processing function types
  type Extractor = PartialFunction[(Calendar, Event), In]
  type Parser = In => Out
  type Merger = PartialFunction[(Out, Out), Out]
  type Director = PartialFunction[Out, List[Directive[_]]]

  // helper types
  type DateRange = (LocalDate, LocalDate)
  type Directives = List[(LocalDate, List[Directive[_]])]
  type Entries = List[Entry[_]]

  // processing functions
  val extractor: Extractor
  val parser: Parser
  val merger: Merger
  val director: Director

  def getEntries(range: DateRange, directives: Directives): Entries

  // processing
  def process(agent: CalendarAgent, from: LocalDate, to: LocalDate)(implicit context: ExecutionContext) = new EventProcessor(this).execute(agent, from, to)
  def processAll(agent: CalendarAgent, from: LocalDate, to: LocalDate, book: Workbook)(implicit context: ExecutionContext) = {
    process(agent, from, to) map { fillExcel(book, _, from -> to) }
  }

  def fillExcel(book: Workbook, entries: Entries, range: DateRange)

}