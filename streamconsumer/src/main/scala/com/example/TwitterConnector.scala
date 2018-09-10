package com.example
import twitter4j._
import com.typesafe.scalalogging.LazyLogging
import akka.actor.ActorRef


object TwitterConnector{
  def apply(filter: String, actor: ActorRef): TwitterConnector = {
    val connector = new TwitterConnector(actor)

    connector.setFilter(filter)
    connector
  }
}


class TwitterConnector(actor: ActorRef) extends LazyLogging with StatusMapping {
  val twitterStream:TwitterStream = new TwitterStreamFactory().getInstance()
  addMessageListener

  def addMessageListener: Unit ={
    twitterStream.addListener(new StatusListener(){

      import twitter4j.StallWarning
      import twitter4j.StatusDeletionNotice

      def onStatus(status: Status): Unit = {
        actor ! mapStatus(status)
      }

      def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {
        //logger.debug("Got a status deletion notice id:" + statusDeletionNotice.getStatusId)
      }

      def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {
        //logger.warn("Got track limitation notice:" + numberOfLimitedStatuses)
      }

      def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {
        //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId)
      }

      def onStallWarning(warning: StallWarning): Unit = {
        logger.warn("Got stall warning:" + warning)
      }

      def onException(ex: Exception): Unit = {
        logger.error("Got error: ")
        ex.printStackTrace()
      }
    })

  }

  def close: Unit ={
    twitterStream.cleanUp()
    actor ! StreamEnd
  }

  def start(): Unit ={
    twitterStream.sample()
  }

  def setFilter(filter : String): Unit ={
    val fq = new FilterQuery()
    fq.track(filter)
    twitterStream.filter(fq)
  }

}


trait StatusMapping{
  def mapStatus(status: Status) = {
    val user = status.getUser()
    val tags = status.getText()
      .split(" ")
      .filter(s => s.nonEmpty)
      .filter(s => s.startsWith("#"))
      .map(s => s.substring(1))

    TwitterStatus(
      status.getText(),
      Some(status.getLang()),
      status.isRetweeted(),
      status.getRetweetCount(),
      user.getId(),
      user.getName(),
      Some(user.getLocation()),
      tags
    )
  }
}
