package app.logorrr;

import app.logorrr.util.ColorUtil;
import app.logorrr.views.block.LPixelBuffer$;
import javafx.scene.paint.Color;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Thread)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
//@Fork(1)
public class LogoRRRBenchmark {

    int count = 10 * 10;
    int width = 100;
    int blocksize = 5;
    int[] myArray = new int[blocksize * blocksize * count];
    int c = ColorUtil.toARGB(Color.rgb(255, 200, 0));

    int vantablack = 0;

    @Benchmark
    public void benchmarkDrawRect() {
        for (int i = 0; i <= count; i++) {
            LPixelBuffer$.MODULE$.drawRect(myArray, i, width, blocksize, c, vantablack,vantablack,vantablack,vantablack);
        }
    }


    public static void main(String[] args) throws RunnerException {
  /*
        LogoRRRBenchmark b = new LogoRRRBenchmark();
        b.testExample();
*/
        Options opt = new OptionsBuilder()
                .include(LogoRRRBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();

    }

}
