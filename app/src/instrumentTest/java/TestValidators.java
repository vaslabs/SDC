import android.test.AndroidTestCase;

import com.vaslabs.sdc.utils.BarometerValidator;
import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.ValidationMessageType;

/**
 * Created by vnicolao on 20/06/15.
 */
public class TestValidators extends AndroidTestCase {

    public void testBarometerValidatorOnBarometerPresent() {

        IValidator validator = new BarometerValidator(this.mContext);
        assertEquals(ValidationMessageType.WARNING, validator.getMessageType());

        assertTrue(validator.validate());

    }



    public void testBarometerValidatorOnBarometerAbsent() {

        IValidator validator = new BarometerValidator(this.mContext);
        assertEquals(ValidationMessageType.WARNING, validator.getMessageType());

        assertTrue(validator.validate());
    }


}
