package me.lab6.server.json;

import com.google.gson.*;
import me.lab6.common.utility.DataType;
import me.lab6.common.models.Coordinates;

import java.lang.reflect.Type;

/**
 * A utility class for deserializing Coordinates objects from JSON.
 */
public class CoordinatesDeserializer implements JsonDeserializer<Coordinates> {
    /**
     * Deserializes a Coordinates object from JSON.
     *
     * @param json    the JsonElement to deserialize.
     * @param typeOfT the type of the object to deserialize to.
     * @param context the context in which the deserialization is happening.
     * @return the deserialized Coordinates object.
     * @throws JsonParseException if the JSON is not valid.
     */
    @Override
    public Coordinates deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        double coordinateX = 0.0;
        if (jsonObject.has("x")) {
            String preCoordinateX = jsonObject.get("x").getAsString();
            JsonValidator.ensureCorrect(DataType.DOUBLE, false, false, preCoordinateX);
            coordinateX = Double.parseDouble(preCoordinateX);
        }

        JsonValidator.ensureHas(jsonObject, "y");
        String preCoordinateY = jsonObject.get("y").getAsString();
        JsonValidator.ensureCorrect(DataType.DOUBLE, false, false, preCoordinateY);
        Double coordinateY = Double.parseDouble(preCoordinateY);

        return new Coordinates(coordinateX, coordinateY);
    }
}
