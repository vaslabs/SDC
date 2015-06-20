import android.content.Context;
import android.location.LocationManager;
import android.test.AndroidTestCase;

import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.LocationValidator;
import com.vaslabs.sdc.utils.ValidationMessageType;

/**
 * Created by vnicolao on 20/06/15.
 */
public final class TestLocationValidator extends AndroidTestCase{

    public void testWhenLocationIsOn() {
        IValidator locationValidator = LocationValidator.getInstance(this.mContext);
        assertTrue(locationValidator.validate());
        assertEquals(ValidationMessageType.ERROR, locationValidator.getMessageType());
    }

    public void testWhenLocationIsOff() {
        IValidator locationValidator = LocationValidator.getInstance(this.mContext);
        assertFalse(locationValidator.validate());
        assertEquals(ValidationMessageType.ERROR, locationValidator.getMessageType());
    }

}
