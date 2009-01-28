using org.disco.bdd.prototype.PrototypeLifeCycleAdapter

before "blah blah", {
  given "do something", {
    foo = 12
  }
}

scenario "do something else" , {
   given "something else", {
     bar = 12
   }
   when "foo and bar are added", {
     foo += bar
   }

   then "the answer should be 24", {
     foo.shouldBe 24
   }

   and "there should be a magic variable called _foo_", {
    _foo_.shouldBe "test"
  }
}