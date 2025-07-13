package app.logorrr;

import app.logorrr.jfxbfr.color.BlockColor;
import app.logorrr.jfxbfr.ChunkListCell$;
import app.logorrr.jfxbfr.color.ColorUtil;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.paint.Color;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.IntBuffer;

@State(Scope.Thread)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
//@Fork(1)
public class LogoRRRBenchmark {

    int count = 100;
    int width = 1000;
    int height = 1000;
    int blocksize = 10;
    IntBuffer myArray = IntBuffer.wrap(new int[width*height]);
    int c = ColorUtil.toARGB(Color.rgb(255, 200, 0));

    int vantablack = 0;

    @Benchmark
    public void benchmarkDrawRect() {
        for (int i = 0; i <= count; i++) {
            BlockColor blockColor = new BlockColor(c, vantablack, vantablack, vantablack, vantablack);
            ChunkListCell$.MODULE$.drawRectangle(new PixelBuffer(width, height, myArray, PixelFormat.getIntArgbPreInstance()), i, blockColor, blocksize, width);
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
