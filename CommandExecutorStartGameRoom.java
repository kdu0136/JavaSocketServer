import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

//방 입장 명령 실행자
public class CommandExecutorStartGameRoom implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try
		{
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
						
			//방 인원 전부가 레디를 했을 경우 (방장 제외) 게임 시작 알림
			if(clientHandler.gameRoom_.isAllClientReady()){
				clientHandler.gameRoom_.numQuiz_ = 0; //진행 중 퀴즈 초기화
				clientHandler.gameRoom_.isStart_ = true; //게임방 상태 게임 중으로 변경
		        int randNum = new Random().nextInt(GameRoom.QUIZ_LIST.length);
				clientHandler.gameRoom_.quiz_ = GameRoom.QUIZ_LIST[randNum]; //게임방 퀴즈 정답 설정	     
				clientHandler.gameRoom_.numQuiz_++; //진행 한 퀴즈 수 증가
		        
		        String[] keys = {"isGameStart", "nick", "quiz", "maxQuiz"};
		        Object[] values = {true, clientHandler.gameRoom_.hostClientHandler.account_.nick_, clientHandler.gameRoom_.quiz_,
		        		clientHandler.gameRoom_.maxQuiz_};
		        String jsonStr = JsonEncode.getInstance().encodeCommandJson("startGameRoom", keys, values); //방에 있는 클라이언트에게 게임 시작 알림 (일반 유저 용)

				ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //현재 입장한 방 client 목록									
				Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
				while(iterator.hasNext()) {
					String id = (String)iterator.next();
					ClientHandler handler = chm.getClientHandler(id);
					handler.account_.isReady_ = false;
					ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 startGameRoom 명령을 수행합니다.");
					handler.sendResponse(jsonStr);		
				}									
			}else{			
		        String[] keys = {"isGameStart"};
		        Object[] values = {false};
		        String jsonStr = JsonEncode.getInstance().encodeCommandJson("startGameRoom", keys, values); //방에 있는 클라이언트에게 게임 시작 알림 (일반 유저 용)
		       
				ServerLog.getInstance().log(this.getClass().getName(), clientHandler.account_.id_ + " 에게 startGameRoom 명령을 수행합니다.");
				clientHandler.sendResponse(jsonStr);								
			}						
		} catch (Exception e)
		{
			e.printStackTrace();
		}		
	}

	/**********************************
	 * decoding(String msg) - json형식 명령 디테일을 풀어주는 기능
	 * msg - json형식의 명령 디테일
	**********************************/
	@Override
	public HashMap<String, Object> decoding(String request) throws Exception {
		return null;
	}
}
