package com.setpace.springrest;

import lombok.Getter;

@Getter
public class BasicAuth {
    private String username;
    private String password;

    public BasicAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
