package com.insightly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;

public class Insightly{
    public Insightly(String apikey){
        this.apikey = apikey;
    }

    public JSONObject addContact(JSONObject contact) throws IOException{
        try{
            if(contact.has("CONTACT_ID") && (contact.getLong("CONTACT_ID") > 0)){
                return verifyResponse(generateRequest("/v2.1/Contacts", "PUT", contact.toString()).asJson()).getBody().getObject();
            }
            else{
                return verifyResponse(generateRequest("/v2.1/Contacts", "POST", contact.toString()).asJson()).getBody().getObject();
            }
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public void deleteContact(long contact_id) throws IOException{
        try{
            verifyResponse(generateRequest("/v2.1/Contacts/" + contact_id, "DELETE", "").asJson());
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONArray getContacts(Map<String, Object> options) throws IOException{
        try{
            List<String> query_strings = new ArrayList<String>();

            if(options.containsKey("email") && (options.get("email") != null)){
                String email = (String)options.get("email");
                query_strings.add("email=" + email);
            }
            if(options.containsKey("tag") && (options.get("tag") != null)){
                String tag = (String)options.get("tag");
                query_strings.add("tag=" + tag);
            }
            if(options.containsKey("ids") && (options.get("ids") != null)){
                List<Long> ids = (List<Long>)options.get("ids");
                if(ids.size() > 0){
                    StringBuilder acc = new StringBuilder();
                    for(Long id : ids){
                        acc.append(id);
                        acc.append(",");
                    }

                    query_strings.add("ids=" + acc);
                }
            }

            StringBuilder query_string = new StringBuilder();
            for(String s : query_strings){
                query_string.append(s);
                query_string.append("&");
            }

            return verifyResponse(generateRequest("/v2.1/Contacts" + query_string, "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONArray getContactEmails(long contact_id) throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/Contacts/" + contact_id + "/Emails", "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONArray getContactNotes(long contact_id) throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/Contacts/" + contact_id + "/Notes", "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONArray getContactTasks(long contact_id) throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/Contacts/" + contact_id + "/Tasks", "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONArray getCurrencies() throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/Currencies", "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONArray getUsers() throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/Users", "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public HttpRequest generateRequest(String url, String method, String data) throws IOException{
        try{
            HttpRequest request = null;
            url = BASE_URL + url;

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
        catch(IOException ex){
            throw ex;
        }
    }

    protected <T> HttpResponse<T> verifyResponse(HttpResponse<T> response) throws IOException{
        int status_code = response.getCode();
        if(!(status_code == 200
             || status_code == 201
             || status_code == 202)){
            throw new IOException("Server returned status code " + response.getCode());
        }

        return response;
    }

    public static void main(String[] args) throws Exception{
        if(args.length < 1){
            System.err.println("Please provide your api key as a command-line argument");
            System.exit(1);
        }

        Insightly insightly = new Insightly(args[0]);
        insightly.test();
        System.out.println("Tests complete!");
        System.exit(0);
    }

    public void test() throws Exception{
        test(-1);
    }

    public void test(int top) throws Exception{
        System.out.println("Testing API .....");

        int passed = 0;
        int failed = 0;

        Map<String, Object> options;

        System.out.println("Testing authentication");
        JSONArray currencies = this.getCurrencies();
        if(currencies.length() > 0){
            System.out.println("Authentication passed");
            passed += 1;
        }
        else{
            failed += 1;
        }

        // Test getUsers()
        // also get root user to use in testing write/update calls
        JSONArray users = null;
        JSONObject user = null;
        long user_id = 0;
        try{
            users = this.getUsers();
            user = users.getJSONObject(0);
            user_id = user.getLong("USER_ID");
            System.out.println("PASS: getUsers(), found " + users.length() + " users.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getUsers()");
            failed += 1;
        }

        // getContacts
        JSONObject contact = null;
        try{
            JSONArray contacts;
            options = new HashMap<String, Object>();
            options.put("order_by", "DATE_UPDATED_UTL desc");
            options.put("top", top);
            contacts = this.getContacts(options);
            contact = contacts.getJSONObject(0);
            System.out.println("PASS: getContacts(), found " + contacts.length() + " contacts.");
            passed += 1;
        }
        catch(Exception e){
            System.out.println("FAIL: getContacts()");
            failed += 1;
        }

        if(contact != null){
            long contact_id = contact.getLong("CONTACT_ID");
            try{
                JSONArray emails = this.getContactEmails(contact_id);
                System.out.println("PASS: getContactEmails(), found " + emails.length() + " emails for random contact.");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: getContactEmails()");
                failed += 1;
            }

            try{
                JSONArray notes = this.getContactNotes(contact_id);
                System.out.println("PASS: getContactNotes(), found " + notes.length() + " notes for random contact.");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: getContactNotes()");
                failed += 1;
            }

            try{
                JSONArray tasks = this.getContactTasks(contact_id);
                System.out.println("PASS: getContactTasks(), found " + tasks.length() + " tasks for random contact.");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: getContactTasks()");
                failed += 1;
            }
        }

        // Test addContact
        try{
            contact = new JSONObject();
            contact.put("SALUTATION", "Mr");
            contact.put("FIRST_NAME", "Testy");
            contact.put("LAST_NAME", "McTesterson");

            contact = this.addContact(contact);
            System.out.println("PASS: addContact()");
            passed += 1;

            try{
                this.deleteContact(contact.getLong("CONTACT_ID"));
                System.out.println("PASS: deleteContact()");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: deleteContact()");
                failed += 1;
            }
        }
        catch(Exception ex){
            contact = null;
            System.out.println("FAIL: addContact()");
            failed += 1;
        }

        if(failed != 0){
            throw new Exception(failed + " tests failed!");
        }
    }

    public final String BASE_URL = "https://api.insight.ly";

    private String apikey;
}
