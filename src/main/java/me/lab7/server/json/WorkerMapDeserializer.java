package me.lab7.server.json;

import com.google.gson.*;
import me.lab7.common.models.Worker;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * A deserializer for converting a JSON object with multiple worker entries into an array of Worker objects.
 */
public class WorkerMapDeserializer implements JsonDeserializer<Worker[]> {
    /**
     * Deserializes a JSON object with multiple worker entries into an array of Worker objects.
     *
     * @param json    the JSON element to deserialize
     * @param typeOfT the type of the desired object
     * @param context the context for deserialization
     * @return an array of Worker objects
     * @throws JsonParseException if the JSON is not in the expected format
     */
    @Override
    public Worker[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObj = json.getAsJsonObject();
        int iter = jObj.size();
        Worker[] workers = new Worker[iter];
        for (Map.Entry<String, JsonElement> entry : jObj.entrySet()) {
            iter--;
            workers[iter] = context.deserialize(entry.getValue(), Worker.class);
        }
        return workers;
    }
}
