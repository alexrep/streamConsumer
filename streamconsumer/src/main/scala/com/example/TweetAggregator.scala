package com.example

import akka.actor.{Actor, ActorLogging, ActorSelection, PoisonPill, Props}


object TweetAggregator{
  def props(topic: String, aggregationChunkSize:Int) = Props(new TweetAggregator(topic, aggregationChunkSize))
}

class TweetAggregator(topic: String, aggregationChunkSize:Int) extends Actor with ActorLogging {
  def router: ActorSelection = context.system.actorSelection("/user/DataRouter")

  def consuming(acc: List[TwitterStatus]): Receive = {
    case msg: TwitterStatus =>
      if (acc.size < aggregationChunkSize) {
        context.become(consuming(msg :: acc))
      }
      else {
        router ! AggregatedStatus(topic, acc)
        context.become(consuming(msg :: Nil))
      }

    case msg:TopicClosed =>
      router ! AggregatedStatus(topic, acc)
      router ! msg
      self ! PoisonPill
      context.become(shutting)
  }

  def shutting: Receive = {
    case _ =>
  }

  def receive = consuming(Nil)
}
