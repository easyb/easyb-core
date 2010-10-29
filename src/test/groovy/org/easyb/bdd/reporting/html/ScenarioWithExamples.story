package org.easyb.bdd.reporting.html


examples "names and favorite colors", {
  name  =              ["jack", "jill", "jane"]
  favoriteColor  =     ["red", null, "blue"]
}

scenario "Favorite colors", {
    given "a person called #name and an unknown field called #unknownField", {
	}
    when "the application looks up the favorite color of #name", {
    }    
    then "the favorite color of #name should be #favoriteColor", {
    }
}