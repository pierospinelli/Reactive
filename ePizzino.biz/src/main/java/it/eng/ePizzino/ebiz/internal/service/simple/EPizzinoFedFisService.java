package it.eng.ePizzino.ebiz.internal.service.simple;

import java.util.Map;

import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.ePizzino.ebiz.api.EPizzinoFedFisDAO;
import it.pjsoft.reactive.core.api.ReactiveComponent;
import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;
import it.pjsoft.reactive.generic.transfer.model.msg.JaxbBody;
import it.pjsoft.reactive.generic.transfer.model.msg.ResponseHeader;

@Component(service=ReactiveComponent.class, name="ePizzino_fedfis",
property= {"component.qualifier=reactive",
		"component.layer="+RtContext.LAYER_INNER_BOUNDARY})
public class EPizzinoFedFisService implements ReactiveComponent<String, String[]>{
	
	private EPizzinoFedFisDAO dao= null;
	
	private ObjectMapper om = new ObjectMapper();

	
	@Override
	public String execute(String[] pars) throws ReactiveException {
		try {
			GenericResponse res = new GenericResponse(null, null);
			ResponseHeader header = new ResponseHeader();
			res.setHeader(header);

			
			if(pars.length==0) {
				header.setSuccess(false);
				header.setMessage("Metodo non specificato");
				return om.writeValueAsString(res);
			}
			String col = pars[0];

			String code = null;
			if(pars.length>1)
				code = pars[1];
			res = execute(res, col, code);
			return om.writeValueAsString(res);
		} catch (ReactiveException e) {
			throw e;
		} catch (Exception e) {
			throw new ReactiveException(e);
		}
	}
	
	private GenericResponse execute(GenericResponse res, String col, String code) throws Exception {
		Document d=null;
		switch(col) {
		case "pagamentoM1byIdRicevuta":
			if(code==null) {
				res.getHeader().setSuccess(false);
				res.getHeader().setMessage("ID Ricevuta non specificato");
			}
			Map<String, Object> content = dao.getPagamentoM1ByIDRicevuta(code);
			JaxbBody body = new JaxbBody();
			body.setContent(content);
			res.setBody(body);
			res.getHeader().setSuccess(true);
			break;
		default:
			res.getHeader().setSuccess(false);
			res.getHeader().setMessage("Metodo " + col + " Sconosciuto");
		}
		
		return res;
	}
	



	@Reference(service=EPizzinoFedFisDAO.class)
	public void setDAO(final EPizzinoFedFisDAO dao) {
		this.dao = dao;
	}

	public void unsetDAO(final EPizzinoFedFisDAO dao) {
		this.dao = null;
	}


}
