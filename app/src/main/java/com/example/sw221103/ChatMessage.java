package com.example.sw221103;

public class ChatMessage {
    private String nickname ;
    private String message ;
    private String imageUrl;

    public ChatMessage(String nickname, String message, String imageUrl) {
        this.nickname = nickname;
        this.message = message;
        this.imageUrl = imageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public String getMessage() {
        return message;
    }

    public String getImageUrl() {
        return imageUrl;
    }


}

