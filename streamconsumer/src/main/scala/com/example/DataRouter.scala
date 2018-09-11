package com.example

import akka.actor.{Actor, ActorLogging}

class DataRouter extends Actor with ActorLogging {
  log.info("Starting router")
  override def receive: Receive = {
    case msg:TopicClosed => log.info("Topic closed" + msg.topic)
    case msg: AggregatedStatus => log.info("Got" + msg.statuses.size + " tweets for " + msg.topic)
  }

}
