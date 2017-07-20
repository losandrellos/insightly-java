package com.insightly;

import com.mashape.unirest.http.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;

public class InsightlyException extends Exception {
    private HttpResponse response;

    public InsightlyException() {
    }

    public InsightlyException(String message) {
        super(message);
    }

    public InsightlyException(String message, Throwable cause) {
        super(message, cause);
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public boolean hasResponse() {
        return response != null;
    }

    public boolean isServerError() {
        return hasResponse() &&
                (response.getStatus() > 499 && response.getStatus() < 600);
    }

    public boolean isUnreachable() {
        return getCause() instanceof ConnectTimeoutException;
    }
}

