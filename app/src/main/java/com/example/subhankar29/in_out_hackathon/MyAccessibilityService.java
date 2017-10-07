package com.example.subhankar29.in_out_hackathon;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private AccessibilityServiceInfo info;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }
        List<AccessibilityNodeInfo> findAccessibilityNodeInfosByViewId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            findAccessibilityNodeInfosByViewId = source.findAccessibilityNodeInfosByViewId("com.whatsapp");
        }
        if (findAccessibilityNodeInfosByViewId.size() > 0) {
            AccessibilityNodeInfo parent = (AccessibilityNodeInfo) findAccessibilityNodeInfosByViewId.get(0);
            // You can also traverse the list if required data is deep in view hierarchy.
            String requiredText = parent.getText().toString();
            Log.i("Required Text", requiredText);
        }

    }

    @Override
    public void onInterrupt() {

    }

    @Override 
    public void onServiceConnected() {
        // Set the type of events that this service wants to listen to. Others won't be passed to this service. 
        // We are only considering windows state changed event. 
        info.eventTypes = AccessibilityEvent.TYPE_WINDOWS_CHANGED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        // If you only want this service to work with specific applications, set their package names here. Otherwise, when the service is activated, it will listen to events from all applications. 
        info.packageNames = new String[] {"com.example.android.myFirstApp", "com.example.android.mySecondApp"};
        // Set the type of feedback your service will provide. We are setting it to GENERIC.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        // Default services are invoked only if no package-specific ones are present for the type of AccessibilityEvent generated. 
        // This is a general-purpose service, so we will set some flags 
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS; info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY; info.flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        // We are keeping the timeout to 0 as we don’t need any delay or to pause our accessibility events 
        info.notificationTimeout = 0;
        this.setServiceInfo(info);
    }


}
