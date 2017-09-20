import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

//방 퇴장 명령 실행자
public class CommandExecutorExitGameRoom implements CommandExecutor {
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		String result = "success";
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");

			RoomManager roomManager = RoomManager.getInstance(); //RoomManager 참조
			
			clientHandler.gameRoom_.exitClient(clientHandler); //현재 방에서 퇴장	
			
			//방에 남아있는 인원 있는지 확인
			boolean isAliveRoom = roomManager.isAliveRoom(clientHandler.gameRoom_.key_);

			if(!isAliveRoom){//아무도 없으면 방 삭제
				roomManager.removeRoom(clientHandler.gameRoom_.key_);
			}else{ //방에 남은 인원 있을 때 작업
				ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //나가는 방 client 목록
				//방에서 나가는 client가 방장이라면 방장 위임
				boolean changeHost = clientHandler.gameRoom_.hostClientHandler.equals(clientHandler);
				if(changeHost){ 
					Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
					String id = (String)iterator.next();
					ClientHandler newHostClientHandler = chm.getClientHandler(id); //방장이 될 클라이언트 가져옴
					clientHandler.gameRoom_.setHostClient(newHostClientHandler); //새로운 방장 위임
				}

		        String[] keys = {"id", "nick"};
		        Object[] values = {clientHandler.account_.id_, clientHandler.account_.nick_};
		        String jsonStr = JsonEncode.getInstance().encodeCommandJson("noticeExit", keys, values); //방에 있는 클라이언트에게 유저 퇴장을 알림
		        
		        Object[] values2 = {clientHandler.gameRoom_.hostClientHandler.account_.id_, clientHandler.gameRoom_.hostClientHandler.account_.nick_};
		        String jsonStr2 = JsonEncode.getInstance().encodeCommandJson("noticeChangeHost", keys, values2); //방에 있는 클라이언트에게 방장 변경을 알림
		        
				//방에 남아 있는 유저들에게 퇴장 메세지 전달
				Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
				while(iterator.hasNext()) {
					String id = (String)iterator.next();
					ClientHandler handler = chm.getClientHandler(id);
					if(handler != clientHandler){
	    				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 noticeExit 명령을 수행합니다.");
						handler.sendResponse(jsonStr);	
						if(changeHost){ //방장이 변경 된 경우 방장 변경 메세지 보냄
		    				ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 noticeChangeHost 명령을 수행합니다.");
							handler.sendResponse(jsonStr2);		
						}
					}
				}		

				if(clientHandler.gameRoom_.isStart_){ //게임 중 인원이 나갔을 경우
					if(clientHandler.gameRoom_.clientHandlerManager_.clientHandlers_.size() == 1){ //방에 남은 인원이 1명일 때
						clientHandler.gameRoom_.numQuiz_ = 0; //진행 한 퀴즈 수 초기화
						clientHandler.gameRoom_.isStart_ = false; //게임 대기 중으로 변경

				        String[] keys0 = {"totalNum", "type"};
				        Object[] values0 = {clientHandler.gameRoom_.totalQuizNum_, "exit"};
					    String jsonStr0 = JsonEncode.getInstance().encodeCommandJson("endGameRoom", keys0, values0); //방에 있는 클라이언트에게 타임 아웃으로 새 게임 알림
							
						iterator = chm.clientHandlers_.keySet().iterator();
						while(iterator.hasNext()) {
							String id = (String)iterator.next();
							ClientHandler handler = chm.getClientHandler(id);
							handler.account_.isReady_ = false;
							ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 endGameRoom 명령을 수행합니다.");
							handler.sendResponse(jsonStr0);		
						}			
					}else{ //방에 남은 인원이 아직 많을 경우\
						if(clientHandler.account_.id_.equals(clientHandler.gameRoom_.quizClientHandler.account_.id_)){ //나간 사람이 퀴즈 출제 하는 사람이면
							//다음 출제자 랜덤으로 선택
							int ramdNick = new Random().nextInt(clientHandler.gameRoom_.clientHandlerManager_.clientHandlers_.size());
							String newQuizNick = (String)clientHandler.gameRoom_.clientHandlerManager_.clientHandlers_.keySet().toArray()[ramdNick];
							clientHandler.gameRoom_.quizClientHandler = clientHandler.gameRoom_.clientHandlerManager_.getClientHandler(newQuizNick);

					        int randNum = new Random().nextInt(GameRoom.QUIZ_LIST.length);
							clientHandler.gameRoom_.quiz_ = GameRoom.QUIZ_LIST[randNum]; //게임방 퀴즈 정답 설정
							clientHandler.gameRoom_.numQuiz_++; //진행 한 퀴즈 수 증가
							clientHandler.gameRoom_.totalQuizNum_++; //총 퀴즈 수 증가
								        
					        String[] keys0 = {"nick", "quiz", "numQuiz", "maxQuiz", "totalNum"};
					        Object[] values0 = {clientHandler.gameRoom_.quizClientHandler.account_.nick_, clientHandler.gameRoom_.quiz_,
					        		clientHandler.gameRoom_.numQuiz_, clientHandler.gameRoom_.maxQuiz_, clientHandler.gameRoom_.totalQuizNum_};
					        String jsonStr0 = JsonEncode.getInstance().encodeCommandJson("exitQuizHostGameRoom", keys0, values0); //방에 있는 클라이언트에게 타임 아웃으로 새 게임 알림
						
							iterator = chm.clientHandlers_.keySet().iterator();
							while(iterator.hasNext()) {
								String id = (String)iterator.next();
								ClientHandler handler = chm.getClientHandler(id);
								handler.account_.isReady_ = false;
								ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 exitQuizHostGameRoom 명령을 수행합니다.");
								handler.sendResponse(jsonStr0);		
							}		
						}
					}
				}else{ //대기 중 인원이 나갔을 경우
					//방 인원 전부가 레디를 했을 경우
					if(clientHandler.gameRoom_.isAllClientReady()){
				        String[] keys0 = {"allReady"};
				        Object[] values0 = {true};
				        String jsonStr0 = JsonEncode.getInstance().encodeCommandJson("allReadyGameRoom", keys0, values0); //방에 있는 클라이언트에게 유저 레디 상태
						ServerLog.getInstance().log(this.getClass().getName(), clientHandler.gameRoom_.hostClientHandler.account_.id_ + " 에게 allReadyGameRoom 명령을 수행합니다.");
						clientHandler.gameRoom_.hostClientHandler.sendResponse(jsonStr0);						
					}else{
				        String[] keys0 = {"allReady"};
				        Object[] values0 = {false};
				        String jsonStr0 = JsonEncode.getInstance().encodeCommandJson("allReadyGameRoom", keys0, values0); //방에 있는 클라이언트에게 유저 레디 상태
						ServerLog.getInstance().log(this.getClass().getName(), clientHandler.gameRoom_.hostClientHandler.account_.id_ + " 에게 allReadyGameRoom 명령을 수행합니다.");
						clientHandler.gameRoom_.hostClientHandler.sendResponse(jsonStr0);										
					}					
				}
			}
			
			clientHandler.gameRoom_ = null; //게임방 비우기
			Room waitingRoom = roomManager.getRoom(RoomManager.waitingRoomKey); //waitingRoom 정보 가져오기
			waitingRoom.enterClient(clientHandler); //waitingRoom으로 클라이언트 입장
			
		} catch (Exception e) {
			e.printStackTrace();
			result = "fail";
		}
		
        String[] keys = {"result"}; 
        Object[] values = {result};//방 퇴장 결과
        String jsonStr = JsonEncode.getInstance().encodeCommandJson("exitGameRoomResult", keys, values); 
        clientHandler.sendResponse(jsonStr); //방 생성을 요청한 클라이언트에게 결과를 보냄
	}

	@Override
	public HashMap<String, Object> decoding(String request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
