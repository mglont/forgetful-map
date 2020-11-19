package demo;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.IIIII_Result;

import java.util.Deque;
import java.util.Map;

import static org.openjdk.jcstress.annotations.Expect.ACCEPTABLE;


@JCStressTest
@State
@Outcome(id = "2, 1, 1, 2, 1", expect = ACCEPTABLE, desc = "actor 1 won")
@Outcome(id = "2, 1, 1, 3, 1", expect = ACCEPTABLE, desc = "actor 1 won")
@Outcome(id = "2, 2, 1, 1, 1", expect = ACCEPTABLE, desc = "actor 1 won")
@Outcome(id = "2, 2, 1, 3, 1", expect = ACCEPTABLE, desc = "actor 1 won")
@Outcome(id = "2, 3, 1, 1, 1", expect = ACCEPTABLE, desc = "actor 1 won")
@Outcome(id = "2, 3, 1, 2, 1", expect = ACCEPTABLE, desc = "actor 1 won")

@Outcome(id = "2, 1, 2, 2, 2", expect = ACCEPTABLE, desc = "actor 2 won")
@Outcome(id = "2, 1, 2, 3, 2", expect = ACCEPTABLE, desc = "actor 2 won")
@Outcome(id = "2, 2, 2, 1, 2", expect = ACCEPTABLE, desc = "actor 2 won")
@Outcome(id = "2, 2, 2, 3, 2", expect = ACCEPTABLE, desc = "actor 2 won")
@Outcome(id = "2, 3, 2, 1, 2", expect = ACCEPTABLE, desc = "actor 2 won")
@Outcome(id = "2, 3, 2, 2, 2", expect = ACCEPTABLE, desc = "actor 2 won")

@Outcome(id = "2, 1, 1, 2, 2", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 1, 1, 3, 2", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 2, 1, 1, 2", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 2, 1, 3, 2", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 3, 1, 1, 2", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 3, 1, 2, 2", expect = ACCEPTABLE, desc = "tie")

@Outcome(id = "2, 1, 2, 2, 1", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 1, 2, 3, 1", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 2, 2, 1, 1", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 2, 2, 3, 1", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 3, 2, 1, 1", expect = ACCEPTABLE, desc = "tie")
@Outcome(id = "2, 3, 2, 2, 1", expect = ACCEPTABLE, desc = "tie")
public class NeverExceedCapacity {
    private final LFU<Integer, Integer> lfu = new LFU<>(2);

    @Actor
    public void actor1() {
        lfu.add(1, 1);
        lfu.add(2, 1);
        lfu.add(3, 1);
    }

    @Actor
    public void actor2() {
        lfu.add(1, 2);
        lfu.add(2, 2);
        lfu.add(3, 2);
    }

    @Arbiter
    public void arbitrer(IIIII_Result result) {
        result.r1 = lfu.size();
        final Deque<Integer> q = lfu.getQueue();
        final Map<Integer, Integer> keys = lfu.getLookup();
        var h = q.getFirst();
        var t = q.getLast();
        result.r2 = h;
        result.r3 = keys.get(h);
        result.r4 = t;
        result.r5 = keys.get(t);
    }
}

