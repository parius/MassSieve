package gov.nih.nimh.mass_sieve;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * TODO Describe class
 * <p/>
 * <p/>
 * Created at: Jul 23, 2011 11:08:42 AM
 *
 * @author Alexey Tigarev
 */
public class CustomAssertions {
    public static <X,Y> void assertContainsOnlyKey(Map<X, Y> map, X key) {
        assertEquals(1, map.size());
        assertTrue(map.containsKey(key));
    }

    public static <T> void assertSetEquals(Set<T> set, T... elems) {
        assertEquals(elems.length, set.size());
        for (T elem: elems) {
            assertTrue(set.contains(elem));
        }
    }

    /**
     * Verifies if list contains the only specified element
     * @param list
     * @param element
     */
    public static void assertsContainsOnlyElement(List list, Object element) {
        assertNotNull("Non-null list with single element expected", list);
        assertEquals("One and only element expected", 1, list.size());
        assertEquals("One and only element expected", element, list.get(0));
    }

    public static <E> void assertListEquals(List<E> list, E... elems) {
        assertNotNull(list);
        assertEquals(elems.length, list.size());
        for (int i = 0; i < elems.length; i++) {
            assertEquals(elems[i], list.get(i));
        }
    }

    public static void assertListEmpty(List<?> list) {
        assertNotNull("Non-null empty list expected", list);
        assertTrue("Non-null empty list expected", list.isEmpty());
    }


    public static <E> void assertSetEmpty(Set<E> set) {
        assertSetEmpty("Non-null empty set expected", set);
    }

    public static <E> void assertSetEmpty(String message, Set<E> set) {
        assertNotNull(set);
        assertTrue(message, set.isEmpty());
    }

    public static <E> void assertSetContainsOnlyElement(Set<E> set, E elem) {
        assertNotNull(set);
        assertEquals(1, set.size());
        assertEquals(elem, set.toArray()[0]);
    }

}
