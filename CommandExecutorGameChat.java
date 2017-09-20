import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//게임 방 채팅 명령 실행자 - 상대방이 잘 받는지 채크할 필요 없음
public class CommandExecutorGameChat implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 채팅 내용
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			HashMap<String, Object> map = decoding(request); //받은 명령 디테일 Json 값을 목적에 맞게 풀어줌
			String chatMsg = (String)map.get("msg"); //채팅 내용

			boolean quizCorrect = false; //퀴즈 정답 맞춘 유무
			if(clientHandler.gameRoom_.isStart_){ //게임 진행 중 채팅 내용
				if(!clientHandler.gameRoom_.quizClientHandler.account_.id_.equals(clientHandler.account_.id_)){ //자신이 출제자가 아닐 경우
					if(clientHandler.gameRoom_.quiz_.equals(chatMsg)){ //정답을 이야기 한 경우
						clientHandler.gameRoom_.totalQuizNum_++; //총 퀴즈 수 증가
						if(clientHandler.gameRoom_.numQuiz_ == clientHandler.gameRoom_.maxQuiz_){ //진행 한 퀴즈 수가 총 문제 갯수가 되면 게임 종료 알림
							clientHandler.gameRoom_.numQuiz_ = 0; //진행 한 퀴즈 수 초기화		
							clientHandler.gameRoom_.isStart_ = false; //게임 대기 중으로 변경
						    String[] keys = {"totalNum", "type"};
						    Object[] values = {clientHandler.gameRoom_.totalQuizNum_, "allEnd"};
						    String jsonStr = JsonEncode.getInstance().encodeCommandJson("endGameRoom", keys, values); //방에 있는 클라이언트에게 타임 아웃으로 새 게임 알림

							ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //현재 입장한 방 client 목록									
							Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
							while(iterator.hasNext()) {
								String id = (String)iterator.next();
								ClientHandler handler = chm.getClientHandler(id);
								handler.account_.isReady_ = false;
								ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 endGameRoom 명령을 수행합니다.");
								handler.sendResponse(jsonStr);		
							}				
						}else{ //아직 출제 할 퀴즈가 남아 있으면
							quizCorrect = true;					
					        int randNum = new Random().nextInt(GameRoom.QUIZ_LIST.length);
					        String quiz = GameRoom.QUIZ_LIST[randNum];
							clientHandler.gameRoom_.quiz_ = quiz; //게임방 퀴즈 정답 설정	  
							clientHandler.gameRoom_.quizClientHandler = clientHandler; //새로운 퀴즈 출제자로 변경  
							clientHandler.gameRoom_.numQuiz_++; //진행 한 퀴즈 수 증가
						}
					}					
				}
			}
			
            String[] keys = {"nick", "msg", "quiz", "numQuiz", "maxQuiz", "totalNum"};
            Object[] values = {clientHandler.account_.nick_, chatMsg, clientHandler.gameRoom_.quiz_,
            		clientHandler.gameRoom_.numQuiz_, clientHandler.gameRoom_.maxQuiz_, clientHandler.gameRoom_.totalQuizNum_};
            String jsonStr = JsonEncode.getInstance().encodeCommandJson("gameChat", keys, values); //방에 있는 클라이언트들에게 보낼 채팅을 json으로 변환
            String jsonStr0 = JsonEncode.getInstance().encodeCommandJson("quizCorrect", keys, values); //방에 있는 클라이언트들에게 정답자 닉네임 보냄
			
			ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_;
						
			ServerLog.getInstance().log(this.getClass().getName(), 
					clientHandler.account_.id_ + "의 위치 : " + clientHandler.gameRoom_.name_);
			
			Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
			while(iterator.hasNext()) {
				String id = (String)iterator.next();
				ClientHandler handler = chm.getClientHandler(id);
				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 gameChat 명령을 수행합니다.");
				handler.sendResponse(jsonStr);		
				if(quizCorrect){ //퀴즈 정답을 이야기 한 경우
					ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 quizCorrect 명령을 수행합니다.");
					handler.sendResponse(jsonStr0);							
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
		map.put("msg", jsonObj.get("msg")); //채팅 내용
		
		return map;
	}
}
