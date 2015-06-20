import android.test.AndroidTestCase;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.logs.PositionGraph;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.Position;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by vasilis on 04/04/2015.
 */
public class TestPositionGraph extends AndroidTestCase {
    private PositionGraph positionGraph;

    public void setUp() {
        positionGraph = new PositionGraph();
    }


    public void test_that_values_are_converted_to_byte_stream() {
        MetersSensorValue value = new MetersSensorValue();
        value.setRawValue(1.0f);
        long currentTime1 = System.currentTimeMillis();
        positionGraph.registerBarometerValue(new HPASensorValue(), value);
        value = new MetersSensorValue();
        value.setRawValue(2f);
        long currentTime2 = System.currentTimeMillis();
        positionGraph.registerBarometerValue(new HPASensorValue(), value);
        byte[] byteStream = positionGraph.getBarometerData();
        long firstTimeRegistration = 0;
        for (int i = 0; i < 8; i++) {
            firstTimeRegistration |= (byteStream[i] & 0xffL) << (i*8);
        }

        int barometerValueBits = 0;
        for (int i = 0; i < 4; i++) {
            barometerValueBits |= ((0 | byteStream[8+i]) << (i*8));
        }

        float firstBarometerValue = Float.intBitsToFloat(barometerValueBits);

        assertTrue(firstBarometerValue == 1f || firstBarometerValue == 2f);

        if (firstBarometerValue == 1f) {
            assertTrue(Math.abs(firstTimeRegistration - currentTime1) <= 1);
        } else {
            assertTrue(Math.abs(firstTimeRegistration - currentTime1) <= 2);
        }

    }

    public void test_position_graph_save_to_log_file() throws NoSuchFieldException, IllegalAccessException {
        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance(mContext);
        Field positionGraphField = SkyDivingEnvironment.class.getField("positionGraph");
        positionGraphField.setAccessible(true);
        positionGraphField.set(sde, new PositionGraph());
        PositionGraph pg = (PositionGraph) positionGraphField.get(sde);
        MetersSensorValue value = new MetersSensorValue();
        value.setRawValue(100.0f);
        pg.registerBarometerValue(new HPASensorValue(), value);

        value = new MetersSensorValue();
        value.setRawValue(200.0f);
        pg.registerBarometerValue(new HPASensorValue(), value);

        sde.writeSensorLogs();

        List<String> lines = sde.getBarometerSensorLogsLinesUncompressed();
        assertEquals(2, lines.size());

    }
}
