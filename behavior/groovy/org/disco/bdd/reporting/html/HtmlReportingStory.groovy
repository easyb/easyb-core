import org.disco.easyb.domain.Story
import org.disco.easyb.domain.BehaviorFactory
import org.disco.easyb.listener.ResultsCollector
import org.disco.easyb.listener.BroadcastListener
import org.disco.easyb.report.HtmlReportWriter

scenario "a single pending scenario", {

  given "a story file with a single pending scenario is loaded", {
    storyBehavior = BehaviorFactory.createBehavior(new File('./behavior/groovy/org/disco/bdd/reporting/html/SinglePendingStory.groovy'))
  }

  and
  given "the collectors are initialized", {
    broadcastListener = new BroadcastListener();
    resultsCollector = new ResultsCollector();
    broadcastListener.registerListener(resultsCollector);
  }

  when "the story is executed", {
    storyBehavior.execute(broadcastListener)
  }

  and
  when "the html reports are written", {
    htmlReportLocation = "./target/SinglePendingScenario-report.html"
    new HtmlReportWriter(htmlReportLocation).writeReport(resultsCollector)
  }

  then "the resulting html should have a behavior summary with only a pending scenario" , {
    xmlReport = new XmlSlurper().parse(new File((String)htmlReportLocation))
    contentDiv = xmlReport.body.div.find { it['@id'] == 'page' }.div.find { it['@id'] == 'content'}
    summariesDiv = contentDiv.div.find { it['@class'] == 'post' }.div.find { it['@class'] == 'entry' }.div.find { it['@id'] == 'Summaries' }

    behaviorsSummaryRow = summariesDiv.table[0].tbody.tr
    behaviorsSummaryRow.td[0].text().shouldBe '1'
    behaviorsSummaryRow.td[1].text().shouldBe '0'
    behaviorsSummaryRow.td[2].text().shouldBe '1'
    behaviorsSummaryRow.td[2].@class.shouldBe 'stepResultPending'
  }

  and
  then "should have a stories summary with only a pending scenario", {
    storiesSummaryRow = summariesDiv.table[1].tbody.tr
    storiesSummaryRow.td[0].text().shouldBe '1'
    storiesSummaryRow.td[1].text().shouldBe '1'
    storiesSummaryRow.td[2].text().shouldBe '0'
    storiesSummaryRow.td[2].@class.shouldBe ''
    storiesSummaryRow.td[3].text().shouldBe '1'
    storiesSummaryRow.td[3].@class.shouldBe 'stepResultPending'
  }

  and
  then "should have a specifications summary with no results", {
    specificationsSummaryRow = summariesDiv.table[2].tbody.tr
    specificationsSummaryRow.td[0].text().shouldBe '0'
    specificationsSummaryRow.td[1].text().shouldBe '0'
    specificationsSummaryRow.td[1].@class.shouldBe ''
    specificationsSummaryRow.td[2].text().shouldBe '0'
    specificationsSummaryRow.td[2].@class.shouldBe ''

  }

  and
  then "should have a single scenario named single pending", {
    storiesListDiv = contentDiv.div.find { it['@class'] == 'post' }.div.find { it['@class'] == 'entry' }.div.find { it['@id'] == 'StoriesList' }

    storyRow = storiesListDiv.table.tbody.tr[0]
    storyRow.td[0].a.text().shouldBe 'single pending'
    storyRow.td[1].text().shouldBe '1'
    storyRow.td[2].text().shouldBe '0'
    storyRow.td[2].@class.shouldBe ''
    storyRow.td[3].text().shouldBe '1'
    storyRow.td[3].@class.shouldBe 'stepResultPending'

    scenariosRow = storiesListDiv.table.tbody.tr[1]
    scenarioRow = scenariosRow.td.table.tbody.tr
    scenarioRow.td[0].text().shouldBe 'pending scenario'
    scenarioRow.td[0].@title.shouldBe 'Scenario'
    scenarioRow.td[1].text().shouldBe 'pending'
    scenarioRow.td[1].@title.shouldBe 'Result'
    scenarioRow.td[1].@class.shouldBe 'stepResultPending'

  }

  and
  then "should have a plain story printed out with scenario named pending scenario", {
    storiesListPlainDiv = contentDiv.div.find { it['@class'] == 'post' }.div.find { it['@class'] == 'entry' }.div.find { it['@id'] == 'StoriesListPlain' }

    storiesListPlainDiv.div[0].text().shouldBe "1 scenario (including 1 pending) successfully"
    storiesListPlainDiv.div[1].text().contains "&nbsp;&nbsp;Story: single pending"
    storiesListPlainDiv.div[1].text().contains "&nbsp;&nbsp;&nbsp;&nbsp;scenario pending scenario"
  }
}
