package com.example
import akka.actor.{ActorSystem, Props}
import java.util.concurrent.LinkedBlockingQueue
import com.typesafe.config._
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class PipelineManager(configuration: Config) extends LazyLogging{
  val system: ActorSystem = ActorSystem("TwitterStream")
  val sources = collection.mutable.Map.empty[String, PartialPipeline]
  initializeInfrastructure(system)
  val taskQueue = new LinkedBlockingQueue[String]()
  private val twitterTimeout = 80 seconds

  system.scheduler.schedule(5 seconds, twitterTimeout) {
    val topic = taskQueue.poll()
    if (topic != null) {
      addTwitterSource(topic)
    }
  }

  def submitTopic(topic:String): Unit ={
    taskQueue.offer(topic)
  }

  def initializeInfrastructure(system: ActorSystem): Unit ={
    system.actorOf(StatisticsAggregator.props,"statisticsAggregator")
    system.actorOf(Props[DataRouter],"DataRouter")
  }

  def addTwitterSource(topic: String): Unit ={
    val partialPipeline = createTwitterPipeLine(topic, system)
    partialPipeline.start()
    sources += (topic -> partialPipeline)
    logger.debug(s"Topic added: $topic")
  }

  def createTwitterPipeLine(topic: String, system: ActorSystem)= new TwitterPartialPipeline(topic, system)

  def removeSource(topic:String): Unit ={
    sources.remove(topic) match {
      case None => logger.warn(s"No topic exist: $topic")
      case Some(source: PartialPipeline) =>
        source.close()
        logger.debug(s"Topic removed: $topic")
    }
  }


}
