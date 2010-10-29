package org.easyb.bdd.story.zip

import org.easyb.bdd.zip.ZipCodeValidator

given "an invalid zip code", {
    invalidzipcode = "221o1"
}

and

given "the zipcodevalidator is initialized", {
    zipvalidate = new ZipCodeValidator();
}

when "validate is invoked with the invalid zip code", {
    value = zipvalidate.validate(invalidzipcode)
}

then "the validator instance should return false", {
    value.shouldBe(false)
}