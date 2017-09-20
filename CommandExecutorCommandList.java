import java.util.HashMap;
import java.util.Iterator;

//실행 가능한 명령 리스트 보여주는 명령 실행자
public class CommandExecutorCommandList implements CommandExecutor {	
	/**********************************
	 * execute(String request, ClientHandler clientHandler) - 명령 실행
	 * request - 명령 디테일
	 * clientHandler - 명령을 요청 한 클라이언트
	**********************************/
	public void execute(String request, ClientHandler clientHandler) throws Exception {
		try {
			ServerLog.getInstance().log(this.getClass().getName(),"명령을 수행합니다.");
			
			String sendCommand = "[CommandList]: ";

			Iterator<String> iterator = clientHandler.requestHandler_.commands_.keySet().iterator(); //등록 명령 Map 이터레이터 
			while(iterator.hasNext()) {
				String command = (String)iterator.next();
				sendCommand += command;
				if(iterator.hasNext())
					sendCommand += ", ";
			}
			
			clientHandler.sendResponse(sendCommand);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
		}
	}

	@Override
	public HashMap<String, Object> decoding(String request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
