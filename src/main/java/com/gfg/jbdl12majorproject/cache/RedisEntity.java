package com.gfg.jbdl12majorproject.cache;


import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@RedisHash("transactions")
public class RedisEntity implements Serializable {
    @Id
    String id;
    String transactionId;
    String toUserId;
    Double amount;
    String currency;
    String transactionType;
    String transactionStatus;
    String transactionMethod;
    Date createdAt;
}
