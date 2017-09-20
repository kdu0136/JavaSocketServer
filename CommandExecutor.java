import java.util.HashMap;

//명령 실행자
public interface CommandExecutor {
	//명령 실행 함수
	public void execute(String request, ClientHandler clientHandler) throws Exception;
	public HashMap<String, Object> decoding(String request) throws Exception;
}
