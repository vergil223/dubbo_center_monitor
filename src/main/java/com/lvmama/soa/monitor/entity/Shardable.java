package com.lvmama.soa.monitor.entity;

/**
 * the entity which will be shard in DB should implement this interface
 * @author lvzhenyu
 *
 */
public interface Shardable {
	public String getShardTableName();
}
