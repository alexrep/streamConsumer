package com.example

import akka.actor.{Actor, ActorLogging}

abstract class CounterActor extends Actor with ActorLogging with TweetPropertyCounter {
  def receive:Receive = count(Map.empty)

  def count(counters: Counters): Receive ={
    case AggregatedStatus(topic, statuses) => context.become(count(updateCounters(counters, topic, statuses)))
    case TopicClosed(topic) =>  context.become(count(counters - topic))
    case RequestStatistics => sender ! statisticsMessage(counters)
  }

  def statisticsMessage(counters: Counters):CountersReport


}
