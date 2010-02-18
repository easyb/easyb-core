package org.easyb.domain

import org.easyb.domain.Story

before "init a null story", {
  given "a story obj", {
    stry = new Story(null, null, null)
  }
}


scenario "tag with one word", {
  given "a tag name like santiy", {
    cat = "sanity"
  }
  when "a story containing this name is processed", {
    sstory = """
tag "sanity"

scenario "User A wants to work with a lab mdta, user b wants to use an prod mdta",{
	given "blah",{

	}

	when "blah", {

	}

	then "blah", {

	}
}

"""
  }
  then "it should be run", {
    answer = stry.containsTag(sstory, [cat] as String[])
    answer.shouldBe true
  }
}


scenario "tag list", {
  given "a tag list", {
    cat = ["sanity", "bar", "baz"]
  }
  when "a story containing this name is processed", {
    bstory = """
tag "baz"

scenario "User A wants to work with a lab mdta, user b wants to use an prod mdta",{
	given "blah",{

	}

	when "blah", {

	}

	then "blah", {

	}
}

"""
  }
  then "it should be run", {
    answer = stry.containsTag(bstory, cat as String[])
    answer.shouldBe true
  }
}



scenario "tag list not true", {
  given "a tag list", {
    cat = ["sanity", "bar", "baz"]
  }
  when "a story not containing the name is not processed", {
    bstory = """
tag "bazzy"

scenario "User A wants to work with a lab mdta, user b wants to use an prod mdta",{
	given "blah",{

	}

	when "blah", {

	}

	then "blah", {

	}
}

"""
  }
  then "it should be run", {
    answer = stry.containsTag(bstory, cat as String[])
    answer.shouldBe false
  }
}



scenario "tag list with one true one false", {
  given "a tag list", {
    cat = ["sanity", "bar", "baz"]
  }
  when "a story containing a name is processed", {
    bbstory = """
tag ["bazzy","bar"]

scenario "User A wants to work with a lab mdta, user b wants to use an prod mdta",{
	given "blah",{

	}

	when "blah", {

	}

	then "blah", {

	}
}

"""
  }
  then "it should be run", {
    answer = stry.containsTag(bbstory, cat as String[])
    answer.shouldBe true
  }
}