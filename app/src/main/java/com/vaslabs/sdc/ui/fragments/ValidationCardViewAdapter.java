package com.vaslabs.sdc.ui.fragments;

import com.vaslabs.sdc.types.ISummaryEntry;
import com.vaslabs.sdc.ui.R;
import com.vaslabs.sdc.utils.IValidator;

/**
 * Created by vnicolaou on 21/12/15.
 */
public class ValidationCardViewAdapter extends CardViewAdapter{

    public ValidationCardViewAdapter(IValidator[] validators) {
        summaryEntries = new ISummaryEntry[validators.length];
        toSummaryEntries(validators);
    }

    private void toSummaryEntries(IValidator[] validators) {
        for (int i = 0; i < validators.length; i++) {
            summaryEntries[i] = new ValidatorSummary(validators[i]);
        }
    }

    @Override
    public int getItemCount() {
        return summaryEntries.length;
    }

    private class ValidatorSummary implements ISummaryEntry {
        final IValidator validator;
        public ValidatorSummary(IValidator validator) {
            this.validator = validator;
        }

        @Override
        public String getTitle() {
            return this.validator.getTitle().toString();
        }

        @Override
        public String getContent() {
            return validator.getMessage().toString();
        }

        @Override
        public int getDrawable() {
            if (validator.validate())
                return R.drawable.ic_success;
            switch (validator.getMessageType()) {
                case ERROR:
                    return R.drawable.ic_error;
                case WARNING:
                    return R.drawable.ic_warning;
            }
            return R.drawable.ic_warning;
        }
    }
}
