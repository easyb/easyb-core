description """
Ideally, easyb should report scenarios/stories that are pending; thus,
the following three scenarios would yield a report that contains
3 pending scenarios. For example, the output of the console should be
something like:

    Running issue with pending counts story (IssueWithPendingCounts.story)
    Scenarios run: 3, Failures: 0, Pending: 3, Time Elapsed: 0.402 sec

    3 total behaviors run with no failures

And the text report would have a [pending] flag next to each scenario or
then clause
"""

scenario "this is a pending scenario", {
    given "something"
    when "something is done"
    then "there is a way to handle it"
}


scenario "this is another pending scenario", {
    given "something else"
    when "something else is done"
    then "there is also a way to handle it"
}

scenario "this is finally a pending scenario with nothing in it"