package com.example

import akka.actor.{Actor, ActorLogging, Props}

object StatisticsAggregator{
  def props = Props(new StatisticsAggregator)
}

class StatisticsAggregator extends Actor with ActorLogging{
  override def receive: Receive  = {
    case ThroughputSlice(topic, count, timestamp) => log.info(s" for $topic received $count")
  }
}
