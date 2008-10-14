scenario "sub-strings should not match doesNotContain", {
    given "a list of strings, ['a','ab','abc']", {
        l = ['a', 'ab', 'abc']
    }

    then "list should not contain a simple 'c'", {
        l.shouldNotHave('c')
        ensure(l) {
            doesNotContain('c')
        }
    }
}