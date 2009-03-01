import org.easyb.listener.FailureDetector
import org.easyb.result.Result

def listener

before "create a failure listener", {
    listener = new FailureDetector()
}

it "should consider failing specs as a cause of failure", {
    listener.gotResult(new Result(Result.FAILED))
    listener.failuresDetected().shouldBe(true)
}

it "should not consider passing specs as a cause of failure", {
    listener.gotResult(new Result(Result.SUCCEEDED))
    listener.failuresDetected().shouldBe(false)
}

it "should not consider pending specs as a cause of failure", {
    listener.gotResult(new Result(Result.PENDING))
    listener.failuresDetected().shouldBe(false)
}