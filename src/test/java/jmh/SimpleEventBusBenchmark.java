package jmh;

import com.github.rfresh2.SimpleEventBus;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.rfresh2.EventConsumer.of;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 4, time = 5)
@Measurement(iterations = 4, time = 5)
public class SimpleEventBusBenchmark {
    private static final int ITERATIONS = 100_000;

    private SimpleEventBus simpleEventBus;

    @Setup
    public void setup() {
        EventBenchmarkListener listener = new EventBenchmarkListener();
        simpleEventBus = new SimpleEventBus(Executors.newSingleThreadExecutor());
        simpleEventBus.subscribe(listener, of(Blackhole.class, listener::onEvent));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 1)
    public void callEvent(Blackhole blackhole) {
        for (int i = 0; i < ITERATIONS; i++) this.simpleEventBus.post(blackhole);
    }
}
