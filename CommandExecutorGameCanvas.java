import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//게임 방 채팅 명령 실행자 - 상대방이 잘 받는지 채크할 필요 없음
public class CommandExecutorGameCanvas implements CommandExecutor {	
	private String jsonStr; //방에 있는 클라이언트들에게 보낼 명령
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 채팅 내용
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
//			if((boolean)map.get("clear")){ //클리어 명령 했으면 레디스에서 삭제
//				String hashesKey = clientHandler.gameRoom_.key_ + "Canvas";
//				JedisHandler jedisHandler = new JedisHandler();
//				jedisHandler.jedisDel(hashesKey);				
//			}
			
			//jsonStr 레디스에 저장 (방 key에 맞춰서 순서대로)		
			JedisHandler jedisHandler = new JedisHandler();
			String hashesKey = clientHandler.gameRoom_.key_ + "Canvas" + clientHandler.gameRoom_.totalQuizNum_;
			long fieldNum = jedisHandler.jedisHlen(hashesKey); //해당 유저 hashes에 저장할 fieldNum 받아오기
			jedisHandler.jedisHset(hashesKey, String.valueOf(fieldNum), jsonStr);			
			////////////////////////////////////////
			
			ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_;
						
			ServerLog.getInstance().log(this.getClass().getName(), 
					clientHandler.account_.id_ + "의 위치 : " + clientHandler.gameRoom_.name_);
			
			Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
			while(iterator.hasNext()) {
				String id = (String)iterator.next();
				ClientHandler handler = chm.getClientHandler(id);
				if(handler != clientHandler){ //본인이 아닌 클라이언트에게 Paint 전송
    				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 gameCanvas 명령을 수행합니다.");
					handler.sendResponse(jsonStr);		
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}
	/**********************************
	 * decoding(String msg) - json형식 명령 디테일을 풀어주는 기능
	 * msg - json형식의 명령 디테일
	**********************************/
	public HashMap<String, Object> decoding(String request) throws Exception {
		JSONParser parser = new JSONParser(); 
		JSONObject jsonObj = (JSONObject)parser.parse(request); 
		
		HashMap<String, Object> map = new HashMap<String, Object>();
        switch (((String)jsonObj.get("type"))){ //canvas type: pen, eraser, canvas
            case "pen": //pen path 정보
        		String[] keys1 = {"type", "motion", "x", "y"};
        		Object[] values1 = {"pen", jsonObj.get("motion"), 
        				jsonObj.get("x"), jsonObj.get("y")};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys1, values1); 
    			map.put("clear", false);
                break;

            case "toPen": //pen 으로 변경
        		String[] keys2 = {"type"};
        		Object[] values2 = {"toPen"};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys2, values2); 
    			map.put("clear", false);
                break;

            case "toEraser": //eraser 으로 변경
        		String[] keys3 = {"type"};
        		Object[] values3 = {"toEraser"};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys3, values3); 
    			map.put("clear", false);
                break;

            case "canvasColor": //canvas 색상 변경
        		String[] keys4 = {"type", "color"};
        		Object[] values4 = {"canvasColor", jsonObj.get("color")};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys4, values4); 
    			map.put("clear", false);
                break;
            	
            case "penColor": //펜 색상 변경
        		String[] keys5 = {"type", "color"};
        		Object[] values5 = {"penColor", jsonObj.get("color")};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys5, values5); 
    			map.put("clear", false);
            	break;
            	
            case "penWidth": //펜 두깨 변경
        		String[] keys6 = {"type", "width"};
        		Object[] values6 = {"penWidth", jsonObj.get("width")};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys6, values6); 
    			map.put("clear", false);
            	break;
                
            case "undo": //되돌리기
        		String[] keys7 = {"type"};
        		Object[] values7 = {"undo"};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys7, values7); 
    			map.put("clear", false);
            	break;
            	
            case "clear": //모두 지우기
        		String[] keys8 = {"type"};
        		Object[] values8 = {"clear"};
        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys8, values8); 
    			map.put("clear", true);
            	break;
            	
//            case "canvasResize": //캔버스 사이즈 설정
//        		String[] keys9 = {"type",  "width", "height"};
//        		Object[] values9 = {"canvasResize", jsonObj.get("width"), jsonObj.get("height")};
//        		jsonStr = JsonEncode.getInstance().encodeCommandJson("gameCanvas", keys9, values9); 
//    			map.put("clear", false);
//            	break;
        }
		return map;
	}
}
