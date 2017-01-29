package be.rubenpieters.freeshowcase.unsafe

import be.rubenpieters.freeshowcase.{TestSpec, Track}

/**
  * Created by ruben on 29/01/17.
  */
class LastfmApiTest extends TestSpec {
  val lastfmApiSample1 = testString("lastfmApiSample1.txt")

  "parseLastfmApiJson" should "parse sample 1 correctly" in {
    inside (LastfmApi.parseLastfmApiJson(lastfmApiSample1)) { case Right(list) =>
      list should contain theSameElementsAs List(
        Track("Lantlôs","Pulse/Surreal")
        , Track("Microfilm","The Bay of Future Passed")
        , Track("Mogwai","We're No Here")
        , Track("EF","Tomorrow My Friend...")
        , Track("Low","Lullaby")
        , Track("Russian Circles","Afrika")
        , Track("Australasia","Aorta")
        , Track("Maybeshewill","He Films the Clouds Pt. 2")
        , Track("Blackfilm","Midnight to 4 AM")
        , Track("Ne Obliviscaris","As Icicles Fall")
        , Track("Flies Are Spies From Hell","Mountain Language")
        , Track("Jon Hopkins","Open Eye Signal")
        , Track("Audrey Fall","Priboi")
        , Track("Vessel","Gentlemen, It's Been An Honor Serving With You")
        , Track("Awake in Sleep","Flowing Seasons")
        , Track("Darren Korb","In Circles")
        , Track("Cinemechanica","Take Me To The Hospital")
        , Track("Mount Kimbie","Made to Stray")
        , Track("La Mar","Releash")
        , Track("Long Arm","Key Door")
        , Track("Leech","Nebeleben")
        , Track("Leech","Delirium Dancer")
        , Track("Agalloch","In the Shadow of Our Pale Companion")
        , Track("Beware of Safety","Memorial Day")
        , Track("Australasia","Spine")
        , Track("AL_X","Bloom")
        , Track("Cloudkicker","Seattle")
        , Track("You.May.Die.In.The.Desert","West Of 1848")
        , Track("TAMUSIC","FF7 Fighting / 闘う者達")
        , Track("Toundra","Danubio / Danube")
        , Track("Misuse","desecid")
        , Track("Martin Stig Andersen","Hotel (with SFX)")
        , Track("This is Your Captain Speaking","Henry & Maximus")
        , Track("Empires","Sickly Brown Sky")
        , Track("Io","Where Were Going We Don't Need Roads")
        , Track("Barrows","Pirates")
        , Track("The Seven Mile Journey","Passenger's Log, the Unity Fractions")
        , Track("Powder! Go Away","lost touch.")
        , Track("Beware of Safety","The Supposed Common")
        , Track("Explosions in the Sky","A Poor Man's Memory")
        , Track("Daturah","Hybrisma")
        , Track("Sleeping in Gethsemane","King of the World")
        , Track("Slint","Good Morning, Captain")
        , Track("Sigur Rós","Gong")
        , Track("dB soundworks","Fast Track to Browntown (Ch 3 Boss)")
        , Track("dB soundworks","Rocket Rider (Ch 3 Dark World)")
        , Track("dB soundworks","The Battle of Lil' Slugger (Ch 1 Boss)")
        , Track("The Seven Mile Journey","Identity Journals (Anonymous)")
        , Track("Pelican","City of Echoes")
        , Track("Joy Wants Eternity","Yet Onward We Marched")
      )
    }
  }
}
