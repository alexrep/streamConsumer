package com.example

import akka.actor.{Actor, ActorLogging}

class DataRouter extends Actor with ActorLogging {
  override def receive: Receive = {
    case msg:TopicClosed => log.debug("Topic closed" + msg.topic)
    case msg: AggregatedStatus => log.debug("Got 500 tweets for " + msg.topic)
  }

}
