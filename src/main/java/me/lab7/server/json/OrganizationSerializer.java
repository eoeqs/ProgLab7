package me.lab7.server.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import me.lab7.common.models.Organization;

import java.lang.reflect.Type;

/**
 * This class implements the JsonSerializer interface for the Organization class.
 * It provides a way to serialize Organization objects into JSON format.
 */
public class OrganizationSerializer implements JsonSerializer<Organization> {
    /**
     * Serializes an Organization object into a JsonElement.
     *
     * @param src the Organization object to serialize
     * @param srcType the type of the source object
     * @param context the context for serialization that can be used to serialize further elements
     * @return a JsonElement representing the serialized Organization object
     */
    public JsonElement serialize(Organization src, Type srcType, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("org_name", src.fullName());
        result.addProperty("annual_turnover", src.annualTurnover());
        result.addProperty("employee_count", src.employeesCount());
        result.add("address", context.serialize(src.postalAddress()));
        return result;
    }
}
