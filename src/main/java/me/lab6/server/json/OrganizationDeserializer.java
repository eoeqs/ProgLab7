package me.lab6.server.json;

import com.google.gson.*;
import me.lab6.server.exceptions.IncorrectWorkerFieldException;
import me.lab6.common.utility.DataType;
import me.lab6.common.models.Address;
import me.lab6.common.models.Organization;

import java.lang.reflect.Type;

/**
 * This class is a custom JSON deserializer for the Organization class.
 */
public class OrganizationDeserializer implements JsonDeserializer<Organization> {
    /**
     * Deserializes a JSON object into an Organization object.
     *
     * @param json    the JSON object to be deserialized
     * @param typeOfT the type of the desired object
     * @param context the context for deserialization that is passed to a deserializer during invocation of its {@link JsonDeserializer#deserialize(JsonElement, Type, JsonDeserializationContext)} method
     * @return an Organization object
     * @throws JsonParseException            if json is not in the expected format
     * @throws IncorrectWorkerFieldException if a required field is missing or invalid
     */
    @Override
    public Organization deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonValidator.ensureHas(jsonObject, "org_name");
        if (jsonObject.get("org_name").getAsString().isBlank()) {
            throw new IncorrectWorkerFieldException();
        }
        String orgName = jsonObject.get("org_name").getAsString();

        JsonValidator.ensureHas(jsonObject, "annual_turnover");
        String preTurnover = jsonObject.get("annual_turnover").getAsString();
        JsonValidator.ensureCorrect(DataType.INT, false, true, preTurnover);
        int turnover = Integer.parseInt(preTurnover);

        JsonValidator.ensureHas(jsonObject, "employee_count");
        String preEmpCount = jsonObject.get("employee_count").getAsString();
        JsonValidator.ensureCorrect(DataType.LONG, false, true, preEmpCount);
        long empCount = Long.parseLong(preEmpCount);

        JsonValidator.ensureHas(jsonObject, "address");
        Address address = context.deserialize(jsonObject.get("address"), Address.class);

        return new Organization(orgName, turnover, empCount, address);
    }
}
