package it.pjsoft.reactive.core.impl;

import java.sql.Connection;
import java.sql.Driver;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.osgi.framework.ServiceException;
import org.osgi.service.async.Async;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
//import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.jpa.EntityManagerFactoryBuilder;
//import org.osgi.service.jpa.EntityManagerFactoryBuilder;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProviderFactory;
import org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory;
//import org.osgi.service.transaction.control.jpa.JPAEntityManagerProviderFactory;
import org.osgi.util.promise.Promise;

import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.ReactiveExecutor;

@Component(service=ReactiveExecutor.class, immediate=true)
public class ReactiveExecutorImpl implements ReactiveExecutor {
	private static final int TX_NONE = 0;
	private static final int TX_SUPPORTS = 1;
	private static final int TX_REQUIRED = 2;
	private static final int TX_REQUIRES_NEW = 3;

	@Reference(service=Async.class)
	private Async async;
	
	@Reference 
	private TransactionControl txControl = null;
	
	@Reference //(cardinality=ReferenceCardinality.OPTIONAL)
	private JDBCConnectionProviderFactory jdbcProviderFactory;

	@Reference(cardinality=ReferenceCardinality.OPTIONAL)
	private JPAEntityManagerProviderFactory jpaProviderFactory;
	
	public ReactiveExecutorImpl() {
		System.out.println("ReacriveExecutorImpl Created");
	}
	
	
	@Activate
   void start() {
	System.out.println("ReacriveExecutorImpl Started");
   }
	
	@Override
	public <T> T execSupports(Callable<T> callback) {
		return (T) txControl.supports(()->{
			try {
				return callback.call();
			} catch (Exception e) {
				throw new ReactiveException(e);
			}
		});
	}

	@Override
	public <T> T execRequired(Callable<T> callback) {
		return (T) txControl.required(()->{
			try {
				return callback.call();
			} catch (Exception e) {
				throw new ReactiveException(e);
			}
		});
	}

	@Override
	public <T> T execRequiresNew(Callable<T> callback) {
		return (T) txControl.requiresNew(()->{
			try {
				return callback.call();
			} catch (Exception e) {
				throw new ReactiveException(e);
			}
		});
	}

	@Override
	public <T> Promise<T> asyncNone(Callable<T> callback) throws Exception {
		Callable<T> mediatedCb = async.mediate(callback, (Class<Callable<T>>)callback.getClass());

		Promise<T> promise;
		try {
			promise = (Promise<T>) async.call(mediatedCb.call());
		} catch (Exception e) {
			throw e;
		}

		return promise;
	}

	@Override
	public <T> Promise<T> asyncSupports(Callable<T> callback) throws Exception {
		Callable<T> wrapper = new Wrapper(this, callback, TX_SUPPORTS);
		
		Callable<T> mediatedCb = async.mediate(wrapper, (Class<Callable<T>>)wrapper.getClass());

		Promise<T> promise;
		try {
			promise = (Promise<T>) async.call(mediatedCb.call());
		} catch (Exception e) {
			throw e;
		}

		return promise;
	}

	@Override
	public <T> Promise<T> asyncRequired(Callable<T> callback) throws Exception {
		Callable<T> wrapper = new Wrapper(this, callback, TX_REQUIRED);
		
		Callable<T> mediatedCb = async.mediate(wrapper, (Class<Callable<T>>)wrapper.getClass());

		Promise<T> promise;
		try {
			promise = (Promise<T>) async.call(mediatedCb.call());
		} catch (Exception e) {
			throw e;
		}

		return promise;
	}


	@Override
	public <T> Promise<T> asyncRequiresNew(Callable<T> callback) throws Exception {
		
		Callable<T> wrapper = new Wrapper(this, callback, TX_REQUIRES_NEW);

		
		Callable<T> mediatedCb = async.mediate(wrapper, (Class<Callable<T>>)wrapper.getClass());

		Promise<T> promise;
		try {
			promise = (Promise<T>) async.call(mediatedCb.call());
		} catch (Exception e) {
			throw e;
		}

		return promise;
	}

	@Override
	public Connection getTXConnection(DataSource dataSource,  Map<String, Object> providerProps) {
		if(jdbcProviderFactory==null)
			throw new ServiceException("Missing jdbcProviderFactory service");
		return jdbcProviderFactory.getProviderFor(dataSource,  providerProps).getResource(txControl);
	}
	
	@Override
	public Connection getTXConnection(XADataSource dataSource,  Map<String, Object> providerProps) {
		if(jdbcProviderFactory==null)
			throw new ServiceException("Missing jdbcProviderFactory service");
		return jdbcProviderFactory.getProviderFor(dataSource,  providerProps).getResource(txControl);
	}

	@Override
	public Connection getTXConnection(DataSourceFactory dataSource, Properties factProps,  Map<String, Object> providerProps) {
		if(jdbcProviderFactory==null)
			throw new ServiceException("Missing jdbcProviderFactory service");
		return jdbcProviderFactory.getProviderFor(dataSource,  factProps, providerProps).getResource(txControl);
	}
	
	@Override
	public Connection getTXConnection(Driver driver, Properties driverProps,  Map<String, Object> providerProps) {
		if(jdbcProviderFactory==null)
			throw new ServiceException("Missing jdbcProviderFactory service");
		return jdbcProviderFactory.getProviderFor(driver,  driverProps, providerProps).getResource(txControl);
	}

	
	@Override
	public EntityManager getTXEM(EntityManagerFactory emFactory, Map<String, Object> providerProps) {
		if(jpaProviderFactory==null)
			throw new ServiceException("Missing jpaProviderFactory service");
		return jpaProviderFactory.getProviderFor(emFactory,  providerProps).getResource(txControl);
	}

	@Override
	public EntityManager getTXEM(EntityManagerFactoryBuilder emFactoryBuilder, Map<String, Object> builderProps, Map<String, Object> providerProps) {
		if(jpaProviderFactory==null)
			throw new ServiceException("Missing jpaProviderFactory service");
		return jpaProviderFactory.getProviderFor(emFactoryBuilder, builderProps, providerProps).getResource(txControl);
	}

	public static class Wrapper<T> implements Callable<T> {
		ReactiveExecutorImpl re;
		Callable<T> callback;
		int tx;
		
		public Wrapper(){}

		Wrapper(ReactiveExecutorImpl re, Callable<T> callback, int tx){
			this.re = re;
			this.callback = callback;
			this.tx = tx;
		}
		
		
		@Override
		public T call() throws Exception {
			switch(tx) {
			case TX_NONE:
				return (T) re.execRequired(callback);
			case TX_SUPPORTS:
				return (T) re.execSupports(callback);
			case TX_REQUIRED:
				return (T) re.execRequired(callback);
			case TX_REQUIRES_NEW:
				return (T) re.execRequiresNew(callback);
			}
			throw new ReactiveException("Unknown transaction type");
		}
	};


}
