package com.example

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging


object AkkaQuickstart extends App with LazyLogging {

  val configuration = ConfigFactory.load()
  val manager = new PipelineManager(configuration)
  manager.submitTopic("Scala")
  manager.submitTopic("Python")
  manager.submitTopic("golang")
}
