import org.easyb.listener.BroadcastListener
import org.easyb.listener.FailureDetector
import org.easyb.result.Result

it "should notify all listeners of events", {
    try {
        def recipients = [new FailureDetector(), new FailureDetector()]
        BroadcastListener listener = new BroadcastListener()
        recipients.each {listener.registerListener(it)}
        listener.gotResult(new Result(Result.FAILED))
        recipients.each {it.failuresDetected().shouldBe(true)}
    } catch (Throwable e) {
        e.printStackTrace()
    }

}
