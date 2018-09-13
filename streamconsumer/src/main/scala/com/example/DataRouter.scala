package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object DataRouter{
  def props(routees: Seq[ActorRef]) = Props(new DataRouter(routees))
}

class DataRouter(routees: Seq[ActorRef]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg: TopicClosed => log.info("Topic closed" + msg.topic)
    case msg: AggregatedStatus =>
      //log.debug("Got " + msg.statuses.size + " tweets for " + msg.topic)
      routees.foreach { _ ! msg}
  }
}
