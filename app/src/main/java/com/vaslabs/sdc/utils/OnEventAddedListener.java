package com.vaslabs.sdc.utils;

public interface OnEventAddedListener {
    <T> void onEventAdded(DynamicQueue<T> queue);
}
