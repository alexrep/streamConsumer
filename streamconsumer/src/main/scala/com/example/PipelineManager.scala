package com.example
import akka.actor.{ActorSystem, Props}
import com.typesafe.config._
import com.typesafe.scalalogging.LazyLogging

class PipelineManager(configuration: Config) extends LazyLogging{
  val system: ActorSystem = ActorSystem("TwitterStream")
  val sources = collection.mutable.Map.empty[String, PartialPipeline]
  initializeInfrastructure(system)

  def initializeInfrastructure(system: ActorSystem): Unit ={
    system.actorOf(StatisticsAggregator.props,"statisticsAggregator")
    system.actorOf(Props[DataRouter],"router")
  }

  def addTwitterSource(topic: String): Unit ={
    val partialPipeline = createTwitterPipeLine(topic, system)
    partialPipeline.start
    sources += (topic -> partialPipeline)
    logger.debug(s"Topic added: $topic")
  }

  def createTwitterPipeLine(topic: String, system: ActorSystem)= new TwitterPartialPipeline(topic, system)

  def removeSource(topic:String): Unit ={
    sources.remove(topic) match {
      case None => logger.warn(s"No topic exist: $topic")
      case Some(source: PartialPipeline) =>
        source.close
        logger.debug(s"Topic removed: $topic")
    }
  }


}
