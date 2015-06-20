package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.vaslabs.sdc.ui.util.ValidationAdapter;
import com.vaslabs.sdc.ui.util.ValidationChangeListener;
import com.vaslabs.sdc.utils.BarometerValidator;
import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.LocationValidator;
import com.vaslabs.sdc.utils.ValidationMessageType;
import com.vaslabs.sdc.utils.WifiValidator;


public class ValidationActivity extends Activity implements ValidationChangeListener {

    private ValidationAdapter validationAdapter;
    private ListView validationListView;
    private IValidator[] validators;
    private Button validationProceedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        validationProceedButton = (Button) findViewById(R.id.validationProceedButton);

        validators = new IValidator[] {BarometerValidator.getInstance(this), LocationValidator.getInstance(this), WifiValidator.getInstance(this)};
        validationAdapter = new ValidationAdapter(this, validators, this);
        validationListView = (ListView) findViewById(R.id.validationStepsListView);
        validationListView.setAdapter(validationAdapter);

        onValidationChanged();
        validationProceedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SkyDivingSessionActivity.class);
                startActivity( intent );
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.validation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValidationChanged() {
        validationAdapter.notifyDataSetChanged();
        boolean isReadyToProceed = true;
        boolean hasErrors = false;
        boolean valid;
        for (IValidator v : validators) {
            valid = v.validate();
            if (!valid) {
                if (v.getMessageType() == ValidationMessageType.ERROR)
                    hasErrors = true;
            }
            isReadyToProceed = isReadyToProceed && valid;

        }
        validationProceedButton.setEnabled(!hasErrors);
        if (isReadyToProceed) {
                validationProceedButton.setText(R.string.proceed);
            } else {
                validationProceedButton.setText(R.string.caution_proceed);
            }
        }
}
