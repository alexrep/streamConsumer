package com.example

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging

class PipelineManager(source:SourceStream, system: ActorSystem) extends LazyLogging {
  var pipelines = Map.empty[String, PartialPipeline]

  def addTopicPipeline(topic: String): Unit ={
    val pipeline:PartialPipeline = new TwitterPartialPipeline(topic,system)
    source.subscribePipeline(topic, pipeline.twitConsumer)
    pipeline.start()
    pipelines = pipelines + (topic -> pipeline)
    logger.info(s"Topic added: $topic")
  }

  def removeTopicPipeline(topic: String): Unit ={
    pipelines(topic).close()
    source.unsubscribePipeline(topic)
    pipelines = pipelines - topic
    logger.info(s"Topic removed: $topic")
  }
}
