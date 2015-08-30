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
import com.vaslabs.emergency.EmergencyPreferences;
import com.vaslabs.sdc.ui.contacts.ContactsPickerActivity;
import com.vaslabs.sdc.ui.util.ValidationAdapter;
import com.vaslabs.sdc.ui.util.ValidationChangeListener;
import com.vaslabs.sdc.utils.BarometerValidator;
import com.vaslabs.sdc.utils.EmergencyContactValidator;
import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.InternallyFixableValidation;
import com.vaslabs.sdc.utils.LocationValidator;
import com.vaslabs.sdc.utils.ValidationMessageType;
import com.vaslabs.sdc.utils.WifiValidator;


public class ValidationActivity extends Activity implements ValidationChangeListener {

    private ValidationAdapter validationAdapter;
    private MaterialListView validationListView;
    private IValidator[] validators;
    private ButtonRectangle validationProceedButton;
    private static ValidationActivity va = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);
        va = this;
        validationProceedButton = (ButtonRectangle) findViewById(R.id.validationProceedButton);

        validators = new IValidator[] {BarometerValidator.getInstance(this),
                LocationValidator.getInstance(this),
                WifiValidator.getInstance(this),
                EmergencyContactValidator.getInstance(this)};
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

                if (validators[position] instanceof InternallyFixableValidation) {
                    ((InternallyFixableValidation)(validators[position])).launchActivityForResult(va);
                } else {
                    onValidationChanged();
                }
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
        validationAdapter.notifyDataSetChanged();
    }

    private static final int GET_PHONE_NUMBER = 3007;

    public void getContact() {
        startActivityForResult(new Intent(this, ContactsPickerActivity.class), GET_PHONE_NUMBER);
    }

    // Listen for results.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        // See which child activity is calling us back.
        switch (requestCode) {
            case GET_PHONE_NUMBER:
                // This is the standard resultCode that is sent back if the
                // activity crashed or didn't doesn't supply an explicit result.
                if (resultCode == RESULT_CANCELED){
                    Toast.makeText(this, "No phone number found", Toast.LENGTH_SHORT).show();
                }
                else {
                    String phoneNumber = (String) data.getExtras().get(ContactsPickerActivity.KEY_PHONE_NUMBER);
                    String name = (String) data.getExtras().get(ContactsPickerActivity.KEY_CONTACT_NAME);
                    EmergencyPreferences ep = EmergencyPreferences.load(this);
                    try {
                        ep.addEmergencyContact(name, phoneNumber);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to add contact", Toast.LENGTH_SHORT).show();
                    }
                }
            default:
                break;
        }
    }
}
