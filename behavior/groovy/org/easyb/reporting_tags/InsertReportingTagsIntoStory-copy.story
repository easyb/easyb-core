
extension "jiraReportingPlugin"
 // inserts extra syntax into story

scenario "paying a customer in arrears takes them out of arrears", {
  jira(['id':"CSS-1574", 'description':"This should be in the report"])   // needs the brackets
  given "A Customer", {
    customerId = 5
    jira([id:"some text", description:"some other text"])
  }
  when "We pay an amount", {
    // this should come from an autoloaded syntax extension
    exec {"1"+"1"} 
  }
  then "They aren't in arrears any longer"
}

