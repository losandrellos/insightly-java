package com.insightly;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * <p>
 * Class for building requests for the Insightly REST API.
 * </p>
 * <p>
 * <p>
 * <strong>NOTICE:</string> At this time,
 * this class is primarily intended for internal use
 * and is subject to change without notice.
 * Use at your own risk.
 * </p>
 * <p>
 * <p>
 * Requests are constructed via the following factory methods:
 * </p>
 * <p>
 * <ul>
 * <li>{@link #GET(String, String) GET}</li>
 * <li>{@link #PUT(String, String) PUT}</li>
 * <li>{@link #POST(String, String) POST}</li>
 * <li>{@link #DELETE(String, String) DELETE}</li>
 * </ul>
 * <p>
 * <p>
 * To actually send the request to the server,
 * use one of the {@code as[Type]} methods.
 * This will return the response as the appropriate type
 * (or throw an {@link java.io.IOException IOException} if there was an error).
 * </p>
 * <p>
 * <p>
 * Most methods return the {@link InsightlyRequest} object,
 * so method calls can be chained.
 * </p>
 */
public class InsightlyRequest {
    public final String BASE_URL = "https://api.insight.ly";

    /**
     * <p>
     * Constructs a GET request
     * </p>
     *
     * @param apikey User's api key
     * @param path   Path portion of URL
     */
    public static InsightlyRequest GET(String apikey, String path) {
        return new InsightlyRequest(apikey, path, "GET");
    }

    /**
     * <p>
     * Constructs a DELETE request
     * </p>
     *
     * @param apikey User's api key
     * @param path   Path portion of URL
     */
    public static InsightlyRequest DELETE(String apikey, String path) {
        return new InsightlyRequest(apikey, path, "DELETE");
    }

    /**
     * <p>
     * Constructs a POST request
     * </p>
     *
     * @param apikey User's api key
     * @param path   Path portion of URL
     */
    public static InsightlyRequest POST(String apikey, String path) {
        return new InsightlyRequest(apikey, path, "POST");
    }

    /**
     * <p>
     * Constructs a PUT request
     * </p>
     *
     * @param apikey User's api key
     * @param path   Path portion of URL
     */
    public static InsightlyRequest PUT(String apikey, String path) {
        return new InsightlyRequest(apikey, path, "PUT");
    }

    protected InsightlyRequest(String apikey, String path, String method) {
        try {
            this.apikey = apikey;
            this.url = new URIBuilder(BASE_URL + path);
            this.method = method;
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL: " + ex.getMessage());
        }
    }

    /**
     * Sends request to server and returns response
     * as {@link org.json.JSONArray JSONArray}.
     *
     * @return response from server
     */
    public JSONArray asJSONArray() throws InsightlyException {
        try {
            return verifyResponse(buildHttpRequest().asJson()).getBody().getArray();
        } catch (UnirestException ex) {
            throw new InsightlyException(ex.getMessage(), ex);
        } catch (IOException e) {
            throw new InsightlyException("Cannot build the request", e);
        }
    }

    /**
     * Sends request to server and returns response
     * as {@link org.json.JSONObject JSONObject}.
     *
     * @return response from server
     */
    public JSONObject asJSONObject() throws InsightlyException {
        try {
            return verifyResponse(buildHttpRequest().asJson()).getBody().getObject();
        } catch (UnirestException ex) {
            throw new InsightlyException(ex.getMessage(), ex);
        } catch (IOException e) {
            throw new InsightlyException("Cannot build the request", e);
        }
    }

    /**
     * Sends request to server and returns response
     * as {@link java.lang.String String}
     *
     * @return response from server
     */
    public String asString() throws InsightlyException {
        try {
            return verifyResponse(buildHttpRequest().asString()).getBody();
        } catch (UnirestException ex) {
            throw new InsightlyException(ex.getMessage(), ex);
        } catch (IOException e) {
            throw new InsightlyException("Cannot build the request", e);
        }
    }

    /**
     * Sets the body of the request to provided string
     *
     * @param body {@link java.lang.String String} containtng body contents
     */
    public InsightlyRequest body(String body) {
        this.body = body;
        return this;
    }

    /**
     * Sets the body of the request to provided JSON object
     *
     * @param obj {@link org.json.JSONObject JSONObject} containtng body contents
     */
    public InsightlyRequest body(JSONObject obj) {
        return this.body(obj.toString());
    }

    /**
     * Sets the body of the request to provided JSON array
     *
     * @param arr {@link org.json.JSONArray JSONArray} containtng body contents
     */
    public InsightlyRequest body(JSONArray arr) {
        return this.body(arr.toString());
    }

    /**
     * Adds a query parameter to the request URL
     *
     * @param name  name of parameter
     * @param value value of parameter
     */
    public InsightlyRequest queryParam(String name, String value) {
        this.url.addParameter(name, value);
        return this;
    }

    /**
     * Adds a query parameter to the request URL
     *
     * @param name  name of parameter
     * @param value value of parameter
     */
    public InsightlyRequest queryParam(String name, long value) {
        return this.queryParam(name, String.valueOf(value));
    }

    private HttpRequest buildHttpRequest() throws IOException {
        try {
            HttpRequest request = null;
            String url = this.url.toString();

            if (method.equals("GET")) {
                request = Unirest.get(url);
            } else if (method.equals("PUT")) {
                HttpRequestWithBody req = Unirest.put(url);
                req.header("Content-Type", "application/json");
                if (this.body != null) {
                    req.body(this.body);
                }

                request = req;
            } else if (method.equals("DELETE")) {
                request = Unirest.delete(url);
            } else if (method.equals("POST")) {
                HttpRequestWithBody req = Unirest.post(url);
                req.header("Content-Type", "application/json");
                if (this.body != null) {
                    req.body(this.body);
                }

                request = req;
            } else {
                throw new IOException("parameter method must be GET|DELETE|PUT|UPDATE");
            }

            request.basicAuth(apikey, "");

            return request;
        } catch (IOException ex) {
            throw ex;
        }
    }

    private <T> HttpResponse<T> verifyResponse(HttpResponse<T> response) throws InsightlyException {
        int status_code = response.getStatus();
        if (!(status_code == 200
                || status_code == 201
                || status_code == 202)) {
            InsightlyException insightlyException = new InsightlyException("Server returned status code " + response.getStatus());
            insightlyException.setResponse(response);
            throw insightlyException;
        }

        return response;
    }

    // OData query helpers

    public InsightlyRequest orderBy(String orderby) {
        return this.queryParam("$orderby", orderby);
    }

    public InsightlyRequest skip(long skip) {
        return this.queryParam("$skip", skip);
    }

    public InsightlyRequest top(long top) {
        return this.queryParam("$top", top);
    }

    public InsightlyRequest filter(String filter) {
        return this.queryParam("$filter", filter);
    }

    public InsightlyRequest filter(List<String> filters) {
        for (String filter : filters) {
            this.filter(filter);
        }

        return this;
    }

    private String apikey;
    private URIBuilder url;
    private String method;
    private String body;
}
