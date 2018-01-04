package ch.awae.esgcal

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object Implicit {

  implicit class Object2Future[T](x: => T) {
    def f(implicit context: ExecutionContext): Future[T] = Future { x }
  }

}