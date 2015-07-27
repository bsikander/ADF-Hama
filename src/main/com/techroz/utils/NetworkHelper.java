package main.com.techroz.utils;

import java.io.IOException;
import java.util.Map;

import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareMasterData;
import main.com.techroz.admm.ExchangeSolver.EVADMM.ShareSlaveData;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class NetworkHelper {
private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
    public static String shareMasterObjectToJson(ShareMasterData context) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(context);
    }

    public static String shareSlaveObjectToJson(ShareSlaveData context) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(context);
    }
    
    public static ShareMasterData jsonToShareMasterObject(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, ShareMasterData.class);
    }

    public static ShareSlaveData jsonToShareSlaveObject(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, ShareSlaveData.class);
    }
    
    public static String convertDictionaryToJson(Map<String, double[]> data) throws JsonGenerationException, JsonMappingException, IOException {
    	return OBJECT_MAPPER.writeValueAsString(data);
    }
    
    public static Map<String, double[]> convertJsonToDictionary(String data) throws JsonGenerationException, JsonMappingException, IOException {
    	return OBJECT_MAPPER.readValue(data, Map.class);
    }
}
