package org.disco.easyb.delegates

import org.disco.easyb.delegates.RichlyEnsurable

/**
 * this class's job is to determine which 
 * method to invoke in the FlexDelegate as invokeMethod
 * can't be overridden in that class if one wants the
 * flexibity to limit ()'s 
 *
 */
class RichEnsureProxyDelegate implements RichlyEnsurable {

  private RichEnsureDelegate flex
  /**
   *
   */
  public RichEnsureProxyDelegate() {
    this.flex = new RichEnsureDelegate()
  }
  /**
   *
   */
  public void setVerified(Object verified) {
    this.flex.verified = verified
  }
  /**
   *
   */
  def invokeMethod(String method, Object params) {

    def istest = method.getAt(0..1)

    if (istest.equals("is")) {
      this.handleIsMethod(method, params)
    } else if (method.equals("and")) {
      this.flex.and()
    } else if (method.equals("contains") || method.equals("has")) {
      this.flex.contains(params[0])
    } else if (method.equals("startsWith")) {
      this.flex.startsWith(params[0])
    } else if (method.equals("endsWith")) {
      this.flex.endsWith(params[0])
    } else {
      throw new RuntimeException("No further routes available in invokeMethod, method name was ${method}")
    }
  }
  /**
   * this method provides some level of isolation for refactoring
   *
   */
  def handleIsMethod(method, params) {
    def isamatcher = method =~ 'is(An|A)(.*)'
    def isnotamatcher = method =~ 'isNot(An|A)(.*)'
    def eqmatcher = method =~ 'isEqualTo(.*)'
    def noteqmatcher = method =~ 'isNotEqualTo(.*)'
    def booleanmatcher = method =~ 'is(True|False)'
    def nullmatcher = method =~ 'is(NotNull|Null)'
    def iscontainedmatcher = method =~ 'isContainedIn'

    if (isamatcher.matches()) {
      this.flex.isA(isamatcher.group(2))
    } else if (isnotamatcher.matches()) {
      this.flex.isNotA(isnotamatcher.group(2))
    } else if (eqmatcher.matches()) {
      if (eqmatcher.group(1).equals("")) {
        this.flex.isEqualTo(params[0])
      } else {
        this.flex.isEqualTo(eqmatcher.group(1))
      }
    } else if (noteqmatcher.matches()) {
      if (noteqmatcher.group(1).equals("")) {
        this.flex.isNotEqualTo(params[0])
      } else {
        this.flex.isNotEqualTo(noteqmatcher.group(1))
      }
    } else if (booleanmatcher.matches()) {
      this.flex.isEqualTo(booleanmatcher.group(1).toLowerCase())
    } else if (method.equals("isNotNull")) {
      this.flex.isNotNull()
    } else if (method.equals("isNull")) {
      this.flex.isNull()
    } else if (method.equals("isContainedIn")) {
      this.flex.isContainedIn(params[0])
    } else {
      throw new RuntimeException("No further routes available in invokeMethod")
    }
  }
  /**
   *
   */
  def getProperty(String property) {
    def matcher = property =~ 'is(NotNull|Null)\\(?\\)?'
    if (matcher.matches()) {
      this.flex.invokeMethod(property, null)
    } else {
      //shunt it back to the invokeMethod magic in this class
      this.invokeMethod(property, null)
    }
  }
}