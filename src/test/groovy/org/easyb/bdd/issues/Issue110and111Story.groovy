scenario "ensure shouldStartWith works like startsWith", {
  given "a string", {
    var = "hello world"
  }
    
  then "should start with hello", {
    var.shouldStartWith "hello"
  }
}

scenario "ensure shouldEndWith works like endsWith", {
  given "a string", {
    var = "hello world"
  }
    
  then "should end with word", {
    var.shouldEndWith "world"
  }
}