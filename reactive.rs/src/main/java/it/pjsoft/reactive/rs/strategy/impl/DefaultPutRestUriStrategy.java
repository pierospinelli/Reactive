package it.pjsoft.reactive.rs.strategy.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import it.pjsoft.reactive.rs.strategy.RestUriStrategy;

public class DefaultPutRestUriStrategy extends RestUriStrategy {

	@Override
	public Map<String, String> parsePath(HttpServletRequest httpRequest) {
		String pth = httpRequest.getPathInfo();
		Map<String,String> ret=new HashMap<String,String>();
		String[] segs=pth.split("/");
		for(int i=0;i<segs.length;i++)
			ret.put("PATH_"+i, segs[i]);
		return ret;
	}

}
