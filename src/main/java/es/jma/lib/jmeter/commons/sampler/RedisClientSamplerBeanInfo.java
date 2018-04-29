package es.jma.lib.jmeter.commons.sampler;

import java.beans.PropertyDescriptor;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.gui.TypeEditor;

import es.jma.lib.jmeter.commons.sampler.RedisClientSampler;

/**
 * Class that defines the graphic interface
 * @author Julian Montejo
 *
 */
public class RedisClientSamplerBeanInfo extends BeanInfoSupport {
	
	private static final String OPERATION = "operation";
	private static final String DATA = "data";
	private static final String KEY = "key";
	private static final String FIELD = "field";
	private static final String URL_CONNECTION = "urlConnection";
	

	public RedisClientSamplerBeanInfo() {
		super(es.jma.lib.jmeter.commons.sampler.RedisClientSampler.class);
		
		createPropertyGroup("Redis operation",
	       new String[]{
	            	URL_CONNECTION
	                ,OPERATION
	                ,KEY
	                ,FIELD
	                ,DATA
	         });
		/*
		 * List of the admitted operations
		 */
		String[] Operations = new String[] {
				RedisClientSampler.OPERATION_KEYS,
				RedisClientSampler.OPERATION_TYPE,
				RedisClientSampler.OPERATION_SET,
				RedisClientSampler.OPERATION_GET,
				RedisClientSampler.OPERATION_DELETE,
				RedisClientSampler.OPERATION_HGEALL,
				RedisClientSampler.OPERATION_HGET,
				RedisClientSampler.OPERATION_HDEL,
				RedisClientSampler.OPERATION_HSET,
				RedisClientSampler.OPERATION_HSETM,
				RedisClientSampler.OPERATION_TTL,
				RedisClientSampler.OPERATION_EXPIRE,
				RedisClientSampler.OPERATION_EXPIRE_AT
				};
		
		PropertyDescriptor p;

        p = property(URL_CONNECTION);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        //p.setDisplayName("Connection URL");

        p = property(OPERATION);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
		p.setValue(NOT_OTHER, Boolean.TRUE);
        p.setValue(TAGS, Operations);
        p.setValue(DEFAULT, Operations[0]);
        //p.setDisplayName("Operation to execute");
        
        p = property(KEY);
        p.setValue(NOT_UNDEFINED, Boolean.FALSE);
        p.setValue(DEFAULT, "");
        //p.setDisplayName("Key");

        p = property(FIELD);
        p.setValue(NOT_UNDEFINED, Boolean.FALSE);
        p.setValue(DEFAULT, "");
        //p.setDisplayName("Field");
        
        p = property(DATA, TypeEditor.TextAreaEditor);
        p.setValue(NOT_UNDEFINED, Boolean.FALSE);
        p.setValue(MULTILINE, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        //p.setDisplayName("Data to add");
	}

}
