package org.easyb.domain

import org.easyb.domain.Story

before "init a null story", {
  given "a story obj", {
    stry = new Story(null, null, null)
  }
}


scenario "categories with one word", {
  given "a category name like santiy", {
    cat = "sanity"
  }
  when "a story containing this name is processed", {
    sstory = """
category "sanity"

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
    answer = stry.isMemberOfCategory(sstory, [cat] as String[])
    answer.shouldBe true
  }
}


scenario "category list", {
  given "a category list", {
    cat = ["sanity", "bar", "baz"]
  }
  when "a story containing this name is processed", {
    bstory = """
category "baz"

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
    answer = stry.isMemberOfCategory(bstory, cat as String[])
    answer.shouldBe true
  }
}



scenario "category list not true", {
  given "a category list", {
    cat = ["sanity", "bar", "baz"]
  }
  when "a story not containing the name is not processed", {
    bstory = """
category "bazzy"

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
    answer = stry.isMemberOfCategory(bstory, cat as String[])
    answer.shouldBe false
  }
}



scenario "category list with one true one false", {
  given "a category list", {
    cat = ["sanity", "bar", "baz"]
  }
  when "a story containing a name is processed", {
    bbstory = """
category ["bazzy","bar"]

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
    answer = stry.isMemberOfCategory(bbstory, cat as String[])
    answer.shouldBe true
  }
}