package org.disco.easyb.report

import org.disco.easyb.listener.ResultsCollector
import groovy.text.SimpleTemplateEngine

class HtmlReportWriter implements ReportWriter {
  private String location

  HtmlReportWriter(String location) {
    this.location = location
  }

  public void writeReport(ResultsCollector results) {

    writeResourceToDisk("resource${File.separator}thirdparty${File.separator}prototype", "prototype.js")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img01.gif")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img02.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img03.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img04.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img05.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_img06.jpg")
    writeResourceToDisk("resource${File.separator}reports", "easyb_spacer.gif")
    writeResourceToDisk("resource${File.separator}reports", "easyb_report.css")

    InputStream mainTemplateInputStream = this.class.getClassLoader().getResourceAsStream("resource${File.separator}reports${File.separator}easyb_report_main.tmpl");

    def outputReportBufferedWriter = new File(location).newWriter();
    def templateBinding = ["results": results, "reportWriter": outputReportBufferedWriter]
    def templateEngine = new SimpleTemplateEngine()
    def reportTemplate = templateEngine.createTemplate(mainTemplateInputStream.newReader()).make(templateBinding)
    reportTemplate.writeTo(outputReportBufferedWriter)

  }

  private writeResourceToDisk(String resourceLocation, resourceName) {
    InputStream inputStream = this.class.getClassLoader().getResourceAsStream(resourceLocation + File.separator + resourceName);

    if (inputStream != null) {
      FileOutputStream resourceWriter = new FileOutputStream(locationDir + File.separator + resourceName)
      inputStream.eachByte {singleByte ->
        resourceWriter.write(singleByte)
      }
      resourceWriter.close()
    }
  }

  private getLocationDir() {
    if (location.contains("\\")) {
      return location.substring(0, location.lastIndexOf("\\"))
    } else if (location.contains("/")) {
      return location.substring(0, location.lastIndexOf("/"))
    } else {
      return "."
    }
  }

}