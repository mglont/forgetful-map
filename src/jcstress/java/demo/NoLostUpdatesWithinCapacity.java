package demo;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.IZI_Result;

@JCStressTest
@State
@Outcome(id = "1, true, 1", expect = Expect.ACCEPTABLE, desc = "actor1 won")
@Outcome(id = "1, false, 1", expect = Expect.ACCEPTABLE, desc = "actor2 won")
public class NoLostUpdatesWithinCapacity {
    private final LFU<Integer, Boolean> lfu = new LFU<>(1);

    @Actor
    public void actor1() {
        lfu.add(1, true);
    }

    @Actor
    public void actor2() {
        lfu.add(1, false);
    }

    @Arbiter
    public void arbiter(IZI_Result r) {
        final boolean value = lfu.getLookup().get(1);
        r.r1 = 1;
        r.r2 = value;
        r.r3 = lfu.size();
    }
}
