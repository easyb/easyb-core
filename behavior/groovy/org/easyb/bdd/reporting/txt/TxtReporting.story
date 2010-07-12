import org.easyb.domain.BehaviorFactory
import org.easyb.listener.ResultsCollector
import org.easyb.listener.BroadcastListener
import org.easyb.report.TxtStoryReportWriter
import org.easyb.listener.ResultsAmalgamator

before_each "initialize the collectors", {
  and
  given "the collectors are initialized", {
   // broadcastListener = new BroadcastListener();
  //  resultsCollector = new ResultsCollector();
//    broadcastListener.registerListener(resultsCollector);
  }
}

scenario "nested scenarios", {
  given "a story file with nested scenarios", {
    storyBehavior = BehaviorFactory.createBehavior(new File('./behavior/groovy/org/easyb/bdd/reporting/txt/NestedScenarios.story'))
  }

  when "the specification is executed", {
    storyBehavior.execute()
  }

  and
  when "the reports are written", {
    txtReportLocation = "./target/reports/NestedScenarios-report.txt"
    new TxtStoryReportWriter(txtReportLocation).writeReport(new ResultsAmalgamator(storyBehavior))
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