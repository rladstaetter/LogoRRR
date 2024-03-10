package app.logorrr;

import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class LogoRRRBenchmark {

    @Benchmark
    public void testExample() {
    }

}
