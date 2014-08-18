package com.insightly;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

public class InsightlyRequest{
    public final String BASE_URL = "https://api.insight.ly";

    public static InsightlyRequest GET(String apikey, String path){
        return new InsightlyRequest(apikey, path, "GET");
    }

    public static InsightlyRequest DELETE(String apikey, String path){
        return new InsightlyRequest(apikey, path, "DELETE");
    }

    public static InsightlyRequest POST(String apikey, String path){
        return new InsightlyRequest(apikey, path, "POST");
    }

    public static InsightlyRequest PUT(String apikey, String path){
        return new InsightlyRequest(apikey, path, "PUT");
    }

    protected InsightlyRequest(String apikey, String path, String method){
        try{
            this.apikey = apikey;
            this.url = new URIBuilder(BASE_URL + path);
            this.method = method;
        }
        catch(URISyntaxException ex){
            throw new IllegalArgumentException("Invalid URL: " + ex.getMessage());
        }
    }

    public JSONArray asJSONArray() throws IOException{
        try{
            return verifyResponse(buildHttpRequest().asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONObject asJSONObject() throws IOException{
        try{
            return verifyResponse(buildHttpRequest().asJson()).getBody().getObject();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public String asString() throws IOException{
        try{
            return verifyResponse(buildHttpRequest().asString()).getBody();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public InsightlyRequest body(String body){
        this.body = body;
        return this;
    }

    public InsightlyRequest body(JSONObject obj){
        return this.body(obj.toString());
    }

    public InsightlyRequest body(JSONArray arr){
        return this.body(arr.toString());
    }

    public InsightlyRequest queryParam(String name, String value){
        this.url.addParameter(name, value);
        return this;
    }

    public InsightlyRequest queryParam(String name, long value){
        return this.queryParam(name, String.valueOf(value));
    }

    private HttpRequest buildHttpRequest() throws IOException{
        try{
            HttpRequest request = null;
            String url = this.url.toString();

            if(method.equals("GET")){
                request = Unirest.get(url);
            }
            else if(method.equals("PUT")){
                HttpRequestWithBody req = Unirest.put(url);
                req.header("Content-Type", "application/json");
                if(this.body != null){
                    req.body(this.body);
                }

                request = req;
            }
            else if(method.equals("DELETE")){
                request = Unirest.delete(url);
            }
            else if(method.equals("POST")){
                HttpRequestWithBody req = Unirest.post(url);
                req.header("Content-Type", "application/json");
                if(this.body != null){
                    req.body(this.body);
                }

                request = req;
            }
            else{
                throw new IOException("parameter method must be GET|DELETE|PUT|UPDATE");
            }

            request.basicAuth(apikey, "");

            return request;
        }
        catch(IOException ex){
            throw ex;
        }
    }

    private <T> HttpResponse<T> verifyResponse(HttpResponse<T> response) throws IOException{
        int status_code = response.getCode();
        if(!(status_code == 200
             || status_code == 201
             || status_code == 202)){
            throw new IOException("Server returned status code " + response.getCode());
        }

        return response;
    }

    // OData query helpers

    public InsightlyRequest orderBy(String orderby){
        return this.queryParam("$orderby", orderby);
    }

    public InsightlyRequest skip(long skip){
        return this.queryParam("$skip", skip);
    }

    public InsightlyRequest top(long top){
        return this.queryParam("$top", top);
    }

    public InsightlyRequest filter(String filter){
        return this.queryParam("$filter", filter);
    }

    public InsightlyRequest filter(List<String> filters){
        for(String filter : filters){
            this.filter(filter);
        }

        return this;
    }

    private String apikey;
    private URIBuilder url;
    private String method;
    private String body;
}
