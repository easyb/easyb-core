

shared_stories "stories"

scenario "We should be able to use a shared story", {
  it_behaves_as "my shared story"

  when "we add a number to beforeVal", {
    beforeVal += 50
  }

  // this doesn't follow on, but hey
  then "state should be set", {
    number.shouldBe 60
  }
}

runScenarios()

assert number == 60
assert beforeVal == 70
