package com.example
import java.util

import twitter4j.{HashtagEntity, Status}

import scala.collection.JavaConverters._

trait StatusMapping{
  def mapStatus(status: Status): TwitterStatus = {
    val user = status.getUser()
    val tags = asScalaBuffer(util.Arrays.asList(status.getHashtagEntities())).flatMap({el => el}).map { entity:HashtagEntity => entity.getText() }

    TwitterStatus(status.getId(), status.getText(), Some(status.getLang()), status.isRetweeted(), status.getRetweetCount(), user.getId().toString, user.getName(), Some(user.getLocation()), tags)
  }
}
