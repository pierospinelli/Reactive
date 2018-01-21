package it.pjsoft.reactive.core.api;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.util.promise.Promise;

public interface ReactiveExecutor {

	<T> T execSupports(Callable<T> callback);
	<T> T execRequired(Callable<T> callback);
	<T> T execRequiresNew(Callable<T> callback);

	<T> Promise<T> asyncNone(Callable<T> callback) throws Exception;
	<T> Promise<T> asyncSupports(Callable<T> callback) throws Exception;
	<T> Promise<T> asyncRequired(Callable<T> callback) throws Exception;
	<T> Promise<T> asyncRequiresNew(Callable<T> callback) throws Exception;
	
	Connection getTXConnection(DataSource dataSource,  Map<String, Object> providerProps);
	Connection getTXConnection(XADataSource dataSource,  Map<String, Object> providerProps);
	Connection getTXConnection(DataSourceFactory dataSource, Properties factProps,  Map<String, Object> providerProps);
	Connection getTXConnection(Driver driver, Properties driverProps,  Map<String, Object> providerProps);

	EntityManager getTXEM(EntityManagerFactory emFactory, Map<String, Object> providerProps);
	EntityManager getTXEM(EntityManagerFactoryBuilder emFactoryBuilder, Map<String, Object> builderProps, Map<String, Object> providerProps);
}
