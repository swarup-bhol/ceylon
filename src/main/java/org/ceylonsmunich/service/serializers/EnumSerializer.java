package org.ceylonsmunich.service.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.ceylonsmunich.service.entity.EnumRef;

import java.io.IOException;

public class EnumSerializer extends StdSerializer<EnumRef> {

    public EnumSerializer(Class<EnumRef> t) {
        super(t);
    }

    public EnumSerializer() {
        this(null);
    }

    @Override
    public void serialize(EnumRef enumRef, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectFieldStart(enumRef.getEnumId());
        enumRef.getConstants().forEach((o)->{
            try {
                jsonGenerator.writeStringField(String.valueOf(o.getKey()),o.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
    }
}
