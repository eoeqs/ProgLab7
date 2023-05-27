package me.lab6.server.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.lab6.common.workerRelated.Worker;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * A serializer that converts a HashMap of Workers to a JSON object.
 * Each Worker object is serialized using Gson library and added to the resulting JsonObject,
 * where the key is the worker's ID and the value is the serialized worker.
 */
public class WorkerMapSerializer implements JsonSerializer<HashMap<Long, Worker>> {
    /**
     * Serializes a HashMap of Workers to a JSON object.
     *
     * @param src       the HashMap of Workers to serialize
     * @param typeOfSrc the type of the source object
     * @param context   the serialization context
     * @return the resulting JSON object
     */
    @Override
    public JsonElement serialize(HashMap<Long, Worker> src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        for (Worker w : src.values()) {
            result.add(String.valueOf(w.getId()), context.serialize(w));
        }
        return result;
    }
}
