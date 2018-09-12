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

  override def receive: Receive  = {
    case TagCounters(counters) => log.info(counters.toString())
  }
}
