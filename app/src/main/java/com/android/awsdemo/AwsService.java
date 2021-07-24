package com.android.awsdemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;

public class AwsService extends Service {

    private static final String COGNITO_POOL_ID = "eu-central-1:6888e504-258e-4a26-b81f-5f1529450ce2";
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a737e058vrwvh-ats.iot.eu-central-1.amazonaws.com";
    private static final Regions MY_REGION = Regions.EU_CENTRAL_1;
    private static final String LOG_TAG = "AwsService";
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AWSIotMqttManager mqttRemoteAwsClient;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeAwsParameter();
    }

    private void initializeAwsParameter() {
        if (credentialsProvider == null)
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    this,
                    COGNITO_POOL_ID, // Identity pool ID
                    MY_REGION // Region
            );
        mqttRemoteAwsClient = new AWSIotMqttManager("amitjaiswal", CUSTOMER_SPECIFIC_ENDPOINT);
        mqttRemoteAwsClient.setAutoResubscribe(false);
        establishRemoteConnection("user_zahir");
    }

    private void establishRemoteConnection(String clientId) {
        Log.d(LOG_TAG, "clientId = " + clientId);
        new Thread(new StartRemoteAwsMqttConnection(mqttRemoteAwsClient, credentialsProvider, (status, throwable) -> {
            Log.e(LOG_TAG, "Status = " + status);
            handleRemoteStatus(status, throwable);
        })).start();
    }

    private void handleRemoteStatus(AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus status, Throwable throwable) {
        switch (status) {
            case Connecting:
                break;
            case Connected:
                subscribeToRemoteConnection("local_amitjaiswal");
                break;
            case Reconnecting:
                break;
            case ConnectionLost:
                break;
            default:
                break;
        }
    }

    // static  methods
    public static Intent startMqttService(Context context) {
        Intent intent = new Intent(context, AwsService.class);
        context.startService(intent);
        return intent;
    }

    public static void stopMqttService(Context context) {
        context.stopService(new Intent(context, AwsService.class));
    }


    private void subscribeToRemoteConnection(String topic) {
        try {
            if (topic == null)
                return;
            Log.e(LOG_TAG, "subscribeToTopic: " + topic);
            mqttRemoteAwsClient.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                    (topic1, data) -> onRemoteMessageArrived(topic1, data));
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
    }

    private void onRemoteMessageArrived(String topic, byte[] data) {
        Log.e(LOG_TAG, topic+" - > " + new String(data).toString());
    }
}
