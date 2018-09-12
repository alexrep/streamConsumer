package com.example

import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.scalalogging.LazyLogging

class PipelineManager(source:SourceStream, system: ActorSystem) extends LazyLogging {
  def addTopicPipeline(topic: String): Unit ={
    val twitConsumer = createPartialPipeline(topic, system)
    source.subscribePipeline(topic, twitConsumer)
    logger.info(s"Topic added: $topic")
  }

  def removeTopicPipeline(topic: String): Unit ={
    source.unsubscribePipeline(topic)
    logger.info(s"Topic removed: $topic")
  }

  def createPartialPipeline(topic: String, system: ActorSystem): ActorRef ={
    val aggregator: ActorRef = system.actorOf(TweetAggregator.props(topic),s"$topic-aggregator")
    system.actorOf(TweetConsumer.props(topic, aggregator), s"$topic-consumer")
  }
}
