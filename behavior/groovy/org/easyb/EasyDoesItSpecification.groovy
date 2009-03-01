package org.easyb

import org.easyb.bdd.Person
import org.easyb.exception.VerificationException;

//isNull()
//isNotNull()
//isA<class type>
//isEqualTo(value)
//isEqualTo<value>
//isNotEqualTo<value>
//isTrue
//isFalse
//fail(message)
//fail(message, exception)
//fail(message, epected, actual

//todo
//how about contains(value) or contains<Value> and this 
// value could be a substring or a value(s) in a collection?

String value = "test"

//is is a method on Groovy's nullobject and that's the one
//being picked up in this method call. The question is
//"how do you override this specification?"
it "should work to say is test", {
    //ensure(value){
    //	isEqualTo("test")
    //}
    value.shouldBe("test")
}

it "should not be null and should be a String (without ()s)", {
    //ensure(value){
    // isNotNull
    //isAString
    //}
    value.shouldNotBe(null)
    value.shouldBeA(String)
}

it "should not be null AND!! should be a String (without ()s)", {
    //ensure(value){
    //	isNotNull
    //	and
    //	isAString
    //}
    value.shouldntBe(null)
    and
    value.shouldBeA(String)
}

it "should be null", {
    def tst = null
    ensure(tst) {
        isNull()
    }
    //tst.isNull()
}

it "should be null", {
    Integer ival = null
    ensure(ival) {
        isNull
    }
}

it "should not be null and should be an Integer", {
    Integer ival = 10
    //ensure(ival){
    //	isNotNull
    //	isAnInteger
    //}
    ival.shouldNotBe(null)
    ival.shouldBeAn(Integer)
}



it "should be equal to test", {
    //ensure(value){
    //	isEqualTo("test")
    //}
    value.shouldBeEqualTo("test")
}

it "should be equal to 23", {
    i23 = 23
    //ensure(i23){
    //	isEqualTo(23)
    //}
    i23.shouldBeEqualTo(23)
}

it "should be equal to Test without ()s", {
    mVal = "Test"
    //ensure(mVal){
    //	isEqualToTest
    //}
    mVal.shouldBe "Test"
}

it "should be equal to Test with space", {
    mVal = "Fest"
    //ensure(mVal){
    //	isEqualTo "Fest"
    //}
    mVal.shouldBeEqualTo "Fest"
}

it "should be equal to 23 without ()s", {
    mVal = 23
    //ensure(mVal){
    //	isEqualTo23
    //}
    mVal.shouldBe 23
}

it "should be equal to 33 with break", {
    mVal = 33
    //ensure(mVal){
    //	isEqualTo 33
    //}
    mVal.shouldBeEqualTo 33

}

it "should contain est", {
    //ensure(value){
    //	contains("est")
    //}
    value.shouldHave("est")
}

it "should find a value in an array", {
    ensure([1, 2, 3]) {
        contains(3)
    }
    //[1,2,3].has(3)
}

it "should find some values in an array", {
    ensure([1, 2, 3]) {
        contains([2, 3])
    }
    //[1,2,3].has [2,3]
}

it "should find map key values", {
    def mp = [value: "Andy", another: 34, 55: "test"]
    ensure(mp) {
        contains("Andy")
        contains([value: "Andy", 55: "test"])
        contains(55: "test")
        contains(another: 34)
        contains(value)
    }

    mp.shouldHave("Andy")
    mp.shouldHave(55: "test")
    mp.shouldHave([value: "Andy", 55: "test"])
    mp.shouldHave(value)
    mp.shouldHave(another: 34)
}

it "should find properties on normal objects", {
    def person = new Person("Andy", 11)
    ensure(person) {
        contains(firstName: "Andy")
        contains(age: 11)
    }

    //person.has(age:11)
    person.age.shouldBe 11
}

it "should find properties, in a map, on normal objects", {
    def person = new Person("Jude", 41)
    ensure(person) {
        contains([firstName: "Jude", age: 41])
    }
}

it "should find properties, in a map, on normal objects like contains", {
    def person = new Person("Jill", 11)
    ensure(person) {
        has([firstName: "Jill", age: 11])
    }
}


def stringTrue = "true"
def stringFalse = "false"
def booleanTrue = true
def booleanFalse = false

it "should equal string true", {
    ensure(stringTrue) {
        isEqualTo("true")
    }
}



it "should be isTrue", {
    ensure(stringTrue) {
        isTrue
    }
}

it "should equal string false", {
    ensure(stringFalse) {
        isEqualTo("false")
    }
}

it "should be isFalse", {
    ensure(stringFalse) {
        isFalse
    }
}

it "should be equal to boolean true", {
    ensure(booleanTrue) {
        isEqualTo(true)
    }
}

it "should be equal to boolean true with spaces", {
    ensure(booleanTrue) {
        isEqualTo true
    }
}

it "should be that true isTrue", {
    ensure(booleanTrue) {
        isTrue
    }
}

it "should be equal to boolean false", {
    ensure(booleanFalse) {
        isEqualTo(false)
    }
}

it "should be that false isFalse", {
    ensure(booleanFalse) {
        isFalse
    }
}

it "should not be equal to boolean true", {
    ensure(booleanFalse) {
        isNotEqualTo(true)
    }
}

it "should not be equal to boolean false", {
    ensure(booleanTrue) {
        isNotEqualTo(false)
    }
}

it "should not be equal to test with spaces", {
    ensure("notTest") {
        isNotEqualTo "test"
        and
        isNotEqualToTest
    }
}

it "should not be equal to test", {
    ensure("notTest") {
        isNotEqualTo("test")
        and
        isNotEqualToTest
    }
}

it "should pass if two references that point to different objects are passed to isNotEqualTo", {
    ensure(new Object()) {
        isNotEqualTo(new Object())
    }
}

it "should pass if two references that point to the same object are passed to isEqualto", {
    objectOne = new Object()
    objectTwo = objectOne
    ensure(objectOne) {
        isEqualTo(objectTwo)
    }
}

it "should throw VerificationException with specified message", {
    try {
        fail("failure message only")
        throw new Exception("Expected a VerificationException")
    } catch (RuntimeException re) {
        // pass because VerificationException is a subclass of runtime
    }
}

it "should throw VerificationException with specified message and take an exception as second arg", {
    try {
        fail("failure message with exception", new Exception("Holder exception"))
        throw new Exception("Expected a VerificationException")
    } catch (RuntimeException re) {
        // pass because VerificationException is a subclass of runtime
    }
}

it "should throw VerificationException, expected and actual must also match those passed in", {
    try {
        fail("failure message with exception", "expected", "actual")
        throw new Exception("Expected a VerifcationException")
    } catch (RuntimeException re) {
        ensure("expected" == ((VerificationException) re).getExpected())
        ensure("actual" == ((VerificationException) re).getActual())
    }
}

