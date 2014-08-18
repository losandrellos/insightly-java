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

    public JSONObject getContact(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Contacts/" + id).asJSONObject();
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

    public JSONArray getCountries() throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/Countries", "GET", "").asJson()).getBody().getArray();
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

    public JSONArray getCustomFields() throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/CustomFields", "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONObject getCustomField(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/CustomFields/" + id).asJSONObject();
    }

    public JSONArray getEmails(Map<String, Object> options) throws IOException{
        try{
            String query_string = buildQueryString(buildODataQuery(options));
            return verifyResponse(generateRequest("/v2.1/Emails" + query_string, "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONObject getEmail(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Emails/" + id).asJSONObject();
    }

    public void deleteEmail(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Emails/" + id).asString();
    }

    public JSONArray getEmailComments(long email_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Emails/" + email_id + "/Comments").asJSONArray();
    }

    public JSONObject addCommentToEmail(long email_id, String body, long owner_user_id) throws IOException{
        JSONObject data = new JSONObject();
        data.put("BODY", body);
        data.put("OWNER_USER_ID", owner_user_id);

        return InsightlyRequest.POST(apikey, "/v2.1/Emails/" + email_id + "/Comments")
            .body(data)
            .asJSONObject();
    }

    public JSONObject getEvent(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Events/" + id).asJSONObject();
    }

    public JSONArray getEvents() throws IOException{
        return getEvents(null);
    }

    public JSONArray getEvents(Map<String, Object> options) throws IOException{
        InsightlyRequest request = InsightlyRequest.GET(apikey, "/v2.1/Events");
        if(options != null){
            buildODataQuery(request, options);
        }
        return request.asJSONArray();
    }

    public JSONObject addEvent(JSONObject event) throws IOException{
        InsightlyRequest request = null;
        if(event.has("EVENT_ID") && (event.getLong("EVENT_ID") > 0)){
            long event_id = event.getLong("EVENT_ID");
            request = InsightlyRequest.PUT(apikey, "/v2.1/Events");
        }
        else{
            request = InsightlyRequest.POST(apikey, "/v2.1/Events");
        }

        return request.body(event).asJSONObject();
    }

    public void deleteEvent(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Events/" + id).asString();
    }

    public JSONArray getFileCategories() throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/FileCategories").asJSONArray();
    }

    public JSONObject getFileCategory(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/FileCategories/" + id).asJSONObject();
    }

    public JSONObject addFileCategory(JSONObject category) throws IOException{
        InsightlyRequest request = null;
        if(category.has("CATEGORY_ID") && (category.getLong("CATEGORY_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, "/v2.1/FileCategories");
        }
        else{
            request = InsightlyRequest.POST(apikey, "/v2.1/FileCategories");
        }

        return request.body(category).asJSONObject();
    }

    public void deleteFileCategory(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/FileCategories/" + id);
    }

    public JSONArray getNotes() throws IOException{
        return this.getNotes(null);
    }

    public JSONArray getNotes(Map<String, Object> options) throws IOException{
        InsightlyRequest request = InsightlyRequest.GET(apikey, "/v2.1/Notes");

        if(options != null){
            buildODataQuery(request, options);
        }

        return request.asJSONArray();
    }

    public JSONObject getNote(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Notes/" + id).asJSONObject();
    }

    public JSONObject addNote(JSONObject note) throws IOException{
        InsightlyRequest request = null;

        if(note.has("NOTE_ID") && (note.getLong("NOTE_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, "/v2.1/Notes");
        }
        else{
            request = InsightlyRequest.POST(apikey, "/v2.1/Notes");
        }

        return request.body(note).asJSONObject();
    }

    public JSONArray getNoteComments(long note_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Notes/" + note_id + "/Comments").asJSONArray();
    }

    public JSONObject addNoteComment(long note_id, JSONObject comment) throws IOException{
        String url_path = "/v2.1/Notes/" + note_id + "/Comments";
        return InsightlyRequest.POST(apikey, url_path).body(comment).asJSONObject();
    }

    public JSONArray getOpportunities() throws IOException{
        return this.getOpportunities(null);
    }

    public JSONArray getOpportunities(Map<String, Object> options) throws IOException{
        InsightlyRequest request = InsightlyRequest.GET(apikey, "/v2.1/Opportunities");
        buildODataQuery(request, options);
        return request.asJSONArray();
    }

    public JSONObject getOpportunity(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Opportunities/" + id).asJSONObject();
    }

    public void deleteNote(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Notes/" + id).asString();
    }

    public JSONObject addOpportunity(JSONObject opportunity) throws IOException{
        String url_path = "/v2.1/Opportunities";
        InsightlyRequest request = null;

        if(opportunity.has("OPPORTUNITY_ID") && (opportunity.getLong("OPPORTUNITY_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, url_path);
        }
        else{
            request = InsightlyRequest.POST(apikey, url_path);
        }

        return request.body(opportunity).asJSONObject();
    }

    public void deleteOpportunity(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Opportunities/" + id).asString();
    }

    public JSONArray getOpportunityCategories() throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/OpportunityCategories").asJSONArray();
    }

    public JSONObject getOpportunityCategory(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/OpportunityCategries/" + id).asJSONObject();
    }

    public JSONObject addOpportunityCategory(JSONObject category) throws IOException{
        String url_path = "/v2.1/OpportunityCategories";
        InsightlyRequest request = null;

        if(category.has("OPPORTUNITY_ID") && (category.getLong("OPPORTUNITY_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, url_path);
        }
        else{
            request = InsightlyRequest.POST(apikey, url_path);
        }

        return request.body(category).asJSONObject();
    }

    public void deleteOpportunityCategory(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/OpportunityCategories/" + id).asString();
    }

    public JSONArray getOpportunityEmails(long opportunity_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Opportunities/" + opportunity_id + "/Emails").asJSONArray();
    }

    public JSONArray getOpportunityNotes(long opportunity_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Opportunities/" + opportunity_id + "/Notes").asJSONArray();
    }

    public JSONArray getOpportunityStateHistory(long opportunity_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Opportunities/" + opportunity_id + "/StateHistory").asJSONArray();
    }

    public JSONArray getOpportunityStateReasons() throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/OpportunityStateReasons").asJSONArray();
    }

    public JSONArray getOpportunityTasks(long opportunity_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Opportunities/" + opportunity_id + "/Tasks").asJSONArray();
    }

    public JSONArray getOrganizations() throws IOException{
        return this.getOrganizations(null);
    }

    public JSONArray getOrganizations(Map<String, Object> options) throws IOException{
        InsightlyRequest request = InsightlyRequest.GET(apikey, "/v2.1/Organisations");
        buildODataQuery(request, options);
        return request.asJSONArray();
    }

    public JSONObject getOrganization(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Organisations/" + id).asJSONObject();
    }

    public JSONObject addOrganization(JSONObject organization) throws IOException{
        String url_path = "/v2.1/Organisations";
        InsightlyRequest request = null;

        if(organization.has("ORGANISATION_ID") && (organization.getLong("ORGANISATION_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, url_path);
        }
        else{
            request = InsightlyRequest.POST(apikey, url_path);
        }

        return request.body(organization).asJSONObject();
    }

    public void deleteOrganization(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Organisations/" + id).asString();
    }

    public JSONArray getOrganizationEmails(long organization_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Organisations/" + organization_id + "/Emails").asJSONArray();
    }

    public JSONArray getOrganizationNotes(long organization_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Organisations/" + organization_id + "/Notes").asJSONArray();
    }

    public JSONArray getOrganizationTasks(long organization_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Organisations/" + organization_id + "/Tasks").asJSONArray();
    }

    public JSONArray getPipelines() throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Pipelines").asJSONArray();
    }

    public JSONObject getPipeline(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Pipelines/" + id).asJSONObject();
    }

    public JSONArray getPipelineStages() throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/PipelineStages").asJSONArray();
    }

    public JSONObject getPipelineStage(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/PipelineStages/" + id).asJSONObject();
    }

    public JSONArray getProjects() throws IOException{
        return this.getProjects(null);
    }

    public JSONArray getProjects(Map<String, Object> options) throws IOException{
        InsightlyRequest request = InsightlyRequest.GET(apikey, "/v2.1/Projects");

        if(options != null){
            buildODataQuery(request, options);
        }

        return request.asJSONArray();
    }

    public JSONObject getProject(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Projects/" + id).asJSONObject();
    }

    public JSONObject addProject(JSONObject project) throws IOException{
        String url_path = "/v2.1/Projects";
        InsightlyRequest request = null;

        if(project.has("PROJECT_ID") && (project.getLong("PROJECT_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, url_path);
        }
        else{
            request = InsightlyRequest.POST(apikey, url_path);
        }

        return request.body(project).asJSONObject();
    }

    public void deleteProject(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Projects/" + id).asString();
    }

    public JSONArray getProjectEmails(long project_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Projects/" + project_id + "/Emails").asJSONArray();
    }

    public JSONArray getProjectNotes(long project_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Projects/" + project_id + "/Notes").asJSONArray();
    }

    public JSONArray getProjectTasks(long project_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Projects/" + project_id + "/Tasks").asJSONArray();
    }

    public JSONArray getProjectCategories() throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/ProjectCategories").asJSONArray();
    }

    public JSONObject getProjectCategory(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/ProjectCategories/" + id).asJSONObject();
    }

    public JSONObject addProjectCategory(JSONObject category) throws IOException{
        String url_path = "/v2.1/ProjectCategories";
        InsightlyRequest request = null;
        if(category.has("CATEGORY_ID") && (category.getLong("CATEGORY_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, url_path);
        }
        else{
            request = InsightlyRequest.POST(apikey, url_path);
        }

        return request.body(category).asJSONObject();
    }

    public void deleteProjectCategory(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/ProjectCategories/" + id).asString();
    }

    public JSONArray getRelationships() throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Relationships").asJSONArray();
    }

    public JSONArray getTags(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Tags/" + id).asJSONArray();
    }

    public JSONArray getTasks() throws IOException{
        return this.getTasks(null);
    }

    public JSONArray getTasks(Map<String, Object> options) throws IOException{
        InsightlyRequest request = InsightlyRequest.GET(apikey, "/v2.1/Tasks");
        return buildODataQuery(request, options).asJSONArray();
    }

    public JSONObject getTask(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Tasks/" + id).asJSONObject();
    }

    public JSONObject addTask(JSONObject task) throws IOException{
        String url_path = "/v2.1/Tasks";
        InsightlyRequest request = null;

        if(task.has("TASK_ID") && (task.getLong("TASK_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, url_path);
        }
        else{
            request = InsightlyRequest.POST(apikey, url_path);
        }

        return request.body(task).asJSONObject();
    }

    public void deleteTask(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Tasks/" + id).asString();
    }

    public JSONArray getTaskComments(long task_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Tasks/" + task_id + "/Comments").asJSONArray();
    }

    public JSONObject addTaskComment(long task_id, JSONObject comment) throws IOException{
        // Not implemented yet.
        return null;
    }

    public JSONArray getTeams() throws IOException{
        return this.getTeams(null);
    }

    public JSONArray getTeams(Map<String, Object> options) throws IOException{
        InsightlyRequest request = InsightlyRequest.GET(apikey, "/v2.1/Teams");
        return buildODataQuery(request, options).asJSONArray();
    }

    public JSONObject getTeam(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Teams/" + id).asJSONObject();
    }

    public JSONObject addTeam(JSONObject team) throws IOException{
        String url_path = "/v2.1/Teams";
        InsightlyRequest request = null;

        if(team.has("TEAM_ID") && (team.getLong("TEAM_ID") > 0)){
            request = InsightlyRequest.PUT(apikey, url_path);
        }
        else{
            request = InsightlyRequest.POST(apikey, url_path);
        }

        return request.body(team).asJSONObject();
    }

    public void deleteTeam(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/Teams/" + id).asString();
    }

    public JSONArray getTeamMembers(long team_id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/TeamMembers/teamid=" + team_id).asJSONArray();
    }

    public JSONObject getTeamMember(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/TeamMembers/" + id).asJSONObject();
    }

    public JSONObject addTeamMember(JSONObject team_member) throws IOException{
        return InsightlyRequest.POST(apikey, "/v2.1/TeamMembers").body(team_member).asJSONObject();
    }

    public void deleteTeamMember(long id) throws IOException{
        InsightlyRequest.DELETE(apikey, "/v2.1/TeamMembers/" + id).asString();
    }

    public JSONObject updateTeamMember(JSONObject team_member) throws IOException{
        return InsightlyRequest.PUT(apikey, "/v2.1/TeamMembers").body(team_member).asJSONObject();
    }

    public JSONArray getUsers() throws IOException{
        try{
            return verifyResponse(generateRequest("/v2.1/Users", "GET", "").asJson()).getBody().getArray();
        }
        catch(UnirestException ex){
            throw new IOException(ex.getMessage());
        }
    }

    public JSONObject getUser(long id) throws IOException{
        return InsightlyRequest.GET(apikey, "/v2.1/Users/" + id).asJSONObject();
    }

    private InsightlyRequest buildODataQuery(InsightlyRequest request, Map<String, Object> options){
        if(options == null){
            return request;
        }

        if(options.containsKey("top") && (options.get("top") != null)){
            long top = ((Number)options.get("top")).longValue();
            if(top > 0){
                request.top(top);
            }
        }
        if(options.containsKey("skip") && (options.get("skip") != null)){
            long skip = ((Number)options.get("skip")).longValue();
            if(skip > 0){
                request.skip(skip);
            }
        }
        if(options.containsKey("orderby") && (options.get("orderby") != null)){
            String orderby = (String)options.get("orderby");
            request.orderBy(orderby);
        }
        if(options.containsKey("filters") && (options.get("filters") != null)){
            List<String> filters = (List<String>)options.get("filters");
            request.filter(filters);
        }

        return request;
    }

    protected List<String> buildODataQuery(Map<String, Object> options){
        List<String> query_strings = new ArrayList<String>();
        if(options.containsKey("top") && (options.get("top") != null)){
            long top = ((Number)options.get("top")).longValue();
            if(top > 0){
                query_strings.add("$top=" + top);
            }
        }
        if(options.containsKey("skip") && (options.get("skip") != null)){
            long skip = ((Number)options.get("skip")).longValue();
            if(skip > 0){
                query_strings.add("$skip=" + skip);
            }
        }
        if(options.containsKey("orderby") && (options.get("orderby") != null)){
            String orderby = (String)options.get("orderby");
            // TODO:  encode orderby
            query_strings.add("$orderby=" + orderby);
        }
        if(options.containsKey("filters") && (options.get("filters") != null)){
            List<String> filters = (List<String>)options.get("filters");
            for(String f : filters){
                // TODO: encode f
                query_strings.add("$filter=" + f);
            }
        }

        return query_strings;
    }

    protected String buildQueryString(List<String> query_strings){
        StringBuilder query_string = new StringBuilder();
        if(query_strings.size() > 0){
            boolean first = true;
            for(String s : query_strings){
                if(!first){
                    query_string.append("&");
                    first = false;
                }

                query_string.append(s);
            }
        }

        return query_string.toString();
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

        // Test getCountries()
        try{
            JSONArray countries = this.getCountries();
            System.out.println("PASS: getCountries(), found " + countries.length() + " countries.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getCountries");
            failed += 1;
        }

        // Test getCurrencies()
        try{
            currencies = this.getCurrencies();
            System.out.println("PASS: getCurrencies(), found " + currencies.length() + " currencies.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getCurrencies()");
            failed += 1;
        }

        // Test getCustomFields()
        try{
            JSONArray custom_fields = this.getCustomFields();
            System.out.println("PASS: getCustomFields(), found " + custom_fields.length() + " custom fields.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getCustomFields()");
            failed += 1;
        }

        // Test getEmails()
        try{
            options = new HashMap<String, Object>();
            options.put("top", top);
            JSONArray emails = this.getEmails(options);
            System.out.println("PASS: getEmails(), found " + emails.length() + " emails.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getEmails()");
            failed += 1;
        }

        // TODO:  Test getEmail()

        // Test getEvents()
        try{
            options = new HashMap<String, Object>();
            options.put("top", top);
            JSONArray events = this.getEvents(options);
            System.out.println("PASS: getEvents(), found " + events.length() + " events.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getEvents()");
            failed += 1;
        }

        // Test addEvent()
        JSONObject event = null;
        try{
            JSONObject new_event = new JSONObject();
            new_event.put("TITLE", "Text Event");
            new_event.put("LOCATION", "Somewhere");
            new_event.put("DETAILS", "Details");
            new_event.put("START_DATE_UTC", "2014-07-12 12:00:00");
            new_event.put("END_DATE_UTC", "2014-07-12 13:00:00");
            new_event.put("OWNER_USER_ID", user_id);
            new_event.put("ALL_DAY", false);
            new_event.put("PUBLICLY_VISIBLE", true);
            event = this.addEvent(new_event);
            System.out.println("PASS: addEvent()");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: addEvent()");
            ex.printStackTrace();
            failed += 1;
        }

        // Test deleteEvent()
        if(event != null){
            try{
                this.deleteEvent(event.getLong("EVENT_ID"));
                System.out.println("PASS: deleteEvent()");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: deleteEvent()");
                failed += 1;
            }
        }

        // Test getFileCategories()
        try{
            JSONArray categories = this.getFileCategories();
            System.out.println("PASS: getFileCategories(), found " + categories.length() + " file categories.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getFileCategories()");
            failed += 1;
        }

        // Test addFileCategory()
        JSONObject category = null;
        try{
            category = new JSONObject();
            category.put("CATEGORY_NAME", "Test Category");
            category.put("ACTIVE", true);
            category.put("BACKGROUND_COLOR", "000000");
            category = this.addFileCategory(category);
            System.out.println("PASS: addFileCategory()");
            passed += 1;
        }
        catch(Exception ex){
            category = null;
            System.out.println("FAIL: addFileCategory()");
            failed += 1;
        }

        // Test deleteFileCategory()
        try{
            if(category != null){
                this.deleteFileCategory(category.getLong("CATEGORY_ID"));
                System.out.println("PASS: deleteFileCategory()");
                passed += 1;
            }
        }
        catch(Exception ex){
            System.out.println("FAIL: deleteFileCategory()");
            failed += 1;
        }

        // Test getNotes()
        try{
            JSONArray notes = this.getNotes();
            System.out.println("PASS: getNotes(), found " + notes.length() + " notes.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getNotes()");
            failed += 1;
        }

        // Test getOpportunities
        JSONObject opportunity = null;
        try{
            options = new HashMap<String, Object>();
            options.put("orderby", "DATE_UPDATED_UTC");
            options.put("top", top);
            JSONArray opportunities = this.getOpportunities(options);
            if(opportunities.length() > 0){
                opportunity = (JSONObject)opportunities.get(0);
            }
            System.out.println("PASS: getOpportunities(), found " + opportunities.length() + " opportunities.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getOpportunities()");
            ex.printStackTrace();
            failed += 1;
        }

        // Test getOpportunityCategories()
        try{
            JSONArray categories = this.getOpportunityCategories();
            System.out.println("PASS: getOpportunityCategories(), found " + categories.length() + " categories.");
            passed += 1;
        }
        catch(IOException ex){
            System.out.println("FAIL: getOpportunityCategories()");
            failed += 1;
        }

        // Test addOpportunityCategory()
        try{
            category = new JSONObject();
            category.put("CATEGORY_NAME", "Test Category");
            category.put("ACTIVE", true);
            category.put("BACKGROUND_COLOR", "000000");

            category = this.addOpportunityCategory(category);
            System.out.println("PASS: addOpportunityCategory()");
            passed += 1;

            this.deleteOpportunityCategory(category.getLong("CATEGORY_ID"));
            System.out.println("PASS: deleteOpportunityCategory()");
            passed += 1;            
        }
        catch(Exception ex){
            System.out.println("FAIL: addOpportunityCategory()");
            failed += 1;
            System.out.println("FAIL: deleteOpportunityCategory()");
            failed += 1;
        }

        if(opportunity != null){
            long opportunity_id = opportunity.getLong("OPPORTUNITY_ID");

            // Test getOpportunityEmails()
            try{
                JSONArray emails = this.getOpportunityEmails(opportunity_id);
                System.out.println("PASS: getOpportunityEmails(), found " + emails.length() + " emails.");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: getOpportunityEmails()");
                failed += 1;
            }

            //Test getOpportunityNotes()
            try{
                JSONArray notes = this.getOpportunityNotes(opportunity_id);
                System.out.println("PASS: getOpportunityNotes(), found " + notes.length() + " notes.");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: getOpportunityNotes()");
                failed += 1;
            }

            // Test getOpportunityTasks()
            try{
                JSONArray tasks = this.getOpportunityTasks(opportunity_id);
                System.out.println("PASS: getOpportunityTasks(), found " + tasks.length() + " tasks.");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: getOpportunityTasks()");
                failed += 1;
            }

            // Test getOpportunityStateHistory()
            try{
                JSONArray states = this.getOpportunityStateHistory(opportunity_id);
                System.out.println("PASS: getOpportunityStateHistory(), found " + states.length() + " states in history.");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: getOpportunityStateHistory()");
                failed += 1;
            }
        }

        // Test getOpportunityStateReasons()
        try{
            JSONArray reasons = this.getOpportunityStateReasons();
            System.out.println("PASS: getOpportunityStateReasons(), found " + reasons.length() + " reasons.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getOpportunityStateReasons()");
            failed += 1;
        }

         // Test getOrganizations()
        try{
            options = new HashMap<String, Object>();
            options.put("top", top);
            options.put("orderby", "DATE_UPDATED_UTC desc");
            JSONArray organizations = this.getOrganizations(options);
            System.out.println("PASS: getOrganizations(), found " + organizations.length() + " organizations");
            passed += 1;

            if(organizations.length() > 0){
                JSONObject organization = organizations.getJSONObject(0);
                long organization_id = organization.getLong("ORGANISATION_ID");

                // Test getOrganizationEmails()
                try{
                    JSONArray emails = this.getOrganizationEmails(organization_id);
                    System.out.println("PASS: getOrganizationEmails(), found " + emails.length() + " emails.");
                    passed += 1;
                }
                catch(IOException ex){
                    System.out.println("FAIL: getOrganizationEmails()");
                    failed += 1;
                }

                // Test getOrganizationNotes()
                try{
                    JSONArray notes = this.getOrganizationNotes(organization_id);
                    System.out.println("PASS: getOrganizationNotes(), found " + notes.length() + " emails.");
                    passed += 1;
                }
                catch(Exception ex){
                    System.out.println("FAIL: getOrganizationNotes()");
                    failed += 1;
                }

                // Test getOrganizationTasks()
                try{
                    JSONArray tasks = this.getOrganizationTasks(organization_id);
                    System.out.println("PASS: getOrganizationTasks(), found " + tasks.length() + " tasks.");
                    passed += 1;
                }
                catch(Exception ex){
                    System.out.println("FAIL: getOrganizationTasks()");
                    failed += 1;
                }
            }
        }
        catch(Exception ex){
            System.out.println("FAIL: getOrganizations()");
            ex.printStackTrace();
            failed += 1;
        }

        // Test addOrganization()
        try{
            JSONObject organization = new JSONObject();
            organization.put("ORGANISATION_NAME", "Foo Corp");
            organization.put("BACKGROUND", "Details");
            organization = this.addOrganization(organization);
            System.out.println("PASS: addOrganization()");
            passed += 1;

            this.deleteOrganization(organization.getLong("ORGANISATION_ID"));
            System.out.println("PASS: deleteOrganization()");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: addOrganization()");
            ex.printStackTrace();
            failed += 1;
        }

        // Test getPipelines()
        try{
            JSONArray pipelines = this.getPipelines();
            System.out.println("PASS: getPipelines(), found " + pipelines.length() + " pipelines.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getPilelines()");
            failed += 1;
        }

        // Test getPipelineStages()
        try{
            JSONArray stages = this.getPipelineStages();
            System.out.println("PASS: getPipelineStages(), found " + stages.length() + " stages.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getPipelineStages()");
            failed += 1;
        }

        // Test getProjects()
        try{
            options = new HashMap<String, Object>();
            options.put("top", top);
            options.put("orderby", "DATE_UPDATED_UTC desc");
            JSONArray projects = this.getProjects(options);
            System.out.println("PASS: getProjects(), found " + projects.length() + " projects.");
            passed += 1;

            if(projects.length() > 0){
                JSONObject project = projects.getJSONObject(0);
                long project_id = project.getLong("PROJECT_ID");

                // Test getProjectEmails()
                try{
                    JSONArray emails = this.getProjectEmails(project_id);
                    System.out.println("PASS: getProjectEmails(), found " + emails.length() + " projects.");
                    passed += 1;
                }
                catch(Exception ex){
                    System.out.println("FAIL: getProjectEmails()");
                    failed += 1;
                }

                // Test getProjectNotes()
                try{
                    JSONArray notes = this.getProjectNotes(project_id);
                    System.out.println("PASS: getProjectNotes(), found " + notes.length() + " notes.");
                    passed += 1;
                }
                catch(Exception ex){
                    System.out.println("FAIL: getProjectNotes()");
                    failed += 1;
                }

                // Test getProjectTasks()
                try{
                    JSONArray tasks = this.getProjectTasks(project_id);
                    System.out.println("PASS: getProjectTasks(), found " + tasks.length() + " tasks.");
                    passed += 1;
                }
                catch(Exception ex){
                    System.out.println("FAIL: getProjectTasks()");
                    failed += 1;
                }
            }
        }
        catch(Exception ex){
            System.out.println("FAIL: getProjects()");
            failed += 1;
        }

        // Test getProjectCategories()
        try{
            JSONArray categories = this.getProjectCategories();
            System.out.println("PASS: getProjectCategories(), found " + categories.length() + " categories.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getProjectCategories()");
            failed += 1;
        }

        // Test addProjectCategory()
        try{
            category = new JSONObject();
            category.put("CATEGORY_NAME", "Test Category");
            category.put("ACTIVE", true);
            category.put("BACKGROUND_COLOR", "000000");
            category = this.addProjectCategory(category);
            System.out.println("PASS: addProjectCategory()");
            passed += 1;

            // Test deleteProjectCategory()
            try{
                this.deleteProjectCategory(category.getLong("CATEGORY_ID"));
                System.out.println("PASS: deleteProjectCategory()");
                passed += 1;
            }
            catch(Exception ex){
                System.out.println("FAIL: deleteProjectCategory()");
                failed += 1;
            }
        }
        catch(Exception ex){
            System.out.println("FAIL: addProjectCategory()");
            failed += 1;
        }

        // Test getRelationships()
        try{
            JSONArray relationships = this.getRelationships();
            System.out.println("PASS: getRelationships(), found " + relationships.length() + " relationships.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getRelationships()");
            failed += 1;
        }

        // Test getTasks()
        try{
            options = new HashMap<String, Object>();
            options.put("top", top);
            options.put("orderby", "DUE_DATE desc");
            JSONArray tasks = this.getTasks(options);
            System.out.println("PASS: getTasks(), found " + tasks.length() + " tasks.");
            passed += 1;
        }
        catch(Exception ex){
            System.out.println("FAIL: getTasks()");
            failed += 1;
        }

        // Test getTeams()
        try{
            JSONArray teams = this.getTeams();
            System.out.println("PASS: getTeams(), found " + teams.length() + " teams.");
            passed += 1;

            if(teams.length() > 0){
                JSONObject team = teams.getJSONObject(0);
                long team_id = team.getLong("TEAM_ID");
                try{
                    JSONArray team_members = this.getTeamMembers(team_id);
                    System.out.println("PASS: getTeamMembers(), found " + team_members.length() + " team members.");
                    passed += 1;
                }
                catch(Exception ex){
                    System.out.println("FAIL: getTeamMembers()");
                    failed += 1;
                }
            }
        }
        catch(Exception ex){
            System.out.println("FAIL: getTeams()");
            failed += 1;
        }

        if(failed != 0){
            throw new Exception(failed + " tests failed!");
        }
   }

    public final String BASE_URL = "https://api.insight.ly";

    private String apikey;
}
