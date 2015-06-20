import com.vaslabs.sdc.utils.DynamicQueue;

import android.test.AndroidTestCase;

public class TestDynamicQueue extends AndroidTestCase {
    DynamicQueue<String> queue;
    protected void setUp() throws Exception {
        super.setUp();
        queue = new DynamicQueue<String>();
    }
    
    public void test_adding_elements() {
        queue.append( "head" );
        queue.append( "1" );
        queue.append( "2" );
        queue.append( "3" );
        queue.append( "tail" );
        
        assertEquals(5, queue.size());
        assertEquals("head", queue.pop());
        assertEquals(4, queue.size());
        assertEquals("1", queue.pop());
        assertEquals("2", queue.pop());
        assertEquals("3", queue.pop());
        assertEquals("tail", queue.pop());
        assertEquals(0, queue.size());
        assertNull(queue.pop());
        assertEquals(0, queue.size());
        
    }
    public void test_usage_of_queue() {
        queue.append( "head" );
        queue.append( "1" );
        assertEquals("head", queue.pop());
        assertEquals(1, queue.size());
        queue.append( "2" );
        assertEquals("1", queue.pop());
        assertEquals("2", queue.pop());
        assertEquals(0, queue.size());
        queue.append( "3" );
        assertEquals(1, queue.size());
        assertEquals("3", queue.pop());

        queue.append( "tail" );
        queue.pop();
        queue.pop();
        queue.pop();
        assertEquals(0, queue.size());
    }
}
