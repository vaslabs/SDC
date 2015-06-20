import android.test.AndroidTestCase;

import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.LocationValidator;
import com.vaslabs.sdc.utils.ValidationMessageType;
import com.vaslabs.sdc.utils.WifiValidator;

/**
 * Created by vnicolao on 20/06/15.
 */
public class TestWifiValidator extends AndroidTestCase {

    public void testWhenWifiIsOn() {
        IValidator wifiValidator = WifiValidator.getInstance(this.mContext);
        assertTrue(wifiValidator.validate());
        assertEquals(ValidationMessageType.WARNING, wifiValidator.getMessageType());
    }

    public void testWhenWifiIsOff() {
        IValidator wifiValidator = WifiValidator.getInstance(this.mContext);
        assertFalse(wifiValidator.validate());
        assertEquals(ValidationMessageType.WARNING, wifiValidator.getMessageType());
    }
}
