package me.lab6.server.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.lab6.common.models.Worker;

import java.lang.reflect.Type;

/**
 * Serializer class for converting a {@link Worker} object to a JSON object using the Gson library.
 */
public class WorkerSerializer implements JsonSerializer<Worker> {
    /**
     * Serializes a given {@link Worker} object to a JSON object using the Gson library.
     *
     * @param src The worker object to be serialized.
     * @param srcType The type of the worker object.
     * @param context The serialization context.
     * @return A JSON object representing the serialized worker object.
     */
    public JsonElement serialize(Worker src, Type srcType, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("name", src.getName());
        result.addProperty("ID", src.getId());
        if (src.getPosition() != null) {
            result.addProperty("position", src.getPosition().toString());
        }
        if (src.getStatus() != null) {
            result.addProperty("status", src.getStatus().toString());
        }
        result.addProperty("salary", src.getSalary());
        result.addProperty("creation_date", src.getCreationDate().toString());
        result.addProperty("start_date", src.getStartDate().toString());
        result.add("coordinates", context.serialize(src.getCoordinates()));
        result.add("organization", context.serialize(src.getOrganization()));
        return result;
    }
}
