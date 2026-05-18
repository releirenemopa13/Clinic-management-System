package com.healthcare.model;

public class ChatBotResponse {
    private String response;
    private String source;
    
    public ChatBotResponse() {}
    
    public ChatBotResponse(String response, String source) {
        this.response = response;
        this.source = source;
    }
    
    public String getResponse() {
        return response;
    }
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
}
