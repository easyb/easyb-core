package org.disco.bdd.issues

import org.disco.easyb.core.listener.DefaultListener
import org.disco.easyb.core.result.Result
import org.disco.easyb.core.report.TxtStoryReportWriter
import org.disco.easyb.core.report.Report
import org.disco.easyb.core.util.ReportFormat
import org.disco.easyb.core.util.ReportType
import org.disco.easyb.BehaviorBinding

scenario "text story format should have newline prior to story or scenario part", {
  // TODO reimplement issue 21 once the report formatting has been reimplemented
  //  given "a listener populated with some stories and scenarios", {
  //    listener = new DefaultListener()
  //    listener.gotResult(new Result("story description", BehaviorBinding.STORY, Result.SUCCEEDED));
  //    listener.gotResult(new Result("scenario description", BehaviorBinding.STORY_SCENARIO, Result.SUCCEEDED))
  //    listener.gotResult(new Result("given description", BehaviorBinding.STORY_GIVEN, Result.SUCCEEDED));
  //    listener.gotResult(new Result("when description", BehaviorBinding.STORY_WHEN, Result.SUCCEEDED));
  //    listener.gotResult(new Result("then description", BehaviorBinding.STORY_THEN, Result.SUCCEEDED));
  //
  //    listener.gotResult(new Result("scenario description two", BehaviorBinding.STORY_SCENARIO, Result.SUCCEEDED))
  //    listener.gotResult(new Result("given description two", BehaviorBinding.STORY_GIVEN, Result.SUCCEEDED));
  //    listener.gotResult(new Result("when description two", BehaviorBinding.STORY_WHEN, Result.SUCCEEDED));
  //    listener.gotResult(new Result("then description two", BehaviorBinding.STORY_THEN, Result.SUCCEEDED));
  //
  //  }
  //
  //  when "report is written to disk", {
  //    Report txtStoryReport = new Report();
  //    txtStoryReport.setFormat(ReportFormat.TXT.format());
  //    txtStoryReport.setLocation("target/story-issue21-report.txt");
  //    txtStoryReport.setType(ReportType.STORY.type());
  //
  //    reportWriter = new TxtStoryReportWriter(txtStoryReport, listener);
  //
  //    reportWriter.writeReport()
  //  }
  //
  //  then "story and scenario parts should be preceded by a newline", {
  //
  //    new File("target/story-issue21-report.txt").eachLine { line ->
  //      if(line.contains("${' '.padRight(2)}Story:") || line.contains("${' '.padRight(4)}scenario")) {
  //        previousLine.size().shouldBe 0
  //      }
  //      previousLine = line
  //    }
  //  }
}
