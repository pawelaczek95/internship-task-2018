package pl.codewise.internships;

import java.time.LocalDateTime;

public class Message {

    private final String userAgent;
    private final int errorCode;
    private final LocalDateTime timeStamp;

    public Message(String userAgent, int errorCode) {
        this.userAgent = userAgent;
        this.errorCode = errorCode;
        this.timeStamp = LocalDateTime.now();
    }

    public Message(String userAgent, int code, LocalDateTime timeStamp) {
        this.userAgent = userAgent;
        this.errorCode = code;
        this.timeStamp = timeStamp;
    }


    public String getUserAgent() {
        return userAgent;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    @Override
    public Message clone() {
        return new Message(userAgent, errorCode, timeStamp);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Message && ((Message) o).getUserAgent().equals(userAgent) && errorCode == ((Message) o).errorCode && timeStamp.isEqual(((Message) o).getTimeStamp());
    }

    @Override
    public int hashCode(){
        return (userAgent.hashCode() + errorCode + timeStamp.hashCode()) / 3;
    }

    @Override
    public String toString(){
        return "[" + userAgent + "; " + errorCode + "; " + timeStamp.toString() + "]";
    }
}
