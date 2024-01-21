package jmh;

import net.lenni0451.lambdaevents.EventHandler;
import org.openjdk.jmh.infra.Blackhole;

public class EventBenchmarkListener {

    @EventHandler
    public void onEvent(Blackhole event) {
        event.consume(Integer.bitCount(Integer.parseInt("123")));
    }

}
