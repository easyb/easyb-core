package org.disco.easyb.delegates

import org.disco.easyb.exception.VerificationException

/**
 * this class must NOT implement the invokeMethod!!
 */
class RichEnsureDelegate {

  def verified
  /**
  *
  */
  def void and() {
    //println "noop!! with and"
    //noop
  }
  /**
   * This method isn't currently working
   */
  def is(value) {
    // println "is invoked in FlexDelegate"
  }
  /**
   * 
   */
  def void contains(Object value) {
    //println "in rich contains"
    if (this.verified instanceof Map) {
      if (value instanceof Map) {
        this.handleMapContains(value)
      } else {
        if (!this.verified.containsKey(value)) {
          if (!this.verified.containsValue(value)) {
            throw new VerificationException("${verified.toString()} doesn't contain ${value.toString()} as a key or a value")
          }
        }
      }
    } else {
      if (value instanceof String) {
        if (!verified.toString().contains(value.toString())) {
          throw new VerificationException("${verified.toString()} doesn't contain ${value.toString()}")
        }
      } else if (value instanceof Collection) {
        if (!verified.containsAll(value)) {
          throw new VerificationException("${verified} doesn't contain ${value}")
        }
      } else if (value instanceof Map) {
        value.each {ky, vl ->
          def fld = this.verified.getClass().getDeclaredField(ky)
          fld.setAccessible(true)
          def ret = fld.get(this.verified)
          if (ret.getClass() instanceof String) {
            if (!ret.equals(vl)) {
              throw new VerificationException("${verified.getClass().getName()}.${ky} doesn't equal ${vl}")
            }
          } else {
            if (ret != vl) {
              throw new VerificationException("${verified.getClass().getName()}.${ky} doesn't equal ${vl}")
            }
          }
        }
      } else {
        if (!verified.contains(value)) {
          throw new VerificationException("${verified} doesn't contain ${value}")
        }
      }
    }
  }
  /**
   *
   */
  private void handleMapContains(values) {
    def foundcount = 0
    values.each {key, val ->
      this.verified.each {vkey, vvalue ->
        if ((vkey.equals(key) && (val == vvalue))) {
          foundcount++
        }
      }
    }
    if (foundcount != values.size()) {
      throw new VerificationException("values ${values} not found in ${verified}")
    }
  }
  /**
   **/

  def void isEqualTo(Object value) {
    if (value.getClass() == String.class) {
      if (!value.toString().equals(verified.toString())) {
        throw new VerificationException("expected ${value.toString()} but was ${verified.toString()}")
      }
    } else {
      if (value != verified) {
        throw new VerificationException("expected ${value} but was ${verified}")
      }
    }
  }
  /**
   **/

  def void isNotEqualTo(Object value) {
    if (value.getClass() == String.class) {
      if (value.toString().equals(verified.toString())) {
        throw new VerificationException("expected values to differ but both were ${value.toString()}")
      }
    } else {
      if (value == verified) {
        throw new VerificationException("expected values to differ but both were ${value}")
      }
    }
  }
  /**
   * 
   */
  def void isA(Object type) {
    String val = this.getUnqualifiedClassName()
    if (!type.toString().equals(val)) {
      throw new VerificationException("expected ${type} but was ${val}")
    }
  }
  /**
   * 
   */
  def void isNotA(Object type) {
    String val = this.getUnqualifiedClassName()
    if (type.toString().equals(val)) {
      throw new VerificationException("expected not to be ${type} but was")
    }
  }
  /**
   *
   */
  private String getUnqualifiedClassName() {
    String verClassName = verified.getClass().getName()
    return verClassName.substring((verClassName.lastIndexOf(".")) + 1, verClassName.length())
  }
  /**
   * 
   */
  def void isNotNull() {
    //println "in rich ensure isNotNull"
    if (verified == null) {
      throw new VerificationException("value is null")
    }
  }
  /**
   * 
   */
  def void isNull() {
    // println "in rich ensure isNull"
    if (verified != null) {
      throw new VerificationException("value isn't null")
    }
  }
  /**
   * 
   */
  def void startsWith(Object value) {
    if (value instanceof String) {
      if (!verified.toString().startsWith(value.toString())) {
        throw new VerificationException("${verified.toString()} doesn't start with ${value.toString()}")
      }
    } else {
      throw new VerificationException("startsWith not supported on non String objects")
    }
  }
  /**
   * 
   */
  def void endsWith(Object value) {
    if (value instanceof String) {
      if (!verified.toString().endsWith(value.toString())) {
        throw new VerificationException("${verified.toString()} doesn't end with ${value.toString()}")
      }
    } else {
      throw new VerificationException("endsWith not supported on non String objects")
    }
  }
  /**
   * 
   */
  def void isContainedIn(Object value) {
    if (!value.contains(verified)) {
      throw new VerificationException("${value} doesn't contain ${verified}")
    }
  }
}