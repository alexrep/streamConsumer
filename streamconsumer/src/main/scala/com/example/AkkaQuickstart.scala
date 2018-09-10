//#full-example
package com.example

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging


object AkkaQuickstart extends App with LazyLogging {

  val system: ActorSystem = ActorSystem("helloAkka")
  val configuration = ConfigFactory.load()
  val manager = new PipelineManager(configuration)
  manager.addTwitterSource("Scala")
  manager.addTwitterSource("Python")

}
