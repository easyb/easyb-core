import groovy.util.FileNameFinder

scenario "using compiled Groovy code should work as excepted", {
    given "a compiled Groovy object-- binary form", {
        filefnder = new FileNameFinder()
    }
    when "it is used", {
        names = filefnder.getFileNames("./docs/ivy/", "*.xsl")
    }
    then "easyb should work just fine", {
        names.size.shouldBe 1
        names[0].contains("/ivy-doc.xsl").shouldBe true
    }
}