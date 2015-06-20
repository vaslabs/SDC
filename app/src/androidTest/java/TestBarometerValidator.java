import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.test.AndroidTestCase;

import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.sensors.SDSensorManager;
import com.vaslabs.sdc.utils.BarometerValidator;
import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.ValidationMessageType;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.lang.reflect.Field;

/**
 * Created by vnicolao on 20/06/15.
 */
public class TestBarometerValidator extends AndroidTestCase {

    @Override
    public void setUp() {
        System.setProperty("dexmaker.dexcache", this.mContext.getCacheDir().toString());
    }

    public void testBarometerValidatorOnBarometerPresent() {

        IValidator validator = BarometerValidator.getInstance(this.mContext);
        assertEquals(ValidationMessageType.WARNING, validator.getMessageType());

        assertTrue(validator.validate());

    }



    public void testBarometerValidatorOnBarometerAbsent() throws NoSuchFieldException, IllegalAccessException {

        SensorManager sensorManager;
        SDSensorManager sdSensorManager;
        sensorManager = Mockito.mock(SensorManager.class);
        sdSensorManager = Mockito.mock(SDSensorManager.class);
        Mockito.when(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)).thenReturn(null);

        Field fieldSensorManager = SDSensorManager.class.getDeclaredField("sensorManager");
        fieldSensorManager.setAccessible(true);

        Field fieldContext = SDSensorManager.class.getDeclaredField("context");
        fieldContext.setAccessible(true);

        Field fieldSDSensorManager = SDSensorManager.class.getDeclaredField("sdSensorManager");
        fieldSDSensorManager.setAccessible(true);

        fieldSensorManager.set(null, sensorManager);
        fieldContext.set(null, this.mContext);
        fieldSDSensorManager.set(null, sdSensorManager);


        IValidator validator = BarometerValidator.getInstance(this.mContext);
        assertEquals(ValidationMessageType.WARNING, validator.getMessageType());

        assertFalse(validator.validate());

        //cleanup
        fieldSDSensorManager.set(null, null);
        fieldContext.set(null, null);
        fieldSensorManager.set(null, null);
    }


}
