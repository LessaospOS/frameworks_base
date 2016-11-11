/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.android.systemui.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;

import com.android.systemui.utils.leaks.Tracker;
import com.android.systemui.SysuiTestCase;

public class TestableContext extends ContextWrapper {

    private final FakeContentResolver mFakeContentResolver;
    private final FakeSettingsProvider mSettingsProvider;

    private Tracker mReceiver;
    private Tracker mService;
    private Tracker mComponent;

    public TestableContext(Context base, SysuiTestCase test) {
        super(base);
        mFakeContentResolver = new FakeContentResolver(base);
        ContentProviderClient settings = base.getContentResolver()
                .acquireContentProviderClient(Settings.AUTHORITY);
        mSettingsProvider = FakeSettingsProvider.getFakeSettingsProvider(settings,
                mFakeContentResolver);
        mFakeContentResolver.addProvider(Settings.AUTHORITY, mSettingsProvider);
        mReceiver = test.getTracker("receiver");
        mService = test.getTracker("service");
        mComponent = test.getTracker("component");
    }

    public FakeSettingsProvider getSettingsProvider() {
        return mSettingsProvider;
    }

    @Override
    public FakeContentResolver getContentResolver() {
        return mFakeContentResolver;
    }

    @Override
    public Context getApplicationContext() {
        // Return this so its always a TestableContext.
        return this;
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (mReceiver != null) mReceiver.getLeakInfo(receiver).addAllocation(new Throwable());
        return super.registerReceiver(receiver, filter);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter,
            String broadcastPermission, Handler scheduler) {
        if (mReceiver != null) mReceiver.getLeakInfo(receiver).addAllocation(new Throwable());
        return super.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }

    @Override
    public Intent registerReceiverAsUser(BroadcastReceiver receiver, UserHandle user,
            IntentFilter filter, String broadcastPermission, Handler scheduler) {
        if (mReceiver != null) mReceiver.getLeakInfo(receiver).addAllocation(new Throwable());
        return super.registerReceiverAsUser(receiver, user, filter, broadcastPermission,
                scheduler);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (mReceiver != null) mReceiver.getLeakInfo(receiver).clearAllocations();
        super.unregisterReceiver(receiver);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (mService != null) mService.getLeakInfo(conn).addAllocation(new Throwable());
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean bindServiceAsUser(Intent service, ServiceConnection conn, int flags,
            Handler handler, UserHandle user) {
        if (mService != null) mService.getLeakInfo(conn).addAllocation(new Throwable());
        return super.bindServiceAsUser(service, conn, flags, handler, user);
    }

    @Override
    public boolean bindServiceAsUser(Intent service, ServiceConnection conn, int flags,
            UserHandle user) {
        if (mService != null) mService.getLeakInfo(conn).addAllocation(new Throwable());
        return super.bindServiceAsUser(service, conn, flags, user);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        if (mService != null) mService.getLeakInfo(conn).clearAllocations();
        super.unbindService(conn);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        if (mComponent != null) mComponent.getLeakInfo(callback).addAllocation(new Throwable());
        super.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        if (mComponent != null) mComponent.getLeakInfo(callback).clearAllocations();
        super.unregisterComponentCallbacks(callback);
    }
}
