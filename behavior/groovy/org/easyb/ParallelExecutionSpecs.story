import java.util.concurrent.ThreadPoolExecutor
import org.easyb.ConsoleConfigurator

scenario 'when running specs in parallel', {
    given 'a configuration specified to run specs in parallel', {
        configuration = new ConsoleConfigurator().configure('-parallel', 'spec')
    }
    when 'retrieving an executor from the configration to run specs with', {
        executor = configuration.executor
    }
    then 'the executor should be configured to run with 10 threads', {
        executor.shouldBeA ThreadPoolExecutor
        executor.maximumPoolSize.shouldBe 10
    }
}

scenario 'when running specs sequentially', {
    given 'a configuration specified to run specs sequentially', {
        configuration = new ConsoleConfigurator().configure('spec')
    }
    when 'retrieving an executor from the configration to run specs with', {
        executor = configuration.executor
    }
    then 'the executor should be configured to run with 1 threads', {
        executor.shouldBeA ThreadPoolExecutor
        executor.maximumPoolSize.shouldBe 1
    }
}