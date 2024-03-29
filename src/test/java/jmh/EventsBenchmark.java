package jmh;

import com.github.rfresh2.SimpleEventBus;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.ASMGenerator;
import net.lenni0451.lambdaevents.generator.LambdaMetaFactoryGenerator;
import net.lenni0451.lambdaevents.generator.MethodHandleGenerator;
import net.lenni0451.lambdaevents.generator.ReflectionGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.rfresh2.EventConsumer.of;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 4, time = 5)
@Measurement(iterations = 4, time = 5)
public class EventsBenchmark {

    private static final int ITERATIONS = 100_000;

    private LambdaManager reflection;
    private LambdaManager methodHandles;
    private LambdaManager lambdaMetaFactory;
    private LambdaManager asm;
    private SimpleEventBus simpleEventBus;


    @Setup
    public void setup() {
        EventBenchmarkListener listener = new EventBenchmarkListener();
        this.reflection = LambdaManager.basic(new ReflectionGenerator());
        this.methodHandles = LambdaManager.basic(new MethodHandleGenerator());
        this.lambdaMetaFactory = LambdaManager.basic(new LambdaMetaFactoryGenerator());
        this.asm = LambdaManager.basic(new ASMGenerator());

        this.reflection.register(listener);
        this.methodHandles.register(listener);
        this.lambdaMetaFactory.register(listener);
        this.asm.register(listener);

        simpleEventBus = new SimpleEventBus(Executors.newSingleThreadExecutor());
        simpleEventBus.subscribe(listener, of(Blackhole.class, listener::onEvent));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 1)
    public void callReflection(Blackhole blackhole) {
        for (int i = 0; i < ITERATIONS; i++) this.reflection.call(blackhole);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 1)
    public void callMethodHandles(Blackhole blackhole) {
        for (int i = 0; i < ITERATIONS; i++) this.methodHandles.call(blackhole);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 1)
    public void callLambdaMetaFactory(Blackhole blackhole) {
        for (int i = 0; i < ITERATIONS; i++) this.lambdaMetaFactory.call(blackhole);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 1)
    public void callASM(Blackhole blackhole) {
        for (int i = 0; i < ITERATIONS; i++) this.asm.call(blackhole);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 1)
    public void callSimpleEventBus(Blackhole blackhole) {
        for (int i = 0; i < ITERATIONS; i++) this.simpleEventBus.post(blackhole);
    }

}
