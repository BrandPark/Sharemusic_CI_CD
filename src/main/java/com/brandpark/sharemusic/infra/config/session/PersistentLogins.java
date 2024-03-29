package com.brandpark.sharemusic.infra.config.session;

import jdk.jfr.Timestamp;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity @Getter
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(nullable = false, length = 64)
    @Timestamp
    private LocalDateTime lastUsed;
}
