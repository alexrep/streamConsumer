package com.example

import akka.actor.ActorRef

trait PartialPipeline{
  def close(): Unit
  def start(): Unit
  def twitConsumer: ActorRef
}