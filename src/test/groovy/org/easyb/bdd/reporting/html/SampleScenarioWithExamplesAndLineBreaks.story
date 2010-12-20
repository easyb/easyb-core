package org.easyb.bdd.reporting.html


examples "names and favorite colors", {
  name  =              ["jack", "jill", "jane"]
  age  =               [20, 30, 40]
  favoriteColor  =     ["red", null, "blue"]
}

scenario "Formatted favorite colors", {
	given """a Person the following values:
Name:	#name 
Age:	#age
Favorite Color:	#favoriteColor""", {
	}
	when "the application looks up the favorite color of #name", {
	}
	then "the favorite color of #name should be #favoriteColor", {
	}
}