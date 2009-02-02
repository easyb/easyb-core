import org.disco.easyb.domain.BehaviorFactory
import org.disco.easyb.listener.ResultsCollector
import org.disco.easyb.listener.BroadcastListener
import org.disco.easyb.report.TxtStoryReportWriter

before_each "initialize the collectors", {
  and
  given "the collectors are initialized", {
    broadcastListener = new BroadcastListener();
    resultsCollector = new ResultsCollector();
    broadcastListener.registerListener(resultsCollector);
  }
}

scenario "nested scenarios", {
  given "a story file with nested scenarios", {
    storyBehavior = BehaviorFactory.createBehavior(new File('./behavior/groovy/org/disco/bdd/reporting/txt/NestedScenarios.story'))
  }

  when "the specification is executed", {
    storyBehavior.execute(broadcastListener)
  }

  and
  when "the reports are written", {
    txtReportLocation = "./target/reports/NestedScenarios-report.txt"
    new TxtStoryReportWriter(txtReportLocation).writeReport(resultsCollector)
  }
  
  then "the resulting txt should include 2 scenarios", {
    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(txtReportLocation))))
    int scenariosCount = 0
    
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      if (line.matches(".*(parent|child).*")) {
        scenariosCount ++
      }
    }
    
    scenariosCount.shouldBe 2
  }
}