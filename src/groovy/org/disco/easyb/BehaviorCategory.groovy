package org.disco.easyb

import org.disco.easyb.exception.VerificationException
import org.codehaus.groovy.runtime.NullObject

class BehaviorCategory {

  static void shouldBeEqual(Object self, value) {
    _equals(self, value)
  }

  static void shouldBeEqualTo(Object self, value) {
    _equals(self, value)
  }

  static void shouldBe(Object self, value) {
    _equals(self, value)
  }

  static void shouldEqual(Object self, value) {
    _equals(self, value)
  }

  private static void _equals(self, value) {
    if (self.getClass() == NullObject.class) {
      if (value != null) {
        throw new VerificationException("expected ${value.toString()} but target object is null")
      }
    } else if (value.getClass() == String.class) {
      if (!value.toString().equals(self.toString())) {
        throw new VerificationException("expected ${value.toString()} but was ${self.toString()}")
      }
    } else {
      if (value != self) {
        throw new VerificationException("expected ${value} but was ${self}")
      }
    }
  }

  static void shouldNotBe(Object self, value) {
    _notEquals(self, value)
  }

  static void shouldNotEqual(Object self, value) {
    _notEquals(self, value)
  }

  static void shouldntBe(Object self, value) {
    _notEquals(self, value)
  }

  static void shouldntEqual(Object self, value) {
    _notEquals(self, value)
  }

  private static void _notEquals(self, value) {
    if (self.getClass() == String.class) {
      if (value.toString().equals(self.toString())) {
        throw new VerificationException("expected values to differ but both were ${value.toString()}")
      }
    } else {
      if (value == self) {
        throw new VerificationException("expected values to differ but both were ${value}")
      }
    }
  }

  private static void _isA(Object self, type) {
    if (!type.equals(self.getClass())) {
      throw new VerificationException("expected ${type} but was ${self.getClass()}")
    }
  }

  private static void _isNotA(Object self, type) {
    if (type.equals(self.getClass())) {
      throw new VerificationException("expected ${type} but was ${self.getClass()}")
    }
  }

  static void shouldBeA(Object self, value) {
    _isA(self, value)
  }

  static void shouldBeAn(Object self, value) {
    _isA(self, value)
  }

  static void shouldNotBeA(Object self, value) {
    _isNotA(self, value)
  }

  static void shouldNotBeAn(Object self, value) {
    _isNotA(self, value)
  }

  static void shouldHave(Object self, value) {
    _has(self, value)
  }

  static void shouldNotHave(Object self, value) {
    _hasNot(self, value)
  }

  private static void _hasNot(Object self, value) {
    if(self instanceof Map) {
      if (value instanceof Map) {
        _handleMapShouldNotContain(self,value)
      } else {
        if(self.containsKey(value) || self.containsValue(value)) {
          throw new VerificationException("${self.toString()} contains ${value.toString()} as a key or value")
        }
      }
    } // TODO self isn't a map
  }

  private static void _has(Object self, value) {

    if (self instanceof Map) {
      if (value instanceof Map) {
        _handleMapContains(self, value)

      } else {
        if (!self.containsKey(value)) {
          if (!self.containsValue(value)) {
            throw new VerificationException("${self.toString()} doesn't contain ${value.toString()} as a key or a value")
          }
        }
      }
    } else {
      if (value instanceof String) {
        if (!self.toString().contains(value.toString())) {
          throw new VerificationException("${self.toString()} doesn't contain ${value.toString()}")
        }
      } else if (value instanceof Collection) {
        if (!self.containsAll(value)) {
          throw new VerificationException("${self} doesn't contain ${value}")
        }
      } else if (value instanceof Map) {
        value.each {ky, vl ->
          def fld = self.getClass().getDeclaredField(ky)
          fld.setAccessible(true)
          def ret = fld.get(self)
          if (ret.getClass() instanceof String) {
            if (!ret.equals(vl)) {
              throw new VerificationException("${self.getClass().getName()}.${ky} doesn't equal ${vl}")
            }
          } else {
            if (ret != vl) {
              throw new VerificationException("${self.getClass().getName()}.${ky} doesn't equal ${vl}")
            }
          }
        }
      } else {
        if (!self.contains(value)) {
          throw new VerificationException("${self} doesn't contain ${value}")
        }
      }
    }
  }


  
  private static int findItems(delegate, values){
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

  private static void _handleMapShouldNotContain(delegate, values) {
	def foundcount = findItems(delegate, values)
	    if (foundcount == values.size()) {
	      throw new VerificationException("values ${values} found in ${delegate}")
	}
   }

  
  private static void _handleMapContains(delegate, values) {
	def foundcount = findItems(delegate, values)
    if (foundcount != values.size()) {
      throw new VerificationException("values ${values} not found in ${delegate}")
    }
  }

}