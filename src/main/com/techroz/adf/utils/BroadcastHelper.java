package main.com.techroz.adf.utils;

import java.io.IOException;
import java.util.Map;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class BroadcastHelper {
private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    public static String convertDictionaryToJson(Map<String, double[]> data) throws JsonGenerationException, JsonMappingException, IOException {
    	return OBJECT_MAPPER.writeValueAsString(data);
    }
    
    public static Map<String, double[]> convertJsonToDictionary(String data) throws JsonGenerationException, JsonMappingException, IOException {
    	return OBJECT_MAPPER.readValue(data, new TypeReference<Map<String, double[]>>(){});
    }
}
