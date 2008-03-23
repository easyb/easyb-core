package org.disco.bdd.issues

import org.disco.easyb.exception.VerificationException


scenario "maps that should not be contained in other maps", {
  given "a map with 3 values", {
    value = "testfoo"
    mp = [value: "Andy", another: 34, 55: "test"]
  }

  then "calls to shouldNotHave should pass for values that are not in the map", {
    mp.shouldNotHave("Mervin") // covers a value in a map
    mp.shouldNotHave([value: "Mervin", 55: "somethingnottest"]) // covers a map in a map where neither value is found
    mp.shouldNotHave([value: "Andy", 55: "somethingnottest"])  // covers a map in a map where at least one value is not found
    mp.shouldNotHave(55: "foobar") // covers map where key exists but value doesn't
    mp.shouldNotHave(notanother: "test") // covers map where key doesn't exist but value does
    mp.shouldNotHave(value)

    ensure(mp) {
      doesNotContain("Mervin") // covers a value in a map
      doesNotContain([value: "Mervin", 55: "somethingnottest"]) // covers a map in a map where neither value is found
      doesNotContain([value: "Andy", 55: "somethingnottest"])  // covers a map in a map where at least one value is not found
      doesNotContain(55: "foobar") // covers map where key exists but value doesn't
      doesNotContain(notanother: "test") // covers map where key doesn't exist but value does
      doesNotContain(value)
    }
  }

  then "calls to shouldNotHave should throw a VerificationException if they are in the map", {
    ensureThrows(VerificationException.class) {
      mp.shouldNotHave("Andy")
    }

    ensureThrows(VerificationException.class) {
      mp.shouldNotHave(55)
    }

    ensureThrows(VerificationException.class) {
      mp.shouldNotHave([value: "Andy", 55: "test"])
    }

    ensureThrows(VerificationException.class) {
      mp.shouldNotHave(55: "test")
    }

    ensureThrows(VerificationException.class) {
      ensure(mp){
        doesNotContain("Andy") // covers a value in a map
      }
    }

    ensureThrows(VerificationException.class) {
      ensure(mp) {
        doesNotContain(55)
      }
    }

    ensureThrows(VerificationException.class) {
      ensure(mp) {
        doesNotContain([value: "Andy", 55: "test"])
      }
    }

    ensureThrows(VerificationException.class) {
      ensure(mp) {
        doesNotContain(55: "test")
      }
    }
  }
}

scenario "string should not have the given value in it", {

  given "a string with value of test", {
    value = "test"
  }

  then "calls to shouldNotHave should pass for values that aren't in the test value", {
    value.shouldNotHave "foo"

    ensure(value) {
      doesNotContain "foo"
    }
  }

  then "calls to shouldNotHave should throw a VerificationException if they are in the test value", {

    ensureThrows(VerificationException.class) {
      value.shouldNotHave "est"
    }

    ensureThrows(VerificationException.class) {
      ensure(value) {
        doesNotContain("est")
      }
    }
  }
}

scenario "list should not contain specified value", {

  given "a list of values 1, 2, 3", {
    mylist = [1, 2, 3]
  }

  then "calls to shouldNotHave should pass for a value that isn't in mylist", {
    mylist.shouldNotHave(5)

    ensure(mylist) {
      doesNotContain(5)
    }
  }

  then "calls to shouldNotHave should pass for values that aren't in mylist", {
    mylist.shouldNotHave([1,4])

    ensure(mylist) {
      doesNotContain([1,4])
    }
  }

  then "calls to shouldNotHave should throw a VerificationException if the value appears in the list being tested", {

    ensureThrows(VerificationException.class) {
      mylist.shouldNotHave(3)
    }

    ensureThrows(VerificationException.class) {
      ensure(mylist) {
        doesNotContain(3)
      }
    }

  }

  then "calls to shouldNotHave should throw a VerificationException if all values appear in the list being tested", {

    ensureThrows(VerificationException.class) {
      mylist.shouldNotHave([1,2])
    }

    ensureThrows(VerificationException.class) {
      ensure(mylist) {
        doesNotContain([1,2])
      }
    }

  }

}

scenario "object should not contain specified properties", {

//it "should find properties on normal objects", {
//  def person = new Person("Andy", 11)
//  ensure(person) {
//    contains(firstName: "Andy")
//    contains(age: 11)
//  }
//
//  //person.has(age:11)
//  person.age.shouldBe 11
//}
//
//it "should find properties, in a map, on normal objects", {
//  def person = new Person("Jude", 41)
//  ensure(person) {
//    contains([firstName: "Jude", age: 41])
//  }
//}
//
//it "should find properties, in a map, on normal objects like contains", {
//  def person = new Person("Jill", 11)
//  ensure(person) {
//    has([firstName: "Jill", age: 11])
//  }
//}

}