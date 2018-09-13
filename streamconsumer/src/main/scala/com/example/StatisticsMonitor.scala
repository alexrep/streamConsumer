package com.example

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}

import scala.concurrent.duration._

object StatisticsMonitor{
  def props(rawInterval :Int) = Props(new StatisticsMonitor(rawInterval))
}

class StatisticsMonitor(rawInterval :Int) extends Actor with ActorLogging with Ticker{
  override val interval:FiniteDuration = rawInterval seconds
  override def tickTarget: ActorSelection = context.system.actorSelection("/user/*Counter")
  override def message():AnyRef = RequestStatistics

  override def preStart: Unit ={
    super.preStart()
    initTicker()
  }

  override def postStop(): Unit = {
    super.postStop()
    destroyTicker()
  }

  def receive: Receive  = {
    case TagCounters(counters) => outputTopCounters(counters)
    case UserCounters(counters) => outputTopCounters(counters)
  }

  private def outputTopCounters(counters: Counters): Unit ={
    counters.foreach {
      case(topic, counts) =>
        println(topic)
        outputTopicCounts(topCounters(counts - topic))
    }
  }

  private def outputTopicCounts(iterable: Iterable[(String,Int)]): Unit ={
    iterable.foreach({
      case (tag, counter) => println(s"\t$tag\t\t$counter")
    })
  }

  private def topCounters(counters: Map[String,Int]) ={
    counters.toVector.view.sortBy(- _._2).take(20)
  }

}
