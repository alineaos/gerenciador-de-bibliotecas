package io.github.alineaos.librarymanager.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CpfSerializer extends JsonSerializer<String> {

    public final static String CPF_REGEX = "(\\d{3})(\\d{3})(\\d{3})(\\d{2})";
    public final static String CPF_MASK = "$1.$2.$3-$4";

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (s != null){
            String formattedCpf = s.replaceAll(CPF_REGEX, CPF_MASK);
            jsonGenerator.writeString(formattedCpf);
        }
    }
}
