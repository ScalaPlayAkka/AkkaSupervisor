package io.ahha.demo

import akka.actor.{OneForOneStrategy, Props, ActorLogging, Actor}
import akka.actor.SupervisorStrategy.{Escalate, Stop, Restart, Resume}
import scala.concurrent.duration._

class SupervisorActor extends Actor with ActorLogging {
  val childActor = context.actorOf(Props[WorkerActor], name = "workerActor")
  override val supervisorStrategy = OneForOneStrategy( maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
    case _: ArithmeticException => Resume
    case _: NullPointerException => Restart
    case _: IllegalArgumentException => Stop
    case _: Exception => Escalate
  }

  def receive = {
    case result: Result =>
      childActor.tell(result, sender)
    case msg: Object =>
      childActor ! msg
  }
}
