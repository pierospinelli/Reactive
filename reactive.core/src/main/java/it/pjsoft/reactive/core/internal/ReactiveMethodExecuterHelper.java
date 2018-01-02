package it.pjsoft.reactive.core.internal;

import java.lang.reflect.Method;

import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;

import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.ReactiveMethod;

public class ReactiveMethodExecuterHelper  {

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.pjsoft.reactive.core.impl.ReactiveMethodExecuter#exec(java.lang.
	 * Object, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public static <T, I> T execute(Object target, String method, I pars) throws ReactiveException {
		try {
			T ret = null;

			String clzName = null;
			String objId = null;
			Class<ReactiveMethod> clz = (target instanceof Class) ? (Class<ReactiveMethod>) target
					: (Class<ReactiveMethod>) target.getClass();

			clzName = clz.getName();
			if (!(target instanceof Class)) {
				Method m = clz.getMethod("getId", null);
				if (m != null) {
					objId = "" + m.invoke(target);
				}
			}

			ret = execMethod(target, method, ReactiveMethod.CallBackType.PRE_CALL, clzName, null, ret, pars);
			if (objId != null)
				ret = execMethod(target, method, ReactiveMethod.CallBackType.PRE_CALL, clzName, objId, ret, pars);

			ret = execMethod(target, method, ReactiveMethod.CallBackType.METHOD, clzName, null, ret, pars);
			if (objId != null)
				ret = execMethod(target, method, ReactiveMethod.CallBackType.METHOD, clzName, objId, ret, pars);

			ret = execMethod(target, method, ReactiveMethod.CallBackType.POST_CALL, clzName, null, ret, pars);
			if (objId != null)
				ret = execMethod(target, method, ReactiveMethod.CallBackType.POST_CALL, clzName, objId, ret, pars);

			return ret;
		} catch (ReactiveException e) {
			throw e;
		} catch (Exception e) {
			throw new ReactiveException(e);
		}
	}

	private static <T, I> T execMethod(Object target, String method, ReactiveMethod.CallBackType type, String clzName,
			String objId, T ret, I pars) throws Exception {

		StringBuilder sb = new StringBuilder();
		sb.append("(&(" + ReactiveMethod.METHOD_NAME + "=" + method + ")");
		sb.append("(" + ReactiveMethod.CALL_BACK_TYPE + "=" + type + ")");
		sb.append("(" + ReactiveMethod.TARGET_CLASS + "=" + clzName + ")");
		if (objId != null)
			sb.append("(" + ReactiveMethod.TARGET_ENTITY_ID + "=" + objId + ")");
		sb.append(")");
		Filter f = Activator.getContext().createFilter(sb.toString());

		ServiceReference[] sr = Activator.getContext().getServiceReferences(ReactiveMethod.class.getName(), sb.toString());
		if (sr != null && sr.length > 0) {
			for (ServiceReference sre : sr) {
				Object p = sre.getProperty(ReactiveMethod.TARGET_ENTITY_ID);
				if ((p == null && objId == null) || (p != null && p.equals(objId))) {
					ReactiveMethod<T, I> c = (ReactiveMethod<T, I>) Activator.getContext().getService(sre);
					ret = c.execute(target, ret, pars);
				}
			}
		}
		return ret;
	}

}
