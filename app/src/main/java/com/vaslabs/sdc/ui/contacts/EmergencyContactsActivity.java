package com.vaslabs.sdc.ui.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.OnDismissCallback;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.vaslabs.emergency.EmergencyContact;
import com.vaslabs.emergency.EmergencyPreferences;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.units.TimeUnit;

import java.util.Arrays;
import java.util.List;

public class EmergencyContactsActivity extends Activity {

    private MaterialListView emergencyContactsListView;
    Card[] emergencyContactCards = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        emergencyContactsListView = (MaterialListView) findViewById(R.id.emergencyContactsList);
        EmergencyPreferences ep = EmergencyPreferences.load(this);
        List<EmergencyContact> emergencyContactList = ep.getEmergencyContactList();
        emergencyContactCards = toCards(ep.getEmergencyContactList());
        emergencyContactsListView.addAll(emergencyContactCards);
        emergencyContactsListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(Card card, int i) {
                EmergencyPreferences ep = EmergencyPreferences.load(emergencyContactsListView.getContext());
                try {
                    ep.removeContact(i);
                } catch (Exception e) {
                    Toast.makeText(emergencyContactsListView.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                emergencyContactCards = toCards(ep.getEmergencyContactList());
            }
        });
    }

    private Card[] toCards(List<EmergencyContact> emergencyContactList) {
        Card[] cards = new Card[emergencyContactList.size()];
        int index = 0;
        for (EmergencyContact ec : emergencyContactList) {
            cards[index++] = toCard(ec);
        }
        return cards;
    }

    private Card toCard(EmergencyContact ec) {
        SmallImageCard card = new SmallImageCard(this);
        card.setTitle(ec.name);
        card.setDescription(ec.phoneNumber);
        card.setDismissible(true);
        card.setDrawable(R.drawable.ic_person_add_white_48dp);
        return card;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_emergency_contacts, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_contact) {
            getContact();
            return true;
        } else if (id == R.id.action_set_minimum_time) {
            setMinimumEmergencyTimeStartDetectionAfterLanding();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMinimumEmergencyTimeStartDetectionAfterLanding() {
        final String[] items = new String[] {"1", "5", "10", "15"};
        final EmergencyPreferences ep = EmergencyPreferences.load(this);
        int selectedIndex = Arrays.binarySearch(items, String.valueOf((int)Math.round(ep.getMinimumTimeBeforeCall(TimeUnit.MINUTES))));
        if (selectedIndex < 0)
            selectedIndex = 1;
        new MaterialDialog.Builder(this)
                .title(R.string.set_emergency_time)
                .items(items)
                .itemsCallbackSingleChoice(selectedIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        double selectedValue = Double.parseDouble(items[which]);
                        try {
                            ep.setMinimumTimeBeforeEmergencyCall(selectedValue, TimeUnit.MINUTES);
                        } catch (Exception e) {
                            Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                })
                .show();
    }

    private static final int GET_PHONE_NUMBER = 3007;

    public void getContact() {
        startActivityForResult(new Intent(this, ContactsPickerActivity.class), GET_PHONE_NUMBER);
    }

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
                    emergencyContactCards = toCards(ep.getEmergencyContactList());
                    this.emergencyContactsListView.removeAllViews();
                    this.emergencyContactsListView.addAll(emergencyContactCards);
                    this.emergencyContactsListView.getAdapter().notifyDataSetChanged();
                }
            default:
                break;
        }
    }
}
