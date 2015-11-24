package com.lvmama.soa.monitor.pub.mapreduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MapReduce<IK,IV,RK,RV,RR,FR> {
	private Log log=LogFactory.getLog(MapReduce.class);
	
	private Map<IK,IV> inputs;
	private List<Mapper<IK,IV,RK,RV,RR>> mappers=new ArrayList<Mapper<IK,IV,RK,RV,RR>>();
	private List<Reducer<RK,RV,RR>> reducers=new ArrayList<Reducer<RK,RV,RR>>();
	private Combiner<RR,FR> combiner;
	private Context<RK,RV,RR> context=new Context<RK,RV,RR>();
	
	public FR work(){
		try{
			//1. map
			ExecutorService mapExec=Executors.newFixedThreadPool(mappers.size());
			
			int i=0;
			for(Entry<IK,IV> inputEntry:inputs.entrySet()){
				mapExec.submit(new MapTask(mappers.get(i++%mappers.size()),inputEntry.getKey(),inputEntry.getValue(),context));
			}
			mapExec.shutdown();
			while(!mapExec.awaitTermination(1000, TimeUnit.MILLISECONDS)){
				try{
					Thread.sleep(1000L);
				}catch(Exception e){
					log.error("error when waiting for mapExec terminate.", e);
				}
			}
			
			List<Map<RK,RV>> mapResultList=context.getMapResults();
			for(Mapper<IK,IV,RK,RV,RR> mapper:mappers){
				mapResultList.add(mapper.getMapResult());
			}
			
			//2. reduce
			ExecutorService reduceExec=Executors.newFixedThreadPool(reducers.size());
			
			for(Map<RK, RV> mapResult:context.getMapResults()){
				int i1=0;
				for(Entry<RK, RV> entry:mapResult.entrySet()){
					reduceExec.submit(new ReduceTask(reducers.get(i1++%reducers.size()),entry.getKey(),entry.getValue(),context));				
				}
			}
			reduceExec.shutdown();
			while(!reduceExec.awaitTermination(1000, TimeUnit.MILLISECONDS)){
				try{
					Thread.sleep(1000L);			
				}catch(Exception e){
					log.error("error when waiting for reduceExec terminate.", e);
				}
			}
			
			List<RR> reduceResult=context.getReduceResult();
			for(Reducer<RK,RV,RR> reducer:reducers){
				reduceResult.add(reducer.getReduceResult());
			}
			
			//3. combine
			return combiner.combine(reduceResult);
		}catch(Exception e){
			log.error("MapReduce.work() error",e);
			return null;
		}
	}
	
	private class MapTask<IK,IV,RK,RV,RR> implements Runnable{
		private Mapper<IK,IV,RK,RV,RR> mapper;
		private IK inputKey;
		private IV inputValue;
		private Context<RK,RV,RR> context;
		public MapTask(Mapper<IK,IV,RK,RV,RR> m,IK k,IV i,Context<RK,RV,RR> c){
			this.mapper=m;
			this.inputKey=k;
			this.inputValue=i;
			this.context=c;
		}
		@Override
		public void run() {
			mapper.map(inputKey, inputValue, context);
		}
		
	}
	
	private class ReduceTask<RK,RV> implements Runnable{
		private Reducer<RK,RV,RR> reducer;
		private RK reduceKey;
		private RV reduceValue;
		private Context<RK,RV,RR> context;
		public ReduceTask(Reducer<RK,RV,RR> reducer, RK key, RV value,
		Context<RK, RV, RR> context){
			this.reducer=reducer;
			this.reduceKey=key;
			this.reduceValue=value;
			this.context=context;
		}
		@Override
		public void run() {
			reducer.reduce(reduceKey, reduceValue, context);
		}
		
	}

	public Map<IK, IV> getInputs() {
		return inputs;
	}

	public void setInputs(Map<IK, IV> inputs) {
		this.inputs = inputs;
	}

	public void setMappers(Class<? extends Mapper> clazz,int numberOfMappers) {
		for(int i=1;i<=numberOfMappers;i++){
			try {
				this.mappers.add(clazz.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void setReducers(Class<? extends Reducer> clazz,int numberOfReducers) {
		for(int i=1;i<=numberOfReducers;i++){
			try {
				this.reducers.add(clazz.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Combiner<RR, FR> getCombiner() {
		return combiner;
	}

	public void setCombiner(Combiner<RR, FR> combiner) {
		this.combiner = combiner;
	}
}
