package demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
    public void shouldNotAddNull() {
        var map = new LFU<Boolean, Boolean>(0);
        map.add(null, Boolean.FALSE);
    }

    @Test
    public void shouldFindOne() {
        var map = new LFU<Boolean, Boolean>(1);
        var key = true;
        var val = true;
        map.add(key, val);
        Assert.assertEquals("data loss", val, map.find(key));
    }

    @Test()
    public void shouldNotFindNull() {
        var map = new LFU<Boolean, Boolean>(1);
        try {
            var ignored = map.find(null);
            throw new IllegalStateException("should not see this");
        } catch (NullPointerException expected) {
            // we don't allow null keys or values
        }
    }
}
