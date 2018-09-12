package com.example

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging


object AkkaQuickstart extends App with LazyLogging {

  val configuration = ConfigFactory.load()
  val manager = new SystemManager(configuration)
  manager.submitTopic("Python")
  manager.submitTopic("Scala")
  manager.submitTopic("GBBO")

  manager.submitTopic("Trump")
  manager.submitTopic("StupidQuestionsAtWalmart")
}
