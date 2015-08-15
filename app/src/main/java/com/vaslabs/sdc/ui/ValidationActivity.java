package com.vaslabs.sdc.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dexafree.materialList.controller.RecyclerItemClickListener;
import com.dexafree.materialList.model.CardItemView;
import com.dexafree.materialList.view.MaterialListView;
import com.gc.materialdesign.views.ButtonRectangle;
import com.vaslabs.sdc.ui.util.ValidationAdapter;
import com.vaslabs.sdc.ui.util.ValidationChangeListener;
import com.vaslabs.sdc.utils.BarometerValidator;
import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.LocationValidator;
import com.vaslabs.sdc.utils.ValidationMessageType;
import com.vaslabs.sdc.utils.WifiValidator;


public class ValidationActivity extends Activity implements ValidationChangeListener {

    private ValidationAdapter validationAdapter;
    private MaterialListView validationListView;
    private IValidator[] validators;
    private ButtonRectangle validationProceedButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        validationProceedButton = (ButtonRectangle) findViewById(R.id.validationProceedButton);

        validators = new IValidator[] {BarometerValidator.getInstance(this), LocationValidator.getInstance(this), WifiValidator.getInstance(this)};
        validationAdapter = new ValidationAdapter(this, validators, this);
        validationListView = (MaterialListView) findViewById(R.id.validationStepsListView);
        validationListView.setAdapter(validationAdapter);

        onValidationChanged();
        Toast.makeText(this, "Click on the cards to refresh the validation", Toast.LENGTH_LONG).show();
        validationProceedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SkyDivingSessionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        validationListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(CardItemView view, int position) {
                onValidationChanged();
            }

            @Override
            public void onItemLongClick(CardItemView view, int position) {
                Toast.makeText(view.getContext(),
                        "Is " + validators[position].getTitle() + " available?",
                        Toast.LENGTH_LONG).show();
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
            v.refreshImage(valid);
            if (!valid) {
                if (v.getMessageType() == ValidationMessageType.ERROR)
                    hasErrors = true;
            }
            isReadyToProceed = isReadyToProceed && valid;

        }
        validationProceedButton.setEnabled(!hasErrors);
        if (isReadyToProceed) {
            validationProceedButton.setText(this.getString(R.string.proceed));
        } else {
            validationProceedButton.setText(this.getString(R.string.caution_proceed));
        }
    }
}
