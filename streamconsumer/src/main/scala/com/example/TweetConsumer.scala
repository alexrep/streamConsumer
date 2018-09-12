package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, PoisonPill, Props}

object TweetConsumer{
  def props(topic: String, aggregator:ActorRef) = Props(new TweetConsumer(topic, aggregator))
}

class TweetConsumer(topic: String, aggregator:ActorRef) extends Actor with ActorLogging{
  //def statisticsAggregator: ActorSelection = context.system.actorSelection("/user/throughputMonitor")
  def consuming(acc:Int): Receive = {
    case msg: TwitterStatus  =>
      aggregator ! msg
      log.debug("received tweet" + msg)
      context.become(consuming(acc+1))
    case Tick =>
      sender ! ThroughputSlice(topic, acc, System.currentTimeMillis)
      context.become(consuming(0))

    case StreamEnd =>
      self ! PoisonPill
      aggregator ! TopicClosed(topic)
      context.become(shutting)
  }
  def shutting:Receive = {
    case _  =>
  }

  def receive: Receive = consuming(0)

}
