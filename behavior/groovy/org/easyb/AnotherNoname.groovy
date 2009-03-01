package org.easyb



given "a valid string with 6 characters", {
    var = "string"
}

when "length is called", {
    len = var.length()
}

then "the value returned should be 6", {
    len.is(6)
}