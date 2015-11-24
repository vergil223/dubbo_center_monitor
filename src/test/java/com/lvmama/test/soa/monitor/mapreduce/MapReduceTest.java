package com.lvmama.test.soa.monitor.mapreduce;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lvmama.soa.monitor.dao.redis.JedisTemplate;
import com.lvmama.soa.monitor.entity.DubboServiceDayIP;
import com.lvmama.soa.monitor.pub.mapreduce.MapReduce;
import com.lvmama.soa.monitor.pub.mapreduce.redis.RedisGetMultiKeysCombiner;
import com.lvmama.soa.monitor.pub.mapreduce.redis.RedisGetMultiKeysMapper;
import com.lvmama.soa.monitor.pub.mapreduce.redis.RedisGetMultiKeysReducer;
import com.lvmama.soa.monitor.util.DateUtil;
import com.lvmama.test.soa.monitor.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:applicationContext-test.xml" })
public class MapReduceTest extends BaseTest{
	private static final Log log=LogFactory.getLog(MapReduceTest.class);
	
	@Test
	public void testRedisMultiKey() {
		long start=DateUtil.now().getTime();
		System.out.println("-------------testRedisMultiKey() START");
		
		MapReduce<String,String,String,DubboServiceDayIP,Map<String,DubboServiceDayIP>,List<DubboServiceDayIP>> mapReduce=new MapReduce<String,String,String,DubboServiceDayIP,Map<String,DubboServiceDayIP>,List<DubboServiceDayIP>>();
		mapReduce.setMappers(RedisGetMultiKeysMapper.class,8);
		mapReduce.setReducers(RedisGetMultiKeysReducer.class,2);
		mapReduce.setCombiner(new RedisGetMultiKeysCombiner());
		
		JedisTemplate jedisReaderTemplate = JedisTemplate.getReaderInstance();
		String keyPattern="20151124"+"_"+"com.lvmama.soa.monitor.entity.DubboServiceDayIP_"+"pet_public"+"_*";
		Set<String> keys=jedisReaderTemplate.keys(keyPattern);
		Map<String,String> inputs=new HashMap<String,String>();
		for(String key:keys){
			inputs.put(key, key);
		}
		mapReduce.setInputs(inputs);
		
		List<DubboServiceDayIP> list =mapReduce.work();
		for(DubboServiceDayIP day:list){
			System.out.println(day);
		}
		System.out.println("-------------testRedisMultiKey() END cost:"+(DateUtil.now().getTime()-start)+"ms");
	}
	
}
