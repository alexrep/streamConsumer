package com.example

import akka.actor.{Actor, ActorLogging, ActorSelection, PoisonPill, Props}


object TweetAggregator{
  def props(topic: String) = Props(new TweetAggregator(topic))
}


class TweetAggregator(topic: String) extends Actor with ActorLogging {
  val AGGREGATIONSIZE = 500

  def router: ActorSelection = context.system.actorSelection("/user/router")
  def consuming(acc: List[TwitterStatus]): Receive = {
    case msg: TwitterStatus =>
      if (acc.size <= AGGREGATIONSIZE) {
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
