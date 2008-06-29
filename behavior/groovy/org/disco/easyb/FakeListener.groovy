import org.disco.easyb.BehaviorStep
import org.disco.easyb.listener.ExecutionListenerAdaptor
import org.disco.easyb.result.Result

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