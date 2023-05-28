package me.lab6.server.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.lab6.common.models.Address;

import java.lang.reflect.Type;

/**
 * A serializer for the Address class, used to convert Address objects into JSON data.
 */

public class AddressSerializer implements JsonSerializer<Address> {
    /**
     * Serializes an Address object into a JsonElement.
     *
     * @param src     the Address object to serialize.
     * @param srcType the type of the Address object.
     * @param context the context in which the serialization is happening.
     * @return a JsonElement representing the serialized Address object.
     */
    @Override
    public JsonElement serialize(Address src, Type srcType, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("zipCode", src.zipCode());
        result.addProperty("street", src.street());
        return result;
    }
}
