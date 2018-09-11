package com.example

import akka.actor.{ActorRef, ActorSystem, Props}

class TwitterSource(system: ActorSystem) extends SourceStream {
  val twitterBroker: ActorRef = system.actorOf(Props[TwitterBroker] , "twitterBroker")
  val connector = TwitterConnector(twitterBroker)

  def setFilter(filter: Set[String]): Unit ={
    connector.setFilter(filter)
  }

  def subscribePipeline(topic: String, receiver: ActorRef): Unit ={
    twitterBroker ! AddPipeline(topic, receiver)
  }

  def unsubscribePipeline(topic: String): Unit ={
    twitterBroker ! RemovePipeline(topic)
  }
}
