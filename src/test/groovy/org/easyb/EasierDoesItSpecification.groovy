package org.easyb;


it "should support expressions in the ensure clause", {
    ensure(1 == 1)
}

it "should support other types of expressions in the ensure clause", {
    ensure("this".equals("this"))
}

it "should support more than one expression in the ensure clause", {
    ensure("this".equals("this") && "this" instanceof String)
}

it "should support negated expressions in the ensure clause", {
    ensure(!"this".equals("that"))
}