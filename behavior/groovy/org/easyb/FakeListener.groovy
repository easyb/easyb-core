import org.easyb.BehaviorStep
import org.easyb.listener.ExecutionListenerAdaptor
import org.easyb.result.Result

class FakeListener extends ExecutionListenerAdaptor {
    Result result

    void startStep(BehaviorStep step) {

    }

    void gotResult(Result result) {
        this.result = result
    }

    void stopStep() {

    }
}