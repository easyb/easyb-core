package org.easyb.where
/*
Example tests a map at the story level
 */

ignore all

scenario "This scenario should not be run", {
  then "It should blow up if it is run", {
  }
  where "Number examples with '#text' and #number", { throw new org.easyb.exception.VerificationException("shouldn't eval closure")}
}

