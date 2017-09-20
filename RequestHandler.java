import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//클라이언트의 서비스 요청에 대한 처리(분류)를 한다.
public class RequestHandler extends CommandManager {


	public RequestHandler() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	/**********************************
	 * processRequest(String request, ClientHandler clientHandler) - 요청온 값를 명령과 명령 처리커맨드로 분리
	 * request - 요청 값
	 * clientHandler - 요청 보낸 클라이언트
	**********************************/
	public void processRequest(String request, ClientHandler clientHandler) {
		try {						
			JSONParser parser = new JSONParser(); 
			JSONObject jsonObj = (JSONObject)parser.parse(request); 
			
			String cmdStr = (String)jsonObj.get("cmd"); //명령 커맨드 값
			String requestStr = null;
			try{
				JSONObject requestJson = (JSONObject)jsonObj.get("request"); //명령 디테일 값	
				requestStr =  requestJson.toString();				
			}catch(ClassCastException e){
				JSONArray requestJsonArray = (JSONArray)jsonObj.get("request"); //명령 디테일 값 (제이슨 array)
				requestStr =  requestJsonArray.toString();				
			}			

			this.execute(cmdStr, requestStr, clientHandler); //명령 실행		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
