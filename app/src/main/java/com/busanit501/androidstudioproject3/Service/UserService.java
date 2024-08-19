package com.busanit501.androidstudioproject3.Service;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.busanit501.androidstudioproject3.ApiClient;
import com.busanit501.androidstudioproject3.Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class UserService {
    private static final String BASE_URL = "http://your-server-ip:8080/api/users";
    private Gson gson = new Gson();

    public void getAllUsers(Context context, Response.Listener<List<User>> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, BASE_URL, null,
                response -> {
                    Type listType = new TypeToken<List<User>>() {}.getType();
                    List<User> users = gson.fromJson(response.toString(), listType);
                    listener.onResponse(users);
                },
                errorListener
        );
        ApiClient.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public void getUserById(Context context, Long id, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    User user = gson.fromJson(response.toString(), User.class);
                    listener.onResponse(user);
                },
                errorListener
        );
        ApiClient.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void createUser(Context context, User user, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        try {
            JSONObject jsonObject = new JSONObject(gson.toJson(user));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, BASE_URL, jsonObject,
                    response -> {
                        User createdUser = gson.fromJson(response.toString(), User.class);
                        listener.onResponse(createdUser);
                    },
                    errorListener
            );
            ApiClient.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(Context context, Long id, User user, Response.Listener<User> listener, Response.ErrorListener errorListener) {
        try {
            String url = BASE_URL + "/" + id;
            JSONObject jsonObject = new JSONObject(gson.toJson(user));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, url, jsonObject,
                    response -> {
                        User updatedUser = gson.fromJson(response.toString(), User.class);
                        listener.onResponse(updatedUser);
                    },
                    errorListener
            );
            ApiClient.getInstance(context).addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteUser(Context context, Long id, Response.Listener<Void> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE, url, null,
                response -> listener.onResponse(null),
                errorListener
        );
        ApiClient.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}

