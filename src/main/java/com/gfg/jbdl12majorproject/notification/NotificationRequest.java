package com.gfg.jbdl12majorproject.notification;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NotificationRequest {
    private String user;
    private String message;
    private NotificationType type;
}
