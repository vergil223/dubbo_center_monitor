package com.lvmama.test.soa.monitor;

import java.util.List;

import com.lvmama.soa.monitor.entity.DubboMethodDay;
import com.lvmama.soa.monitor.util.CacheUtil;
import com.lvmama.soa.monitor.util.MemcachedUtil;

public class OtherTest {
	public static void main(String args[])throws Exception{
//		System.out.println(MemcachedUtil.getInstance().get("ALERT_TEST_com.lvmama.test.soa.monitor.AlertTesttestMethodDayIPSuccessTimesAlert20160605"));
		
		List<DubboMethodDay> dubboMethodDayList=CacheUtil.getArray("ALERT_TEST_com.lvmama.test.soa.monitor.AlertTesttestMethodDayIPSuccessTimesAlert20160605", DubboMethodDay.class);
		DubboMethodDay day=dubboMethodDayList.get(0);
		System.out.println(day);
		System.out.println(day.getSuccessTimesDetail());
//		StringBuilder sql=new StringBuilder();
//		sql.append(" INSERT INTO                        ");
//		sql.append(" 		DUBBO_METHOD_DAY_IP (           ");
//		sql.append(" 		APP_NAME,                       ");
//		sql.append(" 		SERVICE,                        ");
//		sql.append(" 		METHOD,                         ");
//		sql.append(" 		CONSUMER_IP,                    ");
//		sql.append(" 		PROVIDER_IP,                    ");
//		sql.append(" 		TIME,                           ");
//		sql.append(" 		SUCCESS_TIMES,                  ");
//		sql.append(" 		FAIL_TIMES,                     ");
//		sql.append(" 		ELAPSED_AVG,                    ");
//		sql.append(" 		ELAPSED_MAX,                    ");
//		sql.append(" 		SUCCESS_TIMES_DETAIL,           ");
//		sql.append(" 		FAIL_TIMES_DETAIL,              ");
//		sql.append(" 		ELAPSED_TOTAL_DETAIL,           ");
//		sql.append(" 		ELAPSED_MAX_DETAIL              ");
//		sql.append(" 		) SELECT                        ");
//		sql.append(" 		'a',                            ");
//		sql.append(" 		'a',                     ");
//		sql.append(" 		'a',                      ");
//		sql.append(" 		'a',                  ");
//		sql.append(" 		'a',                  ");
//		sql.append(" 		'a',                        ");
//		sql.append(" 		'a',                ");
//		sql.append(" 		'a',                   ");
//		sql.append(" 		'a',                  ");
//		sql.append(" 		'a',                  ");
//		sql.append(" 		'a',          ");
//		sql.append(" 		'a',             ");
//		sql.append(" 		'a',          ");
//		sql.append(" 		'a'             ");
//		sql.append(" 		from DUAL                       ");
//		sql.append(" 		WHERE                           ");
//		sql.append(" 		NOT EXISTS (                    ");
//		sql.append(" 		SELECT                          ");
//		sql.append(" 		1                               ");
//		sql.append(" 		FROM                            ");
//		sql.append(" 		DUBBO_METHOD_DAY_IP t           ");
//		sql.append(" 		WHERE                           ");
//		sql.append(" 		APP_NAME = 'a'           ");
//		sql.append(" 		AND SERVICE = 'a'        ");
//		sql.append(" 		AND METHOD = 'a'          ");
//		sql.append(" 		AND CONSUMER_IP = 'a' ");
//		sql.append(" 		AND PROVIDER_IP = 'a' ");
//		sql.append(" 		AND TIME = 'a'              ");
//		sql.append(" 		)                               ");
//		CCJSqlParserManager parser=new CCJSqlParserManager();
//		Insert insert = (Insert)parser.parse(new StringReader(sql.toString()));  
//	    
//		ItemsList itemList=insert.getItemsList();
//		
//		
//		System.out.println(insert);
//	    System.out.println(insert);
	}
}
