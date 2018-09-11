package com.example

import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem, Cancellable}
import scala.concurrent.ExecutionContext.Implicits.global

class TwitterPartialPipeline (topic: String, system: ActorSystem) extends PartialPipeline {
  private val aggregator: ActorRef = system.actorOf(TweetAggregator.props(topic),s"$topic-aggregator")
  private val consumer: ActorRef = system.actorOf(TweetConsumer.props(topic, aggregator), s"$topic-consumer")
  private var scheduler: Option[Cancellable] = None
  private val interval = 10 seconds

  def twitConsumer: ActorRef = consumer

  def start(): Unit = {
    scheduler = Some(system.scheduler.schedule(interval, interval, consumer, Tick))
  }

  def close(): Unit = {
    scheduler.foreach((s:Cancellable) => s.cancel )
  }

}
