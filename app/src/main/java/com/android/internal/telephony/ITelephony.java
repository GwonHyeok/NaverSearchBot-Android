package com.android.internal.telephony;

import android.os.IInterface;

public abstract interface ITelephony extends IInterface {
    public abstract boolean disableDataConnectivity();

    public abstract boolean enableDataConnectivity();
}