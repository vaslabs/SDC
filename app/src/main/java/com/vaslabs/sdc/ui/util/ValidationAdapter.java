package com.vaslabs.sdc.ui.util;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.utils.IValidator;

import java.util.Arrays;

/**
 * Created by vnicolao on 20/06/15.
 */
public class ValidationAdapter extends BaseAdapter {

    private final IValidator[] validators;
    private final Context context;
    private final ValidationChangeListener vcl;
    private final static int[] resources = new int[] {R.drawable.ic_warning, R.drawable.ic_error};

    public ValidationAdapter(Context context, IValidator[] validators, ValidationChangeListener validationChangeListener) {
        this.validators = Arrays.copyOf(validators, validators.length);
        this.context = context;
        this.vcl = validationChangeListener;
    }

    @Override
    public int getCount() {
        return validators.length;
    }

    @Override
    public Object getItem(int i) {
        return validators[i];
    }

    @Override
    public long getItemId(int i) {
        return validators[i].getTitle().hashCode();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final IValidator validator = validators[i];
        View viewFromTemplate = LayoutInflater.from(context).inflate(R.layout.validation_view, null);
        final ImageButton imageButton = (ImageButton) viewFromTemplate.findViewById(R.id.validationImageButton);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), validator.getMessage() + "?: Resolve the problems and click the button again", Toast.LENGTH_LONG).show();
                vcl.onValidationChanged();
            }
        });
        TextView titleTextView = (TextView) viewFromTemplate.findViewById(R.id.validationTitleTextView);
        if (validator.validate())
            imageButton.setImageResource(R.drawable.ic_success);
        else
            imageButton.setImageResource(resources[validator.getMessageType().ordinal()]);
        titleTextView.setText(validator.getTitle());
        return viewFromTemplate;
    }

}
