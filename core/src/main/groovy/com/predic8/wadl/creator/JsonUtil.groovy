package com.predic8.wadl.creator

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonUtil {
    private static class JsonUtilHolder {
        public static final JsonUtil instance = new JsonUtil();
    }

    static JsonUtil getInstance() {
        return JsonUtilHolder.instance;
    }

    Object replaceObjectWithArray(JSONObject jsonObject, String key, boolean killRecursion) throws JSONException {
        JSONArray keys =  jsonObject.names()
        for(int i=0; i<keys.length(); i++) {
            if(killRecursion)
                return jsonObject

            String current_key = keys.get(i).toString()
            if(current_key == key) {
                killRecursion = true
                return jsonObject.put(current_key,Collections.singletonList(jsonObject.get(current_key)))
            }

            if(jsonObject.get(current_key).getClass().getName() == "org.json.JSONObject")
                replaceObjectWithArray((JSONObject) jsonObject.get(current_key),key,false)

            else if(jsonObject.get(current_key).getClass().getName() == "org.json.JSONArray") {
                for(int j=0; j<((JSONArray) jsonObject.get(current_key)).length(); j++) {
                    if(((JSONArray) jsonObject.get(current_key)).get(j).getClass().getName() == "org.json.JSONObject")
                        replaceObjectWithArray((JSONObject)((JSONArray) jsonObject.get(current_key)).get(j),key,false)
                }
            }
        }
        return null
    }
}
