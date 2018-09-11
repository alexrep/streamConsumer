package com.example

import akka.actor.ActorRef

trait SourceStream{
  def setFilter(filter: Set[String])

  def subscribePipeline(topic: String, receiver: ActorRef)

  def unsubscribePipeline(topic: String)
}
