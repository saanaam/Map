package com.example.sanamyavarpour.map.Service.Model;

public class Error {

    private int statuscode;
    private String message;

    public Error() {
    }

    public Error(int statuscode, String message) {
        this.statuscode = statuscode;
        this.message = message;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static class SoketError extends Error {
        public SoketError() {

        }

    }

    public static class IoError extends Error {
        public IoError() {
            super();
        }
    }

    public static class HttpError extends Error {
        public HttpError() {
        }

        public HttpError(int statuscode, String message) {
            super(statuscode, message);
        }
    }
}
