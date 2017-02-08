package be.rubenpieters.freeshowcase.util

import be.rubenpieters.freeshowcase.util.json.CirceUtils
import be.rubenpieters.freeshowcase.util.http.HttpUtils
import be.rubenpieters.freeshowcase.util.cats.CatsUtils

/**
  * Created by ruben on 28/01/17.
  */
trait UtilImplicits
  extends CirceUtils.implicits
    with HttpUtils.implicits
    with CatsUtils.implicits

object UtilImplicits extends UtilImplicits
