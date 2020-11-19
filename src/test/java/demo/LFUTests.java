package demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Deque;

@RunWith(JUnit4.class)
public class LFUTests {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotConstructAMapOfNegativeSize() {
        new LFU<Boolean, Boolean>(-1);
    }

    @Test
    public void shouldCreateAMap() {
        var map = new LFU<Byte, Byte>(Integer.MAX_VALUE);
        Assert.assertEquals("a new map should be empty", 0, map.size());
    }

    @Test
    public void shouldAddOne() {
        var map = new LFU<Boolean, Boolean>(1);
        map.add(true, true);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddNullKey() {
        var map = new LFU<Boolean, Boolean>(1);
        map.add(null, Boolean.FALSE);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAddNullValue() {
        var map = new LFU<Boolean, Boolean>(1);
        map.add(Boolean.TRUE, null);
    }

    @Test
    public void shouldNotOverflow() {
        var map = new LFU<Integer, Integer>(0);
        map.add(1, 1);
        Assert.assertEquals(0, map.size());

        map = new LFU<>(1);
        map.add(1, 1);
        map.add(2, 2);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void shouldUpdateExisting() {
        var map = new LFU<Integer, Integer>(1);
        map.add(1, 1);
        map.add(1, 2);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(Integer.valueOf(2), map.find(1));
    }

    @Test
    public void shouldFindOne() {
        var map = new LFU<Boolean, Boolean>(1);
        map.add(true, true);
        Assert.assertEquals("data loss", true, map.find(true));
        map.add(false, false);
    }

    @Test
    public void shouldNotFindAnythingWhenEmpty() {
        var map = new LFU<Boolean, Boolean>(0);
        Assert.assertNull(map.find(true));
        Assert.assertNull(map.find(false));
    }

    @Test()
    public void shouldNotFindNull() {
        var map = new LFU<Boolean, Boolean>(1);
        try {
            @SuppressWarnings("unused") var ignored = map.find(null);
            throw new IllegalStateException("should not see this");
        } catch (NullPointerException expected) {
            // we don't allow null keys or values
        }
    }

    @Test
    public void shouldUpdateInPlace() {
        var map = new LFU<Boolean, Boolean>(2);
        map.add(true, true);
        map.add(false, true);
        final Deque<Boolean> queue = map.getQueue();
        Assert.assertEquals(2, queue.size());
        Assert.assertTrue(queue.getFirst());
        Assert.assertFalse(queue.getLast());

        map.add(true, false);
        Assert.assertTrue(queue.getFirst());

        Assert.assertTrue(map.getLookup().get(false));
        Assert.assertFalse(map.getLookup().get(true));
    }
}
