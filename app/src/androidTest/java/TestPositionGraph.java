import android.test.AndroidTestCase;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.logs.PositionGraph;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.Position;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

/**
 * Created by vasilis on 04/04/2015.
 */
public class TestPositionGraph extends AndroidTestCase {
    private PositionGraph positionGraph;

    public void setUp() {
        positionGraph = new PositionGraph();
    }


    public void test_position_graph_save_to_log_file() throws NoSuchFieldException, IllegalAccessException {
        List<String> lines = SkyDivingEnvironment.getBarometerSensorLogsLinesUncompressed(mContext);
        int expectedSize = lines.size() + 2;
        PositionGraph pg = new PositionGraph();
        MetersSensorValue value = new MetersSensorValue();
        value.setRawValue(100.0f);
        pg.registerBarometerValue(new HPASensorValue(), value, value);

        value = new MetersSensorValue();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        value.setRawValue(200.0f);
        pg.registerBarometerValue(new HPASensorValue(), value, value);


        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance(mContext);
        Field positionGraphField = SkyDivingEnvironment.class.getDeclaredField("positionGraph");
        positionGraphField.setAccessible(true);
        positionGraphField.set(sde, pg);
        sde.writeSensorLogs();

        lines = SkyDivingEnvironment.getBarometerSensorLogsLinesUncompressed(mContext);
        Collections.sort(lines);
        assertEquals("100.0,100.0", lines.get(lines.size() - 2).split(":")[1]);
        assertEquals( "200.0,200.0", lines.get(lines.size() - 1).split(":")[1]);
        assertEquals(expectedSize, lines.size());

    }
}
