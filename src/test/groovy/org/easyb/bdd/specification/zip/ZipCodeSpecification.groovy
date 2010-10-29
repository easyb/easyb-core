package org.easyb.bdd.specification.zip

import org.easyb.bdd.zip.ZipCodeValidator

before "initialize zipcodevalidator instance", {
    zipvalidate = new ZipCodeValidator();
}

it "should deny invalid zip codes", {
    ["221o1", "2210", "22010-121o"].each {zip ->
        zipvalidate.validate(zip).is false
    }
}

it "should accept valid zip codes", {
    ["22101", "22100", "22010-1210"].each {zip ->
        zipvalidate.validate(zip).shouldBe true
    }
}