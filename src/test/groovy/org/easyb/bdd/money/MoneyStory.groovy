package org.easyb.bdd.money

description "This story is about currency management"

narrative "this string is required for now", {
    as_a "person who uses money"
    i_want "to be able to add them together"
    so_that "that I can become rich (and wierd)"
}

scenario "two moneys of the same currency are added", {

    given "one money object is added to another", {
        total = new Money(12, "CHF").add(new Money(14, "CHF"))
    }

    then "the total amount should be the sum of the two", {
        total.amount().shouldBe 26
    }

}

scenario "two moneys of different currencies are added", {

}