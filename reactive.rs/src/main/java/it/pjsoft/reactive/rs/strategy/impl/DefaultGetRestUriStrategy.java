package it.pjsoft.reactive.rs.strategy.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import it.pjsoft.reactive.rs.impl.HttpUtil;
import it.pjsoft.reactive.rs.strategy.RestUriStrategy;

public class DefaultGetRestUriStrategy extends RestUriStrategy {

	@Override
	public Map<String, String> parsePath(HttpServletRequest httpRequest) {
		String pth = HttpUtil.getPathInfo(httpRequest);
		Map<String,String> ret=new HashMap<String,String>();
		String[] segs=pth.split("/");
		for(int i=0;i<segs.length;i++)
			ret.put("PATH_"+i, segs[i]);
		try {
			ret.put("caller", HttpUtil.getRemoteHost(httpRequest));
			ret.put("invocationContext", HttpUtil.getContextPath(httpRequest));
			ret.put("codApplication", segs[4]);
			ret.put("codOrganization", segs[5]);
			ret.put("user", segs[6]);
			ret.put("service", segs[7]);
			ret.put("method", segs[8]);
		} catch (IndexOutOfBoundsException e) { //no catch
		}
		
		return ret;
	}

}
