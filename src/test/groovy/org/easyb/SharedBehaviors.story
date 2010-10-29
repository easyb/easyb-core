shared_behavior "shared behaviors", {
  given "a string", {
    var = ""
  }
  
  when "the string is hello world", {
    var = "hello world"
  }
}

scenario "first scenario", {
  it_behaves_as "shared behaviors"
  
  then "the string should start with hello", {
    var.shouldStartWith "hello"
  }
}

scenario "second scenario", {
  it_behaves_as "shared behaviors"
  
  then "the string should end with world", {
    var.shouldEndWith "world"
  }
}