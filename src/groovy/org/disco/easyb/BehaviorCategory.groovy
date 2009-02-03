package org.disco.easyb

import org.codehaus.groovy.runtime.NullObject
import org.disco.easyb.exception.VerificationException

class BehaviorCategory {

  static void throwValidationException(out, msg) {
    throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
  }

  static void shouldBeGreaterThan(Object self, value) {
    shouldBeGreaterThan(self, value, null)
  }

  static void shouldBeGreaterThan(Object self, value, String msg) {
    if (value > self) {
      throwValidationException("${value} was greater than ${self}", msg)
    } else if (value == self) {
      throwValidationException("${value} was equal to ${self}", msg)
    }
  }

  static void shouldBeLessThan(Object self, value, String msg) {
    if ((self.getClass() == NullObject.class) || (value == null)) {
      throwValidationException("${value} can not be compared to ${self}", msg)

    } else if (value < self) {
      throwValidationException("${value} was less than ${self}", msg)

    } else if (value == self) {
      throwValidationException("${value} was equal to ${self}", msg)
    }
  }

  static void shouldBeLessThan(Object self, value) {
    shouldBeLessThan(self, value, null)
  }

  static void shouldBeEqual(Object self, value) {
    shouldBe(self, value, null)
  }

  static void shouldBeEqual(Object self, value, String msg) {
    shouldBe(self, value, msg)
  }

  static void shouldBeEqualTo(Object self, value) {
    shouldBe(self, value, null)
  }

  static void shouldBeEqualTo(Object self, value, String msg) {
    shouldBe(self, value, msg)
  }

  static void shouldEqual(Object self, value) {
    shouldBe(self, value, null)
  }

  static void shouldEqual(Object self, value, String msg) {
    shouldBe(self, value, msg)
  }

  static void shouldBe(Object self, value) {
    shouldBe(self, value, null)
  }

  static void shouldBe(Object self, value, String msg) {
    isEqual(self, value, msg)
  }

  private static void isEqual(self, value, String msg) {
    if (self.getClass() == NullObject.class) {
      if (value != null) {
        throwValidationException("expected ${value.toString()} but target object is null", msg)
      }
    } else if (value.getClass() == String.class) {
      if (!value.toString().equals(self.toString())) {
        throwValidationException("expected ${value.toString()} but was ${self.toString()}", msg)
      }
    } else {
      if (value != self) {
        throwValidationException("expected ${value} but was ${self}", msg)
      }
    }
  }

  static void shouldNotBe(Object self, value, String msg) {
    notEquals(self, value, msg)
  }

  static void shouldNotEqual(Object self, value, String msg) {
    shouldNotBe(self, value, msg)
  }

  static void shouldntBe(Object self, value, String msg) {
    shouldNotBe(self, value, msg)
  }

  static void shouldntEqual(Object self, value, String msg) {
    shouldNotBe(self, value, msg)
  }

  static void shouldNotBe(Object self, value) {
    shouldNotBe(self, value, null)
  }

  static void shouldNotEqual(Object self, value) {
    shouldNotBe(self, value, null)
  }

  static void shouldntBe(Object self, value) {
    shouldNotBe(self, value, null)
  }

  static void shouldntEqual(Object self, value) {
    shouldNotBe(self, value, null)
  }

  private static void notEquals(self, value, String msg) {
    if (self?.getClass() == String.class) {
      if (value.toString().equals(self.toString())) {
        throwValidationException("expected values to differ but both were ${value.toString()}", msg)
      }
    } else {
      if (value == self) {
        throwValidationException("expected values to differ but both were ${value}", msg)
      } else {
        if (self.is(null) && value.is(null)) {
          throwValidationException("expected values to differ but both were null", msg)
        }
      }
    }
  }

  private static void isA(Object self, type) {
    isA(self, type, null)
  }

  private static void isNotA(Object self, type) {
    isNotA(self, type, null)
  }

  private static void isA(Object self, type, String msg) {
    if (!type.equals(self.getClass()) && !type.isInstance(self)) {
      throwValidationException("expected ${type} but was ${self.getClass()}", msg)
    }
  }

  private static void isNotA(Object self, type, String msg) {
    if (type.equals(self.getClass()) || type.isInstance(self)) {
      throwValidationException("expected ${type} but was ${self.getClass()}", msg)
    }
  }

  static void shouldBeA(Object self, value) {
    shouldBeA(self, value, null)
  }

  static void shouldBeA(Object self, value, String msg) {
    isA(self, value, msg)
  }

  static void shouldBeAn(Object self, value) {
    shouldBeA(self, value, null)
  }

  static void shouldBeAn(Object self, value, String msg) {
    shouldBeA(self, value, msg)
  }

  static void shouldNotBeA(Object self, value) {
    shouldNotBeA(self, value, null)
  }

  static void shouldNotBeA(Object self, value, String msg) {
    isNotA(self, value, msg)
  }

  static void shouldNotBeAn(Object self, value) {
    shouldNotBeA(self, value, null)
  }

  static void shouldNotBeAn(Object self, value, String msg) {
    shouldNotBeA(self, value, msg)
  }

  static void shouldHave(Object self, value, String msg) {
    has(self, value, msg)
  }

  static void shouldNotHave(Object self, value, String msg) {
    hasNot(self, value, msg)
  }

  static void shouldHave(Object self, value) {
    shouldHave(self, value, null)
  }

  static void shouldNotHave(Object self, value) {
    shouldNotHave(self, value, null)
  }

  private static void hasNot(Object self, value) {
    hasNot(self, value, null)
  }

  private static void hasNot(Object self, value, String msg) {
    if (self instanceof Map) {
      if (value instanceof Map) {
        handleMapShouldNotContain(self, value, msg)
      } else {
        if (self.containsKey(value) || self.containsValue(value)) {
          throwValidationException("${self.toString()} should not contain ${value.toString()} as a key or value", msg)
        }
      }
    } else {
      if (value instanceof String) {
        if (self instanceof Collection) {
          if (self.contains(value as String)) {
            throwValidationException("${self.toString()} should not contain ${value.toString()}", msg)
          }
        } else if (self.toString().contains(value.toString())) {
          throwValidationException("${self.toString()} should not contain ${value.toString()}", msg)
        }
      } else if (value instanceof Collection) {
        if (self.containsAll(value)) {
          throwValidationException("${self} should not contain ${value}", msg)
        }
      } else if (value instanceof Map) {
        def matchedValues = []
        value.each {ky, vl ->
          def fld = self.getClass().getDeclaredField(ky)
          fld.setAccessible(true)
          def ret = fld.get(self)
          if ((ret.getClass() instanceof String && ret.equals(vl)) || ret == vl) {
            matchedValues << [ky, vl]
          }
        }
        if (matchedValues.size() == value.size()) {
          throwValidationException("${self.getClass().getName()} should not contain ${matchedValues}", msg)
        }
      } else {
        if (self.contains(value)) {
          throwValidationException("${self} contains ${value}", msg)
        }
      }
    }
  }

  private static void has(Object self, value) {
    has(self, value, null)
  }

  private static void has(Object self, value, String msg) {

    if (self instanceof Map) {
      if (value instanceof Map) {
        handleMapContains(self, value, msg)
      } else {
        if (!self.containsKey(value)) {
          if (!self.containsValue(value)) {
            throwValidationException("${self.toString()} doesn't contain ${value.toString()} as a key or a value", msg)
          }
        }
      }
    } else {
      if (value instanceof String) {
        if (!self.toString().contains(value.toString())) {
          throwValidationException("${self.toString()} doesn't contain ${value.toString()}", msg)
        }
      } else if (value instanceof Collection) {
        if (!self.containsAll(value)) {
          throwValidationException("${self} doesn't contain ${value}", msg)
        }
      } else if (value instanceof Map) {
        value.each {ky, vl ->
          def fld = self.getClass().getDeclaredField(ky)
          fld.setAccessible(true)
          def ret = fld.get(self)
          if (ret.getClass() instanceof String) {
            if (!ret.equals(vl)) {
              throwValidationException("${self.getClass().getName()}.${ky} doesn't equal ${vl}", msg)
            }
          } else {
            if (ret != vl) {
              throwValidationException("${self.getClass().getName()}.${ky} doesn't equal ${vl}", msg)
            }
          }
        }
      } else {
        if (!self.contains(value)) {
          throwValidationException("${self} doesn't contain ${value}", msg)
        }
      }
    }
  }

  private static int findItems(delegate, values) {
    def foundcount = 0
    values.each {key, val ->
      delegate.each {vkey, vvalue ->
        if ((vkey.equals(key) && (val == vvalue))) {
          foundcount++
        }
      }
    }
    return foundcount
  }

  private static void handleMapShouldNotContain(delegate, values, String msg) {
    def foundcount = findItems(delegate, values)
    if (foundcount == values.size()) {
      throwValidationException("values ${values} found in ${delegate}", msg)
    }
  }


  private static void handleMapContains(delegate, values, String msg) {
    def foundcount = findItems(delegate, values)
    if (foundcount != values.size()) {
      throwValidationException("values ${values} not found in ${delegate}", msg)
    }
  }
}