package com.lvmama.test.soa.monitor;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.dao.redis.JedisTemplate;
import com.lvmama.soa.monitor.entity.DubboMethodDay;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class RedisTest extends BaseTest{
	private static final Log log=LogFactory.getLog(RedisTest.class);
	
//	@Autowired
//	private DubboMethodDayIPService dubboMethodDayIPService;
	private static final JedisTemplate jedisWriterTemplate = JedisTemplate.getWriterInstance();
	
	@Test
	public void doTest(){
		System.out.println("----------------------------------------");
		System.out.println(jedisWriterTemplate.keys("*DubboMethodDay*"));
		
		
		
		
		DubboMethodDay object1 = new DubboMethodDay();
		DubboMethodDay object2 = new DubboMethodDay();
		object1.setAppName("RedisTest1");
		object2.setAppName("RedisTest2");

		jedisWriterTemplate.set("com.test.aaa", 111, 1000);
		jedisWriterTemplate.set("com.test.object", object1, 1000);
		jedisWriterTemplate.setArray("com.test.list", Arrays.asList(object1,object2), 1000);
		
		System.out.println(jedisWriterTemplate.get("com.test.aaa"));
		System.out.println(jedisWriterTemplate.getArray("com.test.list", DubboMethodDay.class));
		System.out.println(jedisWriterTemplate.get("com.test.object", DubboMethodDay.class));
		
		System.out.println(jedisWriterTemplate.keys("com.test*"));
		
		jedisWriterTemplate.del("com.test.aaa","com.test.list", "com.test.object");
		
		System.out.println(jedisWriterTemplate.keys("com.test*"));
		System.out.println("----------------------------------------");
	}
	
}
