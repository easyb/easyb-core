package org.easyb;

int value = 0

before "increment the int value", {
    value++
}

it "should be 1 now", {
    value.shouldBe(1)
}

it "should be 2 now", {
    ensure(value) {isEqualTo2}
}