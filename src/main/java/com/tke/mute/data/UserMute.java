package com.tke.mute.data;

import lombok.Data;

import java.util.UUID;

@Data
public class UserMute {
    UUID uuid;
    long time;
    String staffer,reason;
}
