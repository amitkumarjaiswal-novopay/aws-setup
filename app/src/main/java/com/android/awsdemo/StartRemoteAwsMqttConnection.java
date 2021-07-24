
package com.android.awsdemo;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;


public class StartRemoteAwsMqttConnection implements Runnable {
    private final AWSIotMqttManager mqttRemoteAwsClient;
    private final AWSIotMqttClientStatusCallback awsIotMqttClientStatusCallback;
    private final CognitoCachingCredentialsProvider credentialsProvider;

    public StartRemoteAwsMqttConnection(AWSIotMqttManager mqttRemoteAwsClient, CognitoCachingCredentialsProvider credentialsProvider, AWSIotMqttClientStatusCallback awsIotMqttClientStatusCallback) {
        this.mqttRemoteAwsClient = mqttRemoteAwsClient;
        this.awsIotMqttClientStatusCallback = awsIotMqttClientStatusCallback;
        this.credentialsProvider = credentialsProvider;
    }

    public void run() {
        serverConnection();
    }

    private void serverConnection() {
        try {
            mqttRemoteAwsClient.connect(credentialsProvider, awsIotMqttClientStatusCallback);
        } catch (Exception ex) {
        }
    }
}