package com.lvmama.soa.monitor.dao.mybatis;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseDao {
	private static final Log LOG = LogFactory.getLog(BaseDao.class);

	protected SqlSessionTemplate sqlSession;
	protected boolean externalSqlSession;

	@Autowired(required = false)
	public final void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		if (!this.externalSqlSession) {
			this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
		}
	}

	public final SqlSession getBatchSqlSession() {
		return new SqlSessionTemplate(this.sqlSession.getSqlSessionFactory(),
				ExecutorType.BATCH);
	}

	public SqlSession getSqlSession() {
		return this.sqlSession;
	}

	protected int insert(String key, Object object) {
		if (object != null) {
			return getSqlSession().insert(key, object);
		}
		return 0;
	}

	protected int update(String key, Object object) {
		if (object != null) {
			return getSqlSession().update(key, object);
		}
		return 0;
	}

	@SuppressWarnings({ "unchecked" })
	protected <T> T get(String key, Object params) {
		if (params != null) {
			return (T) getSqlSession().selectOne(key, params);
		} else {
			return null;
		}
	}

	protected <T> List<T> getList(String key, Object params) {
		if (params != null) {
			return getSqlSession().selectList(key, params);
		} else {
			return null;
		}
	}
	//
	// protected int insertFromOtherTable(String key, Map<String, Object>
	// paramMap) {
	// if (paramMap != null) {
	// return getSqlSession().insert(createStatementName(key),paramMap);
	// }
	// return 0;
	// }
	//

	//
	// protected int delete(String key, Serializable id) {
	// if (id != null) {
	// return getSqlSession().delete(createStatementName(key), id);
	// }
	// return 0;
	// }
	//
	// protected int delete(String key, Object object) {
	// if (object != null) {
	// return getSqlSession().delete(createStatementName(key), object);
	// }
	// return 0;
	// }
	//
	// protected int deleteAll(String key, Map<String, Object> paramMap) {
	// if (paramMap != null) {
	// return getSqlSession().delete(createStatementName(key),paramMap);
	// }
	// return 0;
	// }
	
	//
	// if (list.size() == 1) {
	// return list.get(0);
	// }
	//
	// if (list.size() > 1) {
	// throw new
	// TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: "+
	// list.size());
	// }
	//
	// return null;
	// }
	//

	//
	// /**
	// * 重载一个无参数的get方法，供vst_search使用
	// * @author wenzhengtao
	// * @param key
	// * @return
	// */
	// @SuppressWarnings({ "unchecked" })
	// protected <T> T get(String key) {
	// return (T) getSqlSession().selectOne(createStatementName(key));
	// }
	//
	// protected <T> List<T> getList(String key) {
	// return getSqlSession().selectList(createStatementName(key));
	// }
	//

	//
	// protected <T> List<T> queryForList(String statementName) throws
	// DataAccessException {
	// return queryForList(statementName, null);
	// }
	//
	// protected <T> List<T> queryForList(final String statementName, final
	// Object parameterObject) throws DataAccessException {
	// if (parameterObject != null) {
	// List<T> result =
	// getSqlSession().selectList(createStatementName(statementName),
	// parameterObject, new RowBounds(0, maxRows));
	// if ((result != null) && (result.size() == maxRows)) {
	// LOG.warn("SQL Exception: result size is greater than the max rows, " +
	// namespaceName + "." + statementName);
	// }
	// return result;
	// } else {
	// return null;
	// }
	// }
	//
	// protected <T> List<T> queryForList(String statementName, int skipResults,
	// int maxResults) throws DataAccessException {
	//
	// if ((maxResults - skipResults) >= maxRows) {
	// maxResults = skipResults + maxRows;
	// LOG.warn("SQL Exception: result size is greater than the max rows, " +
	// createStatementName(statementName));
	// }
	//
	// return queryForList(statementName, null, skipResults, maxResults);
	// }
	//
	// protected <T> List<T> queryForList(final String statementName, final
	// Object parameterObject, final int skipResults, final int maxResults)
	// throws DataAccessException {
	//
	// int tempMaxResults = maxResults;
	// if ((maxResults - skipResults) >= maxRows) {
	// tempMaxResults = skipResults + maxRows;
	// LOG.warn("SQL Exception: result size is greater than the max rows, " +
	// createStatementName(statementName));
	// }
	// return getSqlSession().selectList(createStatementName(statementName),
	// parameterObject, new RowBounds(skipResults, tempMaxResults));
	// }
	//
	// // 数据量比较大的报表导出请用这个接口
	// protected <T> List<T> queryForListForReport(String statementName) throws
	// DataAccessException {
	// return queryForListForReport(statementName, null);
	// }
	//
	// // 数据量比较大的报表导出请用这个接口
	// protected <T> List<T> queryForListForReport(final String statementName,
	// final Object parameterObject) throws DataAccessException {
	//
	// List<T> result =
	// getSqlSession().selectList(createStatementName(statementName),
	// parameterObject, new RowBounds(0, maxRowsForReport));
	//
	// if ((result != null) && (result.size() == maxRowsForReport)) {
	// LOG.warn("SQL Exception: result size is greater than the max rows, " +
	// statementName);
	// }
	// return result;
	// }
	//
	// // 数据量比较大的报表导出请用这个接口
	// protected <T> List<T> queryForList(final String statementName, final
	// Object parameterObject, final boolean isForReportExport) throws
	// DataAccessException {
	//
	// int maxRowsTemp = maxRows;
	// if (isForReportExport) {
	// maxRowsTemp = maxRowsForReport;
	// }
	//
	// List<T> result =
	// getSqlSession().selectList(createStatementName(statementName),
	// parameterObject, new RowBounds(0, maxRowsTemp));
	// if ((result != null) && (result.size() == maxRowsTemp)) {
	// LOG.warn("SQL Exception: result size is greater than the max rows, " +
	// statementName);
	// }
	// return result;
	// }
	//
	// /*
	// * XXX 搜索重载部分，允许参数传入null
	// */
	// protected <T> List<T> getListFree(String key, Object params) {
	// return getSqlSession().selectList(createStatementName(key), params);
	// }
	//
	// protected int updateFree(String key, Object object) {
	// return getSqlSession().update(createStatementName(key), object);
	// }

}
