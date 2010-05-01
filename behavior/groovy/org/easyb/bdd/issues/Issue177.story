
ignore {
  scenario "this is purposely a broken scenario", {
      given "some variable with a value", {
          val = 12
      }
      then "to force an error, one should verify it is not 12", {
          val.shouldNotEqual 12
      }
  }

  scenario "another purposely a broken scenario", {
      given "some variable with a value", {
          val = 13
      }
      then "to force an error, one should verify it is not 12", {
          val.shouldNotEqual 13
      }
  }
}