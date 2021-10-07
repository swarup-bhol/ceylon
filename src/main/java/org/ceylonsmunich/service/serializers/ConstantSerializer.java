package org.ceylonsmunich.service.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.ceylonsmunich.service.entity.Constant;

import java.io.IOException;

public class ConstantSerializer extends StdSerializer<Constant> {
    public ConstantSerializer(Class<Constant> t) {
        super(t);
    }

    public ConstantSerializer() {
        this(null);
    }

    @Override
    public void serialize(Constant constant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(String.valueOf(constant.getKey()), constant.getValue());
        jsonGenerator.writeEndObject();
    }
}
