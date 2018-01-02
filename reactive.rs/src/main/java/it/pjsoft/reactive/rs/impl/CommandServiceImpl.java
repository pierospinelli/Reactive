package it.pjsoft.reactive.rs.impl;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.cxf.dosgi.common.api.IntentsProvider;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.rs.CommandService;

@Component(immediate = true, name = "CommandService", 
    property = { 
    	      "service.exported.interfaces=it.pjsoft.reactive.rs.CommandService", 
//    	      "service.exported.interfaces=it.pjsoft.reactive.rs.*", 
      "service.exported.configs=org.apache.cxf.rs", 
      "org.apache.cxf.rs.address=/simple" //,
//      "org.apache.cxf.rs.provider=com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider",
//      "cxf.bus.prop.skip.default.json.provider.registration=true"
    } 
)
public class CommandServiceImpl implements CommandService, IntentsProvider  {


	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.rs.impl.CommandService#exec(java.lang.String, java.lang.String)
	 */
	@Override
	public String execute(MessageContext messageContext, String cmd, String parsStr) throws Exception{
		try(RtContext rtctx = RtContext.open();) {
			String ret = rtctx.executeComponent(RtContext.LAYER_INNER_BOUNDARY, cmd, parseParams(parsStr));
			return ret;
		}

	}

	private String[] parseParams(String parsStr) {
		if(parsStr==null)
			return new String[0];
		if(parsStr.trim().length()==0)
			return new String[0];
		ArrayList<String> l=new ArrayList<>();
		for(StringTokenizer st=new StringTokenizer(parsStr, "/");
			st.hasMoreTokens();) {
			l.add(st.nextToken().trim());
		}
		return l.toArray(new String[l.size()]);
	}

//	@Activate
//	void activate(BundleContext context) throws Exception {
//		this.context = context;
//		System.out.println("ACTIVATED "+getClass().getSimpleName());
//	}
//
//	@Deactivate
//	void deactivate() throws Exception {
//		this.context = null;
//		System.out.println("DEACTIVATED "+getClass().getSimpleName());
//	}

//	@Reference
//	public void setReactiveCommandExecuter(final ReactiveComponentExecuter executer) {
//		this.executer  = executer;
//	}
//
//	public void unsetReactiveCommandExecuter(final ReactiveComponentExecuter executer) {
//		this.executer = null;
//	}


    @Override
    public List<?> getIntents() {
        return asList(createSwaggerFeature()/*, new JacksonJsonProvider()*/);
    }

	  private Swagger2Feature createSwaggerFeature() {
	        Swagger2Feature swagger = new Swagger2Feature();
	        swagger.setDescription("Sample Command Execution Service");
	        swagger.setTitle("Command Execution sample");
	        swagger.setUsePathBasedConfig(true); // Necessary for OSGi
	        // swagger.setScan(false); // Must be set for cxf < 3.2.x
	        swagger.setPrettyPrint(true);
	        swagger.setSupportSwaggerUi(true);
	        return swagger;
	    }
}
