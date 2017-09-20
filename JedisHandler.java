
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.Jedis; 
import redis.clients.jedis.JedisPool; 
import redis.clients.jedis.JedisPoolConfig; 
import redis.clients.jedis.exceptions.JedisConnectionException; 

public class JedisHandler {
	public static String auth = "KIMehddjs0310!"; //Redis Auth
	
	public JedisHandler(){
	}

	/**********************************
	 * jedisSet(String key, String value) - Redis에  key와 value를 가지고 set하는 함수
	**********************************/
	public void jedisSet(String key, String value){
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{ 
			jedis.set(key, value);	
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
	}

	/**********************************
	 * jedisGet(String key) - Redis에  key를 이용하여 value를 가지고 오는 함수
	**********************************/
	public String jedisGet(String key){
		String value = null;
		
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);			
		try{ 
			value = jedis.get(key);	
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
		
		return value;
	}


	/**********************************
	 * jedisHset(String key, String field, String value) - Redis에  key와 field, value를 가지고 hset하는 함수
	**********************************/
	public void jedisHset(String key, String field, String value){
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{ 
			jedis.hset(key, field, value);	
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
	}

	/**********************************
	 * jedisHset(String key, String field, String value) - Redis에  key와 field, value를 가지고 hset하는 함수
	**********************************/
	public void jedisHset(String key, String[] field, String[] value){
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{
			for(int i = 0; i < field.length; i++)
				jedis.hset(key, field[i], value[i]);	
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
	}
	/**********************************
	 * jedisHlen(String key) - Redis에  key를 가지고 해당 hashes의 file 갯수를 구하는 함수
	**********************************/
	public long jedisHlen(String key){
		long hlen = 0;
		
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{ 
			hlen = jedis.hlen(key);	
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
		
		return hlen;
	}

	/**********************************
	 * jedisHlen(String key, String[] field) - Redis에  key와 field를 이용하여 모든 value값은 ArrayList에 저장하여 반환하는 함수
	**********************************/
	public ArrayList<Object> jedisHget(String key, Object[] field){
		ArrayList<Object> valueList = new ArrayList<Object>();
		
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{
			for(int i = 0; i < field.length; i++)
				valueList.add(jedis.hget(key, (String)field[i]));
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
		
		return valueList;
	}


	/**********************************
	 * jedisHlen(String key, String[] field) - Redis에  key와 field를 이용하여 모든 value값은 ArrayList에 저장하여 반환하는 함수
	**********************************/
	public String jedisHget(String key, String field){
		String value = new String();
		
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{
			value = jedis.hget(key, field);
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
		
		return value;
	}

	
	/**********************************
	 * jedisHkeys(String key) - Redis에  key를 이용하여 해당 hashes에 모든 field name을 반환
	**********************************/
	public Set<String> jedisHkeys(String key){
		Set<String> fieldSet = new HashSet<String>();
		
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{
			fieldSet = jedis.hkeys(key);
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 
		
		return fieldSet;
	}

	/**********************************
	 * jedisDel(String key) - Redis에  key를 이용하여 해당 값을 비워주는 함수
	**********************************/
	public void jedisDel(String key){		
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "localhost"); 
		Jedis jedis = jedisPool.getResource();
		jedis.auth(auth);	
		try{
			jedis.del(key);
		}catch(JedisConnectionException e){ 
			if(null != jedis){ 
				jedisPool.returnBrokenResource(jedis); 
                jedis = null; 
			} 
		}finally{ 
			if(null != jedis){ 
				jedisPool.returnResource(jedis); 
			} 
		} 
		jedisPool.destroy(); 		
	}
}
