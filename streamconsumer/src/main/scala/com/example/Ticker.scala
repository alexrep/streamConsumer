package com.example

import akka.actor.{Actor, ActorSelection, Cancellable}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

trait Ticker { this: Actor =>
  val interval:FiniteDuration
  def tickTarget:ActorSelection
  def message():AnyRef

  implicit val ec: ExecutionContext = context.dispatcher
  var ticker: Option[Cancellable] = None

  def initTicker(): Unit ={
    ticker = Some(context.system.scheduler.schedule(interval, interval) { tickTarget ! message()})
  }
  def destroyTicker(): Unit = {
    ticker.foreach {t => t.cancel()}
  }
}