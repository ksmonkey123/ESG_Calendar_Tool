package ch.awae.esgcal

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.language.implicitConversions

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

import com.google.api.services.calendar.model.EventDateTime

object Implicit {

  implicit class Object2Future[T](x: => T) {
    def f(implicit context: ExecutionContext): Future[T] = Future { x }
  }

  implicit class ArbitraryObjectPipelining[T](val x: T) extends AnyVal {

    def Λ[U](λ: T => U): T = {
      λ(x)
      x
    }

    def #>[B](λ: T => B): B = λ(x)

  }

  implicit class GoogleDateTimeDecoration(val datetime: EventDateTime) extends AnyVal {

    def toLocal = {
      val date = if (datetime.getDate == null) datetime.getDateTime else datetime.getDate
      val cal = Calendar.getInstance(TimeZone getTimeZone "GMT")
      val localTime = date.getValue
      cal setTimeInMillis localTime

      cal.getTime.toInstant.atZone(
        if (date.isDateOnly) ZoneId of "GMT" else ZoneId.systemDefault)
        .toLocalDateTime
    }

  }

  implicit class LocalDateTime2String(val date: LocalDateTime) extends AnyVal {

    def niceString = {
      val dow = date.getDayOfWeek.getValue match {
        case 1 => "Mo"
        case 2 => "Di"
        case 3 => "Mi"
        case 4 => "Do"
        case 5 => "Fr"
        case 6 => "Sa"
        case 7 => "So"
      }
      s"$dow, ${date.getDayOfMonth}.${date.getMonthValue}.${date.getYear} ${date.toLocalTime.toString}"
    }

  }

}