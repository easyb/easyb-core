import org.disco.easyb.exception.VerificationException

//println "starting"

before "set up some value", {
  given "bar is something", {
    bar = 12
  }
}


//println "starting scenario"

scenario "a sample scenario to play around with bar", {
  given "foo is something", {
    foo = 1
  }

  when "foo is doubled", {
    foo += foo
  }

  and "bar is doubled", {
    bar += bar
  }

  then "foo should be tests", {
    foo.shouldBe 2
  }
}

after "this after is at the top of the file", {
  then "make bar something else", {
//    println "running then in after"
    bar.shouldBe 24
  }
}

//println "done"

if(bar != 24){
  println "Test?!"
  throw new VerificationException("the one time after doesn't appear to be working!")
}