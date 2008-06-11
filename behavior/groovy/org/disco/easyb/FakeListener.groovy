import org.disco.easyb.result.Result
import org.disco.easyb.BehaviorStep

class FakeListener {
    Result result

    void startStep(BehaviorStep step) {

    }

    void gotResult(Result result) {
        this.result = result
    }

    void stopStep() {

    }
}