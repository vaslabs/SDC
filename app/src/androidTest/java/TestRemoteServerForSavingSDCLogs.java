import org.json.JSONException;
import org.json.JSONObject;

import com.vaslabs.pwa.CommunicationManager;
import com.vaslabs.pwa.Response;
import com.vaslabs.sdc.logs.SDCLogManager;
import com.vaslabs.sdc.pwa.PWAInvalidCredentialsException;
import com.vaslabs.sdc.ui.R;

import android.test.AndroidTestCase;

public class TestRemoteServerForSavingSDCLogs extends AndroidTestCase {

    private String apitoken;

    public void setUp() {
        apitoken = mContext.getString( R.string.apitoken );
    }
    
    public void test_that_logs_can_be_submitted() throws JSONException {
        CommunicationManager cm = CommunicationManager.getInstance();
        cm.setRemoteHost( mContext.getString(R.string.remote_host));
        SDCLogManager lm = SDCLogManager.getInstance(mContext);
        try {
            lm.submitLogs(apitoken);
        }
        catch (Exception e) {
            fail(e.toString());
        }
        Response response = lm.getLastResponse();
        Object responseBody = response.getBody();
        assertTrue(responseBody instanceof JSONObject);
        JSONObject json = (JSONObject)responseBody;
        assertEquals("OK", json.getString("message"));
        
        
        try {
            lm.submitLogs("invalidtoken");
            
        }
        catch (Exception ice) {
            if (ice instanceof PWAInvalidCredentialsException) {
                return;
            }
            fail(ice.toString());
        }
        
    }
    
    
    
}
