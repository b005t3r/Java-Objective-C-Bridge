package ca.weblite.objc;

import static ca.weblite.objc.RuntimeUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import ca.weblite.objc.foundation.NSRange;
import org.junit.jupiter.api.Test;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 *
 * @author shannah
 */
public class ProxyTest {
    

    /**
     * Test of load method, of class Proxy.
     */
    @Test
    public void testNSArray() {
        
        Proxy o = new Proxy(msgPointer("NSMutableArray", "array"));
        long expectedCount = 0;
        long actualCount = (Long)o.send("count");
        assertEquals(expectedCount, actualCount);
        
        // Add a string to the array and check that
        // the last object matches that string.
        // Strings should be automatically converted to NSStrings
        String aString = "foobar";
        o.send("addObject:", aString);
        
        String obj = (String)o.send("lastObject");
        assertEquals(aString, obj);
        
        // There should be one object in the array
        expectedCount = 1;
        actualCount = (Long)o.send("count");
        assertEquals(expectedCount, actualCount);
        
        //Now the string is there
        boolean expectedContains = true;
        boolean actualContains = o.sendBoolean("containsObject:", aString);
        assertEquals(expectedContains, actualContains);
        
        // Let's try to call a method that takes a structure as one of the 
        // parameters

        Pointer[] buffer = new Pointer[1];
        NSRange range = new NSRange.ByValue();
        range.setLength(1);
        range.setLocation(0);
        o.send("getObjects:range:", buffer, range);
        
        assertEquals(1, buffer.length);

        // Make sure that the first (and only entry) in the
        // buffer is the same string that we added previously.
        assertEquals(aString, str(buffer[0]));

        Proxy enumerator = o.sendProxy("objectEnumerator");
        
        String placeHolder = (String)enumerator.send("nextObject");
        assertEquals(aString, placeHolder);
        
        Proxy newArray = o.sendProxy("arrayByAddingObject:", "Another String");
        
        assertEquals(2, newArray.sendInt("count"));
    }
    
}
