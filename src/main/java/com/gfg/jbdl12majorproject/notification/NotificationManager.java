package com.gfg.jbdl12majorproject.notification;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;

public interface NotificationManager {
    public void send(String message) throws JsonProcessingException, UnsupportedEncodingException;
}
