package be.rubenpieters.freeshowcase

import java.text.Normalizer
import java.util.Locale

import cats.data.NonEmptyList
import cats.implicits._
import org.simmetrics.builders.StringMetricBuilder
import org.simmetrics.metrics.JaroWinkler
import org.simmetrics.simplifiers.{Simplifier, Simplifiers}
import org.simmetrics.tokenizers.Tokenizers

/**
  * Created by ruben on 5/01/17.
  */
case class VideoSearchResult(results: List[Video])

case class Video(title: String, url: String, videoId: String)

object Video {
  val similarityThreshold = 0.8f

  val accentNormalizer = new Simplifier {
    override def simplify(input: String): String = normalize(input)
  }

  val metric =
    StringMetricBuilder.`with`(new JaroWinkler())
      .simplify(Simplifiers.toLowerCase(Locale.ENGLISH))
      .simplify(accentNormalizer)
      .build()

  def containsTermsFully(title: String, terms: List[String]): Boolean = {
    ! terms.exists(term => ! title.contains(term))
  }

  def applyMetric(searchTerm: String, searchResultTitles: List[Video]): List[Video] = {
    searchResultTitles
      .map(t => (t, metric.compare(t.title, searchTerm)))
      .filter{ case (t, m) => m >= similarityThreshold}
      .sortBy(- _._2)
      .map(_._1)
  }

  def normalize(x: String) = Normalizer.normalize(x, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
}