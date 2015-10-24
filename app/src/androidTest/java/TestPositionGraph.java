import android.test.AndroidTestCase;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.logs.PositionGraph;
import com.vaslabs.sdc.sensors.HPASensorValue;
import com.vaslabs.sdc.sensors.MetersSensorValue;
import com.vaslabs.sdc.utils.Position;
import com.vaslabs.units.TimeUnit;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        MetersSensorValue value = new MetersSensorValue(100f);
        pg.registerBarometerValue(null, value, value);

        value = new MetersSensorValue(100f);
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pg.registerBarometerValue(null, value, value);


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

    public void test_position_graph_population() throws IllegalAccessException, NoSuchFieldException {
        PositionGraph pg = new PositionGraph();
        MetersSensorValue msv = new MetersSensorValue(100f);
        MetersSensorValue msvR = new MetersSensorValue(0f);
        pg.registerBarometerValue(null, msv, msvR);
        sleepFor(2, TimeUnit.MILLISECONDS);

        pg.registerBarometerValue(null, new MetersSensorValue(101f), new MetersSensorValue(1f));
        sleepFor(2, TimeUnit.MILLISECONDS);
        pg.registerBarometerValue(null, new MetersSensorValue(101.3f), new MetersSensorValue(1.3f));
        sleepFor(2, TimeUnit.MILLISECONDS);
        pg.registerBarometerValue(null, new MetersSensorValue(102f), new MetersSensorValue(2f));
        sleepFor(2, TimeUnit.MILLISECONDS);

        pg.registerBarometerValue(null, new MetersSensorValue(103f), new MetersSensorValue(3f));
        sleepFor(2, TimeUnit.MILLISECONDS);

        pg.registerBarometerValue(null, new MetersSensorValue(103.9f), new MetersSensorValue(3.9f));
        sleepFor(2, TimeUnit.MILLISECONDS);
        pg.registerBarometerValue(null, new MetersSensorValue(105f), new MetersSensorValue(4f));
        sleepFor(2, TimeUnit.MILLISECONDS);

        pg.registerBarometerValue(null, new MetersSensorValue(106f), new MetersSensorValue(5f));

        Field field = PositionGraph.class.getDeclaredField("barometerAltitudeValues");
        field.setAccessible(true);
        Map barometerData = (Map) field.get(pg);
        assertEquals(6, barometerData.size());
    }

    private void sleepFor(int value, TimeUnit timeUnit) {
        long milliseconds = (long)TimeUnit.MILLISECONDS.convert(value, timeUnit);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
