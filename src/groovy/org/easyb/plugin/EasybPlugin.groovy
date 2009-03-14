package org.easyb.plugin

interface EasybPlugin {
    String getName()
    def beforeScenario(Binding binding)
    def afterScenario(Binding binding)

    def beforeGiven(Binding binding)
    def afterGiven(Binding binding)

    def beforeWhen(Binding binding)
    def afterWhen(Binding binding)

    def beforeThen(Binding binding)
    def afterThen(Binding binding)

    def beforeStory(Binding binding)
    def afterStory(Binding binding)

    void setClassLoader(ClassLoader classLoader)
}
