package io.ahha.demo

import akka.actor.{Props, ActorSystem}
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.pattern.ask


object Main {

  def main(args: Array[String]) {
    val system = ActorSystem("faultTolerance")
    val log = system.log
    val originalValue: Int = 0
    val supervisor = system.actorOf(Props[SupervisorActor],
      name = "supervisor")
    log.info("Sending value 8, no exceptions should be thrown! ")
    var mesg: Int = 8
    supervisor ! mesg
    implicit val timeout = Timeout(5 seconds)
    var future = (supervisor ? new Result).mapTo[Int]
    var result = Await.result(future, timeout.duration)
    log.info("Value Received-> {}", result)

    log.info("Sending value -8, ArithmeticException should be thrown! Our Supervisor strategy says resume!")
    mesg = -8
    supervisor ! mesg
    future = (supervisor ? new Result).mapTo[Int]
    result = Await.result(future, timeout.duration)
    log.info("Value Received-> {}", result)

    log.info("Sending value null, NullPointerException should be thrown! Our Supervisor strategy says restart !")
    supervisor ! new NullPointerException
    future = (supervisor ? new Result).mapTo[Int]
    result = Await.result(future, timeout.duration)
    log.info("Value Received-> {}", result)

    log.info("Sending value \"String\", IllegalArgumentException should be thrown! Our Supervisor strategy says Stop !")
    supervisor ? "Do Something"

    system.shutdown()
  }
}
