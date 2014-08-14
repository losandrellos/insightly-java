package com.insightly;

import java.io.IOException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

public class Insightly{
    public Insightly(String apikey){
        this.apikey = apikey;
    }

    public HttpRequest generateRequest(String url, String method, String data) throws IOException, UnirestException{
        HttpRequest request = null;

        if(method.equals("GET")){
            request = Unirest.get(url);
        }
        else if(method.equals("PUT")){
            HttpRequestWithBody req = Unirest.put(url);
            req.header("Content-Type", "application/json");
            req.body(data);

            request = req;
        }
        else if(method.equals("DELETE")){
            request = Unirest.delete(url);
        }
        else if(method.equals("POST")){
            HttpRequestWithBody req = Unirest.post(url);
            req.header("Content-Type", "application/json");
            req.body(data);

            request = req;
        }
        else{
            throw new IOException("parameter method must be GET|DELETE|PUT|UPDATE");
        }

        request.basicAuth(apikey, "");

        return request;
    }

    private String apikey;
}
