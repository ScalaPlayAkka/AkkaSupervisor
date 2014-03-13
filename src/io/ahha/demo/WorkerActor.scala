package io.ahha.demo

import akka.actor.{Actor, ActorLogging}

case class Result()

class WorkerActor extends Actor with ActorLogging {
  var state: Int = 0


  override def preStart() {
    log.info("Starting WorkerActor instance hashcode # {}", this.hashCode())
  }

  override def postStop() {
    log.info("Stopping WorkerActor instance hashcode # {}", this.hashCode())
  }

  def receive: Receive = {
    case value: Int =>
      if (value <= 0)
        throw new ArithmeticException("Number equal or less")
      else
        state = value
    case result: Result =>
      sender ! state
    case ex: NullPointerException =>
      throw new NullPointerException("Null Value Passed")
    case _ =>
      throw new IllegalArgumentException("Wrong Argument")
  }
}