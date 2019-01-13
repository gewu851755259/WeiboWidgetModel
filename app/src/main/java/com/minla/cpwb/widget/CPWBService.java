package com.minla.cpwb.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class CPWBService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CPWBViewFactory(this, intent);
    }
}
