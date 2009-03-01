package org.easyb

import org.easyb.bdd.Person

scenario "Person object with all properties defined", {

    given "Person object with all values set", {
        person = new Person("Jude", 41)
    }

    then "shouldHave should work with all properties", {
        person.shouldHave firstName: "Jude"
        and
        person.shouldHave age: 41
    }
}