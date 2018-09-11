package com.example

import akka.actor.{Actor, ActorLogging, ActorRef}

class TwitterBroker extends Actor with ActorLogging {
  def standBy: Receive = {
    case AddPipeline(topic: String, receiver: ActorRef) => context.become(routing(Map[String, ActorRef](topic -> receiver)))
    case _ =>
  }

  def routing(receivers: Map[String, ActorRef]): Receive = {
    case AddPipeline(topic, receiver) => context.become(routing(receivers + (topic -> receiver)))
    case RemovePipeline(topic) =>
      receivers(topic) ! StreamEnd
      context.become(getReceive(receivers - topic))
    case msg: TwitterStatus =>
      receivers.foreach { case (topic, receiver) => if (checkContaining(msg, topic)) {receiver ! msg} }
  }

  def receive: Receive  = standBy

  private def checkContaining(msg: TwitterStatus, topic: String)=
    (msg.status + msg.hashTags.mkString(" ")).toLowerCase.contains(topic.toLowerCase)

  private def getReceive(receivers: Map[String, ActorRef]) = {
    if (receivers.isEmpty){
      standBy
    }else{
      routing(receivers)
    }
  }
}
