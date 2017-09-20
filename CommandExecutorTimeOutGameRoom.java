import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

//게임 타임 아웃
public class CommandExecutorTimeOutGameRoom implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try
		{
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");

			clientHandler.gameRoom_.totalQuizNum_++; //총 퀴즈 수 증가
			if(clientHandler.gameRoom_.numQuiz_ == clientHandler.gameRoom_.maxQuiz_){ //진행 한 퀴즈 수가 총 문제 갯수가 되면 게임 종료 알림
				clientHandler.gameRoom_.numQuiz_ = 0; //진행 한 퀴즈 수 초기화
				clientHandler.gameRoom_.isStart_ = false; //게임 대기 중으로 변경

			    String[] keys = {"totalNum", "type"};
			    Object[] values = {clientHandler.gameRoom_.totalQuizNum_, "time"};
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
				//다음 출제자 랜덤으로 선택
				int ramdNick = new Random().nextInt(clientHandler.gameRoom_.clientHandlerManager_.clientHandlers_.size());
				String newQuizNick = (String)clientHandler.gameRoom_.clientHandlerManager_.clientHandlers_.keySet().toArray()[ramdNick];
				clientHandler.gameRoom_.quizClientHandler = clientHandler.gameRoom_.clientHandlerManager_.getClientHandler(newQuizNick);

		        int randNum = new Random().nextInt(GameRoom.QUIZ_LIST.length);
				clientHandler.gameRoom_.quiz_ = GameRoom.QUIZ_LIST[randNum]; //게임방 퀴즈 정답 설정
				clientHandler.gameRoom_.numQuiz_++; //진행 한 퀴즈 수 증가
					        
		        String[] keys = {"nick", "quiz", "numQuiz", "maxQuiz", "totalNum"};
		        Object[] values = {clientHandler.gameRoom_.quizClientHandler.account_.nick_, clientHandler.gameRoom_.quiz_,
		        		clientHandler.gameRoom_.numQuiz_, clientHandler.gameRoom_.maxQuiz_, clientHandler.gameRoom_.totalQuizNum_};
		        String jsonStr = JsonEncode.getInstance().encodeCommandJson("timeOutGameRoom", keys, values); //방에 있는 클라이언트에게 타임 아웃으로 새 게임 알림

				ClientHandlerManager chm = clientHandler.gameRoom_.clientHandlerManager_; //현재 입장한 방 client 목록									
				Iterator<String> iterator = chm.clientHandlers_.keySet().iterator();
				while(iterator.hasNext()) {
					String id = (String)iterator.next();
					ClientHandler handler = chm.getClientHandler(id);
					handler.account_.isReady_ = false;
					ServerLog.getInstance().log(this.getClass().getName(), handler.account_.id_ + " 에게 timeOutGameRoom 명령을 수행합니다.");
					handler.sendResponse(jsonStr);		
				}		
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
