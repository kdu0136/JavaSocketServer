import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonEncode {
    public static JsonEncode jsonEncode_;

    /**********************************
     * JsonControl() - 각 값 초기화
     **********************************/
    private JsonEncode(){
    }

    /**********************************
     * getInstance() - 객체를 생성하고 반환하는 함수
     **********************************/
    public static JsonEncode getInstance(){
        if(jsonEncode_ == null)
            jsonEncode_ = new JsonEncode();

        return jsonEncode_;
    }

    /**********************************
     * encodeCommandJson(String cmd, String[] keys, Object[] values) - 받은 값을 json 형태로 변환
     * cmd - 명령 커맨드
     * keys - 명령 디테일 키값
     * keys - 명령 디테일 값
     **********************************/
    public String encodeCommandJson(String cmd, String[] keys, Object[] values){
		JSONObject json = new JSONObject(); 
		JSONObject requestJson = new JSONObject(); 

        for(int i = 0; i < keys.length; i++){
            requestJson.put(keys[i], values[i]);
        }
        json.put("cmd", cmd);
        json.put("request", requestJson);
				
		return json.toString();
    }
    
    public String encodeCommandJson(String cmd, JSONArray jsonArray){
		JSONObject json = new JSONObject(); 
        json.put("cmd", cmd);
        json.put("request", jsonArray);
				
		return json.toString();
    }
}
