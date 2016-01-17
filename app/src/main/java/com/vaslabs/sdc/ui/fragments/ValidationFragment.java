package com.vaslabs.sdc.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.vaslabs.sdc.SkydivingSessionService;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.utils.BarometerValidator;
import com.vaslabs.sdc.utils.EmergencyContactValidator;
import com.vaslabs.sdc.utils.IValidator;
import com.vaslabs.sdc.utils.LocationValidator;
import com.vaslabs.sdc.utils.WifiValidator;

/**
 * Created by vnicolaou on 21/12/15.
 */
public class ValidationFragment extends CardViewFragment{

    @Override
    public void special() {
        final Context context = recyclerView.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        FloatingActionButton startNewSessionFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.fab_start_new);
        final Activity parentActivity = getActivity();
        IValidator[] validators = new IValidator[4];
        validators[0] = BarometerValidator.getInstance(context);
        validators[1] = LocationValidator.getInstance(context);
        validators[2] = WifiValidator.getInstance(context);
        validators[3] = EmergencyContactValidator.getInstance(context);

        ValidationCardViewAdapter recycleAdapter = new ValidationCardViewAdapter(validators);
        recyclerView.setAdapter(recycleAdapter);
        startNewSessionFloatingActionButton.setImageResource(R.drawable.ic_play_dark);
        startNewSessionFloatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SkydivingSessionService.class);
                context.startService(intent);
                Toast.makeText(context, context.getString(R.string.session_started_msg), Toast.LENGTH_LONG).show();
                parentActivity.finish();
            }
        });
    }

}
