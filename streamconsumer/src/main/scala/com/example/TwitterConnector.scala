package com.example
import twitter4j._
import com.typesafe.scalalogging.LazyLogging
import akka.actor.ActorRef


object TwitterConnector{
  def apply(actor: ActorRef): TwitterConnector = {
    new TwitterConnector(actor)
  }
}


class TwitterConnector(actor: ActorRef) extends LazyLogging with StatusMapping {
  val twitterStream:TwitterStream = new TwitterStreamFactory().getInstance()
  addMessageListener()

  def addMessageListener(): Unit ={
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

  def close(): Unit ={
    twitterStream.cleanUp()
    actor ! StreamEnd
  }

  def setFilter(filter : Set[String]): Unit ={
    val fq = new FilterQuery()
    fq.track(filter.mkString(","))
    twitterStream.filter(fq)
  }

}