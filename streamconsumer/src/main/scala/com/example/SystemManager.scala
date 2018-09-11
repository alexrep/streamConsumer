package com.example
import akka.actor.{ActorSystem, Props}

import com.typesafe.config._
import com.typesafe.scalalogging.LazyLogging
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._



class SystemManager(configuration: Config) extends LazyLogging{
  val system: ActorSystem = ActorSystem("TwitterStream")

  var topics = Set.empty[String]

  var topicsToAdd = Set.empty[String]
  var topicsToRemove = Set.empty[String]



  initializeInfrastructure(system)

  val twitterSource:SourceStream = new TwitterSource(system)
  val pipelineManager = new PipelineManager(twitterSource, system)

  private val twitterTimeout = 40 seconds

  system.scheduler.schedule(5 seconds, twitterTimeout){ processQueue() }


  def initializeInfrastructure(system: ActorSystem): Unit ={
    system.actorOf(StatisticsAggregator.props,"statisticsAggregator")
    system.actorOf(Props[DataRouter],"DataRouter")
  }

  def processQueue(): Unit ={
    topicsToRemove.foreach(removeTopic)
    topicsToAdd.foreach(addTopic)
    topics = topics ++ topicsToAdd -- topicsToRemove
    twitterSource.setFilter(topics)
    topicsToRemove = Set.empty[String]
    topicsToAdd = Set.empty[String]
  }

  private def addTopic(topic: String): Unit ={
    pipelineManager.addTopicPipeline(topic)
  }

  private def removeTopic(topic: String): Unit ={
    pipelineManager.removeTopicPipeline(topic)
  }

  def submitTopic(topic: String): Unit ={
    topicsToAdd = topicsToAdd + topic
  }
  def unsubmitTopic(topic: String): Unit ={
    topicsToRemove = topicsToRemove + topic
  }


}
