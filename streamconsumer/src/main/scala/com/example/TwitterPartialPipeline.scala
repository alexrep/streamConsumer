package com.example

import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorSystem, Cancellable}
import scala.concurrent.ExecutionContext.Implicits.global

class TwitterPartialPipeline (topic: String, system: ActorSystem) extends PartialPipeline {
  private val aggregator: ActorRef = system.actorOf(TweetAggregator.props(topic),s"$topic-aggregator")
  private val consumer: ActorRef = system.actorOf(TweetConsumer.props(topic, aggregator), s"$topic-consumer")
  private val connector = TwitterConnector(topic, consumer)
  private var scheduler: Option[Cancellable] = None

  def start(): Unit = {
    connector.start()
    scheduler = Some(system.scheduler.schedule(10 seconds, 10 seconds, consumer, Tick))
  }

  def close(): Unit = {
    connector.close()
    scheduler.foreach((s:Cancellable) => s.cancel )
  }

}
