package be.rubenpieters.freeshowcase

import cats.data.NonEmptyList

/**
  * Created by ruben on 5/01/17.
  */
case class VideoSearchResult(results: List[Video])

case class Video(title: String, url: String, videoId: String)

object Video {
  def containsTermsFully(title: String, terms: List[String]): Boolean = {
    ! terms.exists(term => ! title.contains(term))
  }
}