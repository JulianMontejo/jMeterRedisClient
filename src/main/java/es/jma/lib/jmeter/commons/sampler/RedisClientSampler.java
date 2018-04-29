package es.jma.lib.jmeter.commons.sampler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author Julian Montejo
 * JMeter sampler that allows perform some operations over redis
 *
 */

public class RedisClientSampler extends AbstractSampler implements TestBean {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(RedisClientSampler.class);
	
	/** List of the admitted operations**/
	public static final String OPERATION_KEYS = "keys";
	public static final String OPERATION_TYPE = "type";
	public static final String OPERATION_SET = "set";
	public static final String OPERATION_GET = "get";
	public static final String OPERATION_DELETE = "del";
	public static final String OPERATION_HGEALL = "hgetAll";
	public static final String OPERATION_HGET = "hget";
	public static final String OPERATION_HDEL = "hdel";
	public static final String OPERATION_HSET = "hset";
	public static final String OPERATION_HSETM = "hsetM";
	public static final String OPERATION_TTL = "ttl";
	public static final String OPERATION_EXPIRE = "expire";
	public static final String OPERATION_EXPIRE_AT = "expireAt";
	
	private String operation;
	private String data;
	private String key;
	private String field;
	private String urlConnection;
	
	private RedisClient rc = null;
	
	public void setOperation (String operation){
		this.operation = operation;
	}
	
	public String getOperation (){
		return this.operation;
	}
	
	public void setData (String data){
		this.data = data;
	}
	
	public String getData (){
		return this.data;
	}
	
	public void setKey (String key){
		this.key = key;
	}
	
	public String getKey(){
		return this.key;
	}

	public void setField (String field){
		this.field = field;
	}
	
	public String getField (){
		return this.field;
	}

	public void setUrlConnection(String urlConnection){
		this.urlConnection = urlConnection;
	}
	
	public String getUrlConnection(){
		return this.urlConnection;
	}

	public SampleResult sample(Entry arg0) {
		StringBuilder devolver = new StringBuilder();
        boolean IsSuccess = true;
        SampleResult retunedSR=new SampleResult();
        retunedSR.sampleStart();
        try {
        	retunedSR.setSamplerData(passedParameters());
        	verifyParameters();
        	int port=6379;
        	String host=null;
        	String[] hostPort=urlConnection.split(":");
        	if (hostPort.length>0 && hostPort[0].length()>0)
        		host=hostPort[0];
        	if (hostPort.length>1 && hostPort[1].length()>0)
	        	try {
	        		port = Integer.parseInt(hostPort[1]);
	        	} catch(NumberFormatException e){
	        		throw new Exception("Wrong url port: "+urlConnection);
	        	}
        	// 5 seconds timeout
        	rc = new RedisClient(host, port, 5000);
        	if (!RedisClient.jedis.ping().equalsIgnoreCase("PONG"))
	        	throw new Exception("Redis not connected");
        	switch(operation) {
        	case OPERATION_KEYS:
        		devolver.append(rc.keys(key));
        		break;
        	case OPERATION_TYPE:
        		devolver.append(rc.type(key));
        		break;
        	case OPERATION_DELETE:
        		devolver.append("del result: ");
        		devolver.append(rc.del(key));
        		break;
        	case OPERATION_SET:
        		devolver.append(rc.set(key, data));
        		break;
        	case OPERATION_GET:
        		devolver.append(rc.get(key));
        		break;
        	case OPERATION_HGEALL:
        		devolver.append(rc.hgetAll(key));
        		break;
        	case OPERATION_HGET:
        		devolver.append(rc.hget(key, field));
        		break;
        	case OPERATION_HSET:
        		devolver.append("hset result: ");
        		devolver.append(rc.hset(key, field, data));
        		break;
        	case OPERATION_HDEL:
        		devolver.append("hdel result: ");
        		devolver.append(rc.hdel(key, field));
        		break;
        	case OPERATION_HSETM:
        		HashMap<String,String> listaDatos=new HashMap<String,String>();
        		StringTokenizer tokens=new StringTokenizer(data, "\n");
        		while(tokens.hasMoreTokens()){
        			String token = (String)tokens.nextToken();
        			String[] fieldAndData=token.split("\t");
        			if (fieldAndData.length<2)
        				throw new Exception("Data error.\nhsetM data format: <field>\\t<data>\ndata: "+token);
        			if (fieldAndData[0]==null || fieldAndData[0].length()==0)
        				throw new Exception("Data error.\nEmpty field in:\n"+token);
        			String key = fieldAndData[0];
        			String data = Arrays.stream(fieldAndData).skip(1).collect(Collectors.joining("\t"));
        			listaDatos.put(key, data);
        		}
        		devolver.append("hsetM result: ");
        		devolver.append(rc.hsetM(key, listaDatos));
        		break;
        	case OPERATION_TTL:
        		devolver.append(rc.ttl(key));
        		break;
        	case OPERATION_EXPIRE:
        		int seconds = Integer.parseInt(field);
        		devolver.append(rc.expire(key,seconds));
        		break;
        	case OPERATION_EXPIRE_AT:
        		long unixTime = Long.parseLong(field);
        		devolver.append(rc.expireAt(key, unixTime));
        		break;
        		
        	}
        	
        }  catch (RuntimeException e) {
            devolver.append("\nError:"+e.getMessage()+"\n\n");
            devolver.append(e);
            IsSuccess = false;
             retunedSR.setResponseMessage(e.getMessage());
            log.error("Error in RedisClientSampler:", e);
         }  catch (Exception e) {
             devolver.append("\nError:"+e.getMessage()+"\n\n");
             devolver.append(e);
             IsSuccess = false;
              retunedSR.setResponseMessage(e.getMessage());
             log.error("Error in RedisClientSampler:", e);
          } finally {
        	  if (rc != null)
				rc.close();
				
             retunedSR.setSuccessful(IsSuccess);
             retunedSR.sampleEnd();
         }
         retunedSR.setDataType("text");
         retunedSR.setResponseData(devolver.toString().getBytes());
         retunedSR.setSampleLabel(getName());
         return retunedSR;
	}
	
	/**
	 * Verify that are enough parameters for the operation selected
	 * @throws Exception
	 */
	private void verifyParameters() throws Exception{
		if (urlConnection==null || urlConnection.length()==0)
			throw new Exception("Error, empty urlConection");
		switch(operation){
		case OPERATION_KEYS:
		case OPERATION_TYPE:
		case OPERATION_HGEALL:
		case OPERATION_DELETE:
		case OPERATION_GET:
		case OPERATION_TTL:
			if (key==null || key.length()==0)
				throw new Exception("Error in operation "+operation
						+"\n key is mandatory");
			break;
		case OPERATION_HGET:
		case OPERATION_HDEL:
		case OPERATION_EXPIRE:
		case OPERATION_EXPIRE_AT:
			if (key==null || field==null || key.length()==0 || field.length()==0)
				throw new Exception("Error in operation "+operation
						+"\n key and field are mandatory");
			break;
		case OPERATION_HSET:
			if (key==null || field==null || data==null
			|| key.length()==0 || field.length()==0 || data.length()==0)
				throw new Exception("Error in operation "+operation
						+"\n key, field and data are mandatory");
			break;
		case OPERATION_SET:
		case OPERATION_HSETM:
			if (key==null || data==null
			|| key.length()==0 || data.length()==0)
				throw new Exception("Error in operation "+operation
						+"\n key and data are mandatory");
			break;
		}
	}

	/**
	 * A list of the parameters passed to the sampler
	 * @return
	 */
	private String passedParameters() {
		StringBuilder devolver = new StringBuilder();
        devolver.append("Connection URL: "+urlConnection);
        devolver.append("\nOperation: "+operation+"\n");
        devolver.append("\nKey: ");
        devolver.append(key);
        devolver.append("\nField: ");
        devolver.append(field);
        devolver.append("\nData:\n");
        devolver.append(data);
        return devolver.toString();
	}
}
