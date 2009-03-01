package org.bdd.issues

scenario "all subparts have no closures defined", {

    given "no closure"
    when "easyb is invoked with this story"
    then "it should accept this as an unimplemented closure"
    and
    then "easyb should not count this as a successful scenario, but a pending one"

}