package es.jma.lib.jmeter.commons.sampler;

import java.util.Map;

import redis.clients.jedis.Jedis;

/**
 * 
 * @author Julian Montejo
 * jedis wrapper 
 *
 */
public class RedisClient 
{
	public static Jedis jedis;
	
	public RedisClient(String host, int port, int timeout){
		jedis = new Jedis(host,port,timeout);
	}
	
	public void close(){
		jedis.disconnect();
	}
	
	public String keys(String searchString){
		StringBuilder devolver = new StringBuilder();
		devolver.append(jedis.keys(searchString));
		return devolver.toString();
	}
	
	public String type(String key){
		return jedis.type(key);
	}
	
	public String hgetAll(String key){
		StringBuilder devolver = new StringBuilder();
		devolver.append(jedis.hgetAll(key));
		return devolver.toString();
	}
	
	public String hget(String key, String field){
		return jedis.hget(key, field);
	}
	
	public long hset(String key, String field, String value){
		return jedis.hset(key, field, value);
	}
	
	public String hsetM(String key, Map<String,String> hash){
		return jedis.hmset(key, hash);
	}
	
	public long hdel(String key, String field){
		return jedis.hdel(key, field);
	}
	
	public long del(String key){
		return jedis.del(key);
	}
	
	public long ttl(String key){
		return jedis.ttl(key);
	}
	
	public Long expire(String key, int seconds){
		return jedis.expire(key, seconds);
	}
	
	public Long expireAt(String key, long unixTime){
		return jedis.expireAt(key, unixTime);
	}
	
	public String get(String key){
		return jedis.get(key);
	}
	
	public String set(String key, String value){
		return jedis.set(key, value);
	}
}
