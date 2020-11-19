package com.eikholm.websocketprep.model;

public class WebSocketCoordinate {
    public final String letterCoordinate;
    public final int numberCoordinate;
    public final String fromWho;

    public WebSocketCoordinate(String letterCoordinate, int numberCoordinate, String fromWho) {
        this.letterCoordinate = letterCoordinate;
        this.numberCoordinate = numberCoordinate;
        this.fromWho = fromWho;
    }
}
