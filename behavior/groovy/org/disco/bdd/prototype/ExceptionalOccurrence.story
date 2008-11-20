

//ignore all

scenario "this generates an exception", {
    given "a null object", {
        foo = null
    }
    then "doing something with it should blow things up", {
        foo.blowmeup()
    }
}