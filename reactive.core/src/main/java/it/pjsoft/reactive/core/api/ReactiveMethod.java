package it.pjsoft.reactive.core.api;

public interface ReactiveMethod<T, I> {
	public enum CallBackType{
		METHOD, PRE_CALL, POST_CALL
	}
	
	public static final String CALL_BACK_TYPE="it.pjsoft.reactive.method.type";
	public static final String METHOD_NAME="it.pjsoft.reactive.method.name";
	public static final String TARGET_CLASS="it.pjsoft.reactive.method.class";
	public static final String TARGET_ENTITY_ID="it.pjsoft.reactive.method.entityId";
	
	public T execute(Object target, T prevRet, I pars) throws Exception;
}
