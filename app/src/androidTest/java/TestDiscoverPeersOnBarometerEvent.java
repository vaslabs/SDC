import android.net.wifi.p2p.WifiP2pManager;
import android.test.AndroidTestCase;
import android.util.Log;

import com.vaslabs.sdc.connectivity.SkyDivingEnvironment;
import com.vaslabs.sdc.connectivity.WirelessBroadcastReceiver;
import com.vaslabs.sdc.sensors.BarometerSensor;
import com.vaslabs.sdc.utils.BarometerTrendStrategy;
import com.vaslabs.sdc.utils.DefaultBarometerTrendListener;
import com.vaslabs.vtrends.impl.AbstractTrendStrategy;
import com.vaslabs.vtrends.types.DifferentiableFloat;
import com.vaslabs.vtrends.types.TrendDirection;
import com.vaslabs.vtrends.types.TrendPoint;


import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by vnicolao on 16/05/15.
 */
public class TestDiscoverPeersOnBarometerEvent extends AndroidTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test_that_trend_detects_increament() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.5);

        trendStrategy.acceptValue(Double.valueOf(0), new DifferentiableFloat(1));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(1), new DifferentiableFloat(2));
        assertEquals(2, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(2), new DifferentiableFloat(3));
        assertEquals(3, trendStrategy.getSize());
        Map<Double, TrendDirection> directionGraph = trendStrategy.getDirectionGraph(3);
        assertEquals(3, directionGraph.size());
        assertNull(directionGraph.get(Double.valueOf(0)));
        assertEquals(TrendDirection.UP, directionGraph.get(Double.valueOf(1)));
        assertEquals(TrendDirection.UP, directionGraph.get(Double.valueOf(2)));

    }

    public void test_that_trend_detects_decreament() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.5);

        trendStrategy.acceptValue(Double.valueOf(0), new DifferentiableFloat(3));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(1), new DifferentiableFloat(2));
        assertEquals(2, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(2), new DifferentiableFloat(1));
        assertEquals(3, trendStrategy.getSize());
        Map<Double, TrendDirection> directionGraph = trendStrategy.getDirectionGraph(3);
        assertEquals(3, directionGraph.size());
        assertNull(directionGraph.get(Double.valueOf(0)));
        assertEquals(TrendDirection.DOWN, directionGraph.get(Double.valueOf(1)));
        assertEquals(TrendDirection.DOWN, directionGraph.get(Double.valueOf(2)));
    }

    public void test_time_density() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.1);

        trendStrategy.acceptValue(Double.valueOf(0), new DifferentiableFloat(3));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(0.09), new DifferentiableFloat(2));
        assertEquals(1, trendStrategy.getSize());
        trendStrategy.acceptValue(Double.valueOf(0.11), new DifferentiableFloat(1));
        assertEquals(2, trendStrategy.getSize());
        Map<Double, TrendDirection> directionGraph = trendStrategy.getDirectionGraph(3);
        assertEquals(2, directionGraph.size());
        assertNull(directionGraph.get(Double.valueOf(0)));
        assertEquals(TrendDirection.DOWN, directionGraph.get(Double.valueOf(0.11)));
    }

    int numbersCalled = 0;
    public void test_dive_scenario_without_perturbations() {
        AbstractTrendStrategy<DifferentiableFloat> trendStrategy = new BarometerTrendStrategy<DifferentiableFloat>(0.5, 0.1, 3000);
        DefaultBarometerTrendListener trendListener = new DefaultBarometerTrendListener() {
            @Override
            public void onTrendEvent() {
                numbersCalled++;
            }
        };

        trendListener.forDirectionAction(TrendDirection.DOWN);
        trendListener.forCertainAltitude(new TrendPoint(new DifferentiableFloat(1000f), Double.valueOf(0)));
        trendStrategy.registerEventListener(trendListener);
        uponClimbing(trendStrategy);
        assertEquals(0, numbersCalled);
        assertEquals(3000, trendStrategy.getSize());
        uponDiving(trendStrategy);
        assertEquals(3000, trendStrategy.getSize());
        assertEquals(1000, numbersCalled);


    }

    private void uponClimbing(AbstractTrendStrategy<DifferentiableFloat> trendStrategy) {
        for (int i = 0; i < 3000; i++) {
            trendStrategy.acceptValue(Double.valueOf(i), new DifferentiableFloat(Float.valueOf(i)));
        }
    }

    private void uponDiving(AbstractTrendStrategy<DifferentiableFloat> trendStrategy) {
        for (float i = 3000; i >= 0; i--) {
            trendStrategy.acceptValue(Double.valueOf(6000 - i), new DifferentiableFloat(i));
        }
    }

    public void test_integration() throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        SkyDivingEnvironment sde = SkyDivingEnvironment.getInstance(this.mContext);
        Field trendStrategy = SkyDivingEnvironment.class.getDeclaredField("trendStrategy");
        trendStrategy.setAccessible(true);
        Field hasStartedScanning = SkyDivingEnvironment.class.getDeclaredField("hasStartedScanning");
        hasStartedScanning.setAccessible(true);

        Field barometerSensor = SkyDivingEnvironment.class.getDeclaredField("barometerSensor");
        barometerSensor.setAccessible(true);
        barometerSensor.set(sde, BarometerSensor.getInstance(mContext));

        WifiP2pManager mManager = (WifiP2pManager) this.mContext.getSystemService(this.mContext.WIFI_P2P_SERVICE);
        WifiP2pManager.Channel mChannel = mManager.initialize(this.mContext, mContext.getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                Log.i("ChannelListener", "Channel disconnected");
            }
        });

        WirelessBroadcastReceiver mReceiver = new WirelessBroadcastReceiver(mManager, mChannel, this.mContext);

        SkyDivingEnvironment.getInstance().registerWirelessManager(mManager, mChannel, null);

        AbstractTrendStrategy ats = (AbstractTrendStrategy)(trendStrategy.get(sde));


        for (float i = 0; i < 3000; i++) {
            ats.acceptValue(i*0.6, new DifferentiableFloat(i));
            assertFalse(hasStartedScanning.getBoolean(sde));
        }

        for (float i = 0; i < 3000; i++) {
            ats.acceptValue(3000*0.6 + i*0.6, new DifferentiableFloat(3000-i));
            if (3000 - i <= 950) {
                assertTrue(hasStartedScanning.getBoolean(sde));
            } else {
                assertFalse(hasStartedScanning.getBoolean(sde));
            }
        }
    }

}
