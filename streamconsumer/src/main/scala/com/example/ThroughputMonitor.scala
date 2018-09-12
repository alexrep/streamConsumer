package com.example

import akka.actor.{Actor, ActorLogging, ActorSelection, Cancellable, Props}
import scala.concurrent.duration._

object ThroughputMonitor{
  def props(rawInterval :Int) = Props(new ThroughputMonitor(rawInterval))
}

class ThroughputMonitor(rawInterval :Int) extends Actor with ActorLogging with Ticker{
  override val interval:FiniteDuration = rawInterval seconds
  override def message():AnyRef = Tick
  override def tickTarget:ActorSelection = context.system.actorSelection("/user/*-consumer")


  override def preStart: Unit ={
    super.preStart()
    initTicker()
  }

  override def postStop(): Unit = {
    super.postStop()
    destroyTicker()
  }

  override def receive: Receive  = {
    case ThroughputSlice(topic, count, timestamp) => log.info(s" for $topic received $count")
  }
}
