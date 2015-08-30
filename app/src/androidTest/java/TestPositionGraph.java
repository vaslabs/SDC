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


    public void test_position_graph_save_to_log_file() throws NoSuchFieldException, IllegalAccessException {
        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance(mContext);
        Field positionGraphField = SkyDivingEnvironment.class.getDeclaredField("positionGraph");
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

        List<String> lines = SkyDivingEnvironment.getBarometerSensorLogsLinesUncompressed(mContext);
        assertEquals(2, lines.size());

    }
}
