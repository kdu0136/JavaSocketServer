import java.util.ArrayList;
import java.util.HashMap;

//방에 저장 된 canvas 불러오기
public class CommandExecutorLoadCanvas implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 채팅 내용
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");		

		    String[] keys = {"progressBar"};
		    Object[] values = {clientHandler.gameRoom_.gameTime_};
		    String jsonStr = JsonEncode.getInstance().encodeCommandJson("progressGameRoom", keys, values); //방에 있는 클라이언트에게 타임 아웃으로 새 게임 알림
			clientHandler.sendResponse(jsonStr); //게임 진행 률 보내기		
			
			String hashesKey = clientHandler.gameRoom_.key_ + "Canvas" + clientHandler.gameRoom_.totalQuizNum_;
			
			JedisHandler jedisHandler = new JedisHandler();
			final long fieldNum = jedisHandler.jedisHlen(hashesKey); //해당 유저 hashes에 저장할 fieldNum 받아오기
			String[] field = new String[(int) fieldNum];
			for(int i = 0; i < field.length; i++)
				field[i] = String.valueOf(i);

			ArrayList<Object> value = jedisHandler.jedisHget(hashesKey, field);
			for(int i = 0; i < value.size(); i++){
				try {
					clientHandler.sendResponse((String)value.get(i));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		return null;
	}
}
