import org.easyb.exception.VerificationException

description "this issue relates to improper reporting of pending scenarios"

int initcount = easybResults().getPendingScenarioCount()

scenario "this is technically a pending scenario", {

}

scenario "this scenario is not pending", {
    given "that someone defined a scenario with an empty closure"
    then "easyb should consider that a pending one"
}

runScenarios()

if (easybResults().getPendingScenarioCount() > (initcount + 2)) {
    throw new VerificationException('Pending scenarios count not correct')
}