package org.disco.easyb

import org.codehaus.groovy.runtime.NullObject
import org.disco.easyb.exception.VerificationException

class BehaviorCategory {

    static void shouldBeGreaterThan(Object self, value) {
        shouldBeGreaterThan(self, value, null)
    }

    static void shouldBeGreaterThan(Object self, value, String msg) {
        if (value > self) {
            def out = "${value} was greater than ${self}"
            throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))

        } else if (value == self) {
            def out = "${value} was equal to ${self}"
            throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
        }
    }

    static void shouldBeLessThan(Object self, value, String msg) {
        if ((self.getClass() == NullObject.class) || (value == null)) {
            def out = "${value} can not be compared to ${self}"
            throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))

        } else if (value < self) {
            def out = "${value} was less than ${self}"
            throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))

        } else if (value == self) {
            def out = "${value} was equal to ${self}"
            throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
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
                def out = "expected ${value.toString()} but target object is null"
                throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
            }
        } else if (value.getClass() == String.class) {
            if (!value.toString().equals(self.toString())) {
                def out = "expected ${value.toString()} but was ${self.toString()}"
                throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
            }
        } else {
            if (value != self) {
                def out = "expected ${value} but was ${self}"
                throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
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
                def out = "expected values to differ but both were ${value.toString()}"
                throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
            }
        } else {
            if (value == self) {
                def out = "expected values to differ but both were ${value}"
                throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
            } else {
                if (self.is(null) && value.is(null)) {
                    def out = "expected values to differ but both were null"
                    throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))

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
        if (!type.equals(self.getClass())) {
            def out = "expected ${type} but was ${self.getClass()}"
            throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
        }
    }

    private static void isNotA(Object self, type, String msg) {
        if (type.equals(self.getClass())) {
            def out = "expected ${type} but was ${self.getClass()}"
            throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))
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

//    def out = "expected ${value.toString()} but target object is null"
//    throw new VerificationException((msg != null ? "\"" + msg + "\", " + out : out))



    static void shouldHave(Object self, value) {
        has(self, value)
    }

    static void shouldNotHave(Object self, value) {
        hasNot(self, value)
    }

    private static void hasNot(Object self, value) {
        if (self instanceof Map) {
            if (value instanceof Map) {
                handleMapShouldNotContain(self, value)
            } else {
                if (self.containsKey(value) || self.containsValue(value)) {
                    throw new VerificationException("${self.toString()} should not contain ${value.toString()} as a key or value")
                }
            }
        } else {
            if (value instanceof String) {
                if (self instanceof Collection) {
                    if (self.contains(value as String)) {
                        throw new VerificationException("${self.toString()} should not contain ${value.toString()}")
                    }
                } else if (self.toString().contains(value.toString())) {
                    throw new VerificationException("${self.toString()} should not contain ${value.toString()}")
                }
            } else if (value instanceof Collection) {
                if (self.containsAll(value)) {
                    throw new VerificationException("${self} should not contain ${value}")
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
                    throw new VerificationException("${self.getClass().getName()} should not contain ${matchedValues}")
                }
            } else {
                if (self.contains(value)) {
                    throw new VerificationException("${self} contains ${value}")
                }
            }
        }
    }

    private static void has(Object self, value) {

        if (self instanceof Map) {
            if (value instanceof Map) {
                handleMapContains(self, value)

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

    private static void handleMapShouldNotContain(delegate, values) {
        def foundcount = findItems(delegate, values)
        if (foundcount == values.size()) {
            throw new VerificationException("values ${values} found in ${delegate}")
        }
    }


    private static void handleMapContains(delegate, values) {
        def foundcount = findItems(delegate, values)
        if (foundcount != values.size()) {
            throw new VerificationException("values ${values} not found in ${delegate}")
        }
    }
}