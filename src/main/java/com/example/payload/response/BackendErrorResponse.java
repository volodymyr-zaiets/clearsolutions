package com.example.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class BackendErrorResponse {
    private String title;
    private String message;
    private Map<String, List<String>> messages;

    public BackendErrorResponse(String title, String message, Map<String, List<String>> messages) {
        this.title = title;
        this.message = message;
        this.messages = messages;
    }
}
