package org.bdd.issues //package needed for eclipse?

given "a file ending in .story", {
    value = 12 + 5
}

then "easyb should excute is as normal", {
    value.shouldBe 17
}