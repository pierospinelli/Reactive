package it.pjsoft.reactive.rs.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Questa classe è un PATCH per il problema di class loading inerente la Classe HttpServletRequest
 * ed altre che sembrano esistere in più versioni sovrapposte.
 * Una volta risolto tale problema questa classe può sparire.
 * @author Piero
 *
 */
public class HttpUtil {

	public static String getPathInfo(HttpServletRequest httpRequest) {
//		return httpRequest.getPathInfo();
		try {
			Method m = httpRequest.getClass().getMethod("getPathInfo");
			return (String) m.invoke(httpRequest);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static Map<String, String[]> getParameterMap(HttpServletRequest httpRequest) {
//		return httpRequest.getParameterMap();
		try {
			Method m = httpRequest.getClass().getMethod("getParameterMap");
			return (Map<String, String[]>) m.invoke(httpRequest);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getRemoteHost(HttpServletRequest httpRequest) {
//		return httpRequest.getRemoteHost();
		try {
			Method m = httpRequest.getClass().getMethod("getRemoteHost");
			return (String) m.invoke(httpRequest);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getContextPath(HttpServletRequest httpRequest) {
//		return httpRequest.getRemoteHost();
		try {
			Method m = httpRequest.getClass().getMethod("getContextPath");
			return (String) m.invoke(httpRequest);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Enumeration<String> getHeaderNames(HttpServletRequest httpRequest) {
//		return httpRequest.getRemoteHost();
		try {
			Method m = httpRequest.getClass().getMethod("getHeaderNames");
			return (Enumeration<String> ) m.invoke(httpRequest);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getHeader(HttpServletRequest httpRequest, String name) {
//		return httpRequest.getRemoteHost();
		try {
			Method m = httpRequest.getClass().getMethod("getHeader", String.class);
			return (String) m.invoke(httpRequest, name);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
