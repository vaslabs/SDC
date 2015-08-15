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

import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.controller.MaterialListAdapter;
import com.dexafree.materialList.model.Card;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.utils.IValidator;

import java.util.Arrays;

/**
 * Created by vnicolao on 20/06/15.
 */
public class ValidationAdapter extends MaterialListAdapter {

    private final IValidator[] validators;
    private final Context context;
    private final ValidationChangeListener vcl;
    private final static int[] resources = new int[] {R.drawable.ic_warning, R.drawable.ic_error};

    public ValidationAdapter(Context context, IValidator[] validators, ValidationChangeListener validationChangeListener) {
        this.validators = Arrays.copyOf(validators, validators.length);
        this.context = context;
        this.vcl = validationChangeListener;
        for (IValidator validator : validators) {
            SmallImageCard card = toCard(validator, context);
            this.add(card);
            validator.attachCard(card);
        }
    }

    public static SmallImageCard toCard(IValidator validator, Context context) {
        SmallImageCard card = new SmallImageCard(context);
        card.setDescription(validator.getMessage().toString());
        card.setTitle(validator.getTitle().toString());
        int drawable_id;
        if (validator.validate())
            drawable_id = R.drawable.ic_success;
        else
            drawable_id = resources[validator.getMessageType().ordinal()];
        card.setDrawable(drawable_id);
        
        return card;
    }

    public int getCount() {
        return validators.length;
    }

    public Object getItem(int i) {
        return validators[i];
    }

    @Override
    public long getItemId(int i) {
        return validators[i].getTitle().hashCode();
    }


}
