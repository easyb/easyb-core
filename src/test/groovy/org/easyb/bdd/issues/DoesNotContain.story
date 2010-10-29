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


scenario "sub-strings should match doesContain", {
    given "a list of strings, ['a','ab','abc']", {
        l = ['a', 'ab', 'abc']
    }

    then "list should contain a simple 'a'", {
        l.shouldHave('a')
        ensure(l) {
            contains('a')
        }
    }
}