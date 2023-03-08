package com.example.demo.domain.recommender;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gorse.gorse4j.Feedback;
import io.gorse.gorse4j.Item;
import io.gorse.gorse4j.RowAffected;
import io.gorse.gorse4j.User;
import lombok.extern.log4j.Log4j2;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This file has been copyd from gorse.io. The linter issues are not a point of concern, since they are usually hidden
 * behind a gradle project :)
 */
@Log4j2
public class Gorse {

    private final String endpoint;
    private final String apiKey;

    public Gorse(String endpoint, String apiKey) {
        this.endpoint = endpoint;
        this.apiKey = apiKey;
    }

    public RowAffected insertUser(User user) throws IOException {
        return this.request("POST", this.endpoint + "/api/user", user, RowAffected.class);
    }

    public User getUser(String userId) throws IOException {
        return this.request("GET", this.endpoint + "/api/user/" + userId, null, User.class);
    }

    public RowAffected deleteUser(String userId) throws IOException {
        return this.request("DELETE", this.endpoint + "/api/user/" + userId, null, RowAffected.class);
    }

    public RowAffected insertItem(Item item) throws IOException {
        return this.request("POST", this.endpoint + "/api/item", item, RowAffected.class);
    }

    public Item getItem(String itemId) throws IOException {
        return this.request("GET", this.endpoint + "/api/item/" + itemId, null, Item.class);
    }

    public RowAffected deleteItem(String itemId) throws IOException {
        return this.request("DELETE", this.endpoint + "/api/item/" + itemId, null, RowAffected.class);
    }

    public RowAffected insertFeedback(List<Feedback> feedbacks) throws IOException {
        return this.request("POST", this.endpoint + "/api/feedback", feedbacks, RowAffected.class);
    }

    public List<Feedback> listFeedback(String userId, String feedbackType) throws IOException {
        return List.of(this.request("GET", this.endpoint + "/api/user/" + userId + "/feedback/" + feedbackType, null, Feedback[].class));
    }

    public List<String> getRecommend(String userId, int pageLength, int page) throws IOException {
        return getRecommend(userId, pageLength, page, 0);
    }

    public List<String> getRecommend(String userId, int pageLength, int page, int initialOffset) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("n", String.valueOf(pageLength));
        parameters.put("offset", String.valueOf(pageLength * page + initialOffset));
        log.info(String.format("Sending request to gorse with parameters: n(%d) offset(%s)", pageLength, parameters.get("offset")));
        return List.of(this.request("GET", this.endpoint + "/api/recommend/" + userId, null, String[].class, parameters));
    }

    private <Request, Response> Response request(String method, String url, Request request, Class<Response> responseClass) throws IOException {
        return request(method, url, request, responseClass, new HashMap<>());
    }

    private <Request, Response> Response request(String method, String url, Request request, Class<Response> responseClass, Map<String, String> parameters) throws IOException {
        StringBuilder paramterList = new StringBuilder();
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
            if (paramterList.isEmpty()) {
                paramterList.append("?");
            } else {
                paramterList.append("&");
            }
            paramterList.append(parameter.getKey());
            paramterList.append("=");
            paramterList.append(parameter.getValue());
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url + paramterList).openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("X-API-Key", this.apiKey);
        connection.setRequestProperty("Content-Type", "application/json");
        // Send request
        ObjectMapper mapper = new ObjectMapper();
        if (request != null) {
            connection.setDoOutput(true);
            String requestBody = mapper.writeValueAsString(request);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(requestBody.getBytes());
            outputStream.close();
        }
        // Get Response
        InputStream inputStream = connection.getInputStream();
        return mapper.readValue(inputStream, responseClass);
    }
}
