/*
 * Copyright (c) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moto.actions.doze;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.moto.actions.MotoActionsSettings;
import com.moto.actions.SensorAction;
import com.moto.actions.SensorHelper;

public class GlanceSensor implements ScreenStateNotifier {
    private static final String TAG = "MotoActions-GlanceSensor";

    private final MotoActionsSettings mMotoActionsSettings;
    private final SensorHelper mSensorHelper;
    private final SensorAction mSensorAction;
    
    private final Sensor mSensor;
    private final Sensor mApproachSensor;

    private boolean mEnabled;

    public GlanceSensor(MotoActionsSettings motoActionsSettings, SensorHelper sensorHelper,
                SensorAction action) {
        mMotoActionsSettings = motoActionsSettings;
        mSensorHelper = sensorHelper;
        mSensorAction = action;

        mSensor = sensorHelper.getGlanceSensor();
        mApproachSensor = sensorHelper.getApproachGlanceSensor();
    }

    @Override
    public void screenTurnedOn() {
        if (mEnabled) {
            Log.d(TAG, "Disabling");
            mSensorHelper.unregisterListener(mGlanceListener);
            mSensorHelper.unregisterListener(mApproachGlanceListener);
            mEnabled = false;
        }
    }

    @Override
    public void screenTurnedOff() {
        if (mMotoActionsSettings.isPickUpEnabled() && !mEnabled) {
            Log.d(TAG, "Enabling");
            mSensorHelper.registerListener(mSensor, mGlanceListener);
            mSensorHelper.registerListener(mSensor, mApproachGlanceListener);
            mEnabled = true;
        }
    }

    private SensorEventListener mGlanceListener = new SensorEventListener() {
        @Override
        public synchronized void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "Changed");
            mSensorAction.action();
        }

        @Override
        public void onAccuracyChanged(Sensor mSensor, int accuracy) {
        }
    };

    private SensorEventListener mApproachGlanceListener = new SensorEventListener() {
        @Override
        public synchronized void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "Approach: Changed");
            mSensorAction.action();
        }

        @Override
        public void onAccuracyChanged(Sensor mSensor, int accuracy) {
        }
    };
}
