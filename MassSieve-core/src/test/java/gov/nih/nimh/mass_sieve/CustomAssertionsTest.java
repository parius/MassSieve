package gov.nih.nimh.mass_sieve;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static gov.nih.nimh.mass_sieve.CustomAssertions.*;
import static org.junit.Assert.assertEquals;

/**
 * TODO Describe class
 * <p/>
 * <p/>
 * Created at: Jul 23, 2011 11:25:49 AM
 *
 * @author Alexey Tigarev
 */
public class CustomAssertionsTest {

    class Lizd<T> extends ArrayList implements List {
        public Lizd(T... elems) {
            for (T elem: elems) {
                add(elem);
            }
        }
    }

    @Test(expected = AssertionError.class)
    public void testAssertListEqualsNull() {
        assertListEquals(null);  // null list is not supported
    }

    @Test
    public void testAssertListEqualsEmpty() {
        assertListEquals(new ArrayList<Integer>());
    }

    @Test
    public void testAssertListEqualsOneElementSame() {
        assertListEquals(new Lizd<String>("1"), "1");
    }

    @Test(expected = AssertionError.class)
    /**
     * Wrong way to compare lists: elements must be specified in parameters
     */
    public void testAssertListEqualsOneMistake() {
        assertListEquals(new Lizd<String>("1"), new Lizd<String>("1"));
    }

    @Test(expected = AssertionError.class)
    public void testAssertListEqualsOneElementDifferent() {
        assertListEquals(new Lizd<String>("1"), "2");
    }

    @Test
    public void testAssertListEqualsTwoElems() {
        assertListEquals(new Lizd<String>("a", "b"), "a", "b");
        assertListEquals(new Lizd<String>(null, "b"), null, "b");
    }

    @Test
    public void testAssertListEqualsManyElems() {
        assertListEquals(new Lizd<Integer>(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void testLizd() {
        assertEquals(0, new Lizd().size());

        assertEquals(1, new Lizd<Integer>(0).size());
        assertEquals(0, new Lizd<Integer>(0).get(0));

        assertEquals(2, new Lizd<Integer>(0, 1).size());
        assertEquals(0, new Lizd<Integer>(0, 1).get(0));
        assertEquals(1, new Lizd<Integer>(0, 1).get(1));
    }
}
