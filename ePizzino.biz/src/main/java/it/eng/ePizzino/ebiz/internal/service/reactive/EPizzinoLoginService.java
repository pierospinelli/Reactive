package it.eng.ePizzino.ebiz.internal.service.reactive;

import java.sql.Date;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.eng.ePizzino.ebiz.api.EPizzinoConfigDAO;
import it.pjsoft.reactive.core.api.ReactiveComponent;
import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericRequest;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;
import it.pjsoft.reactive.generic.transfer.model.msg.JaxbBody;
import it.pjsoft.reactive.generic.transfer.model.msg.MessageBody;
import it.pjsoft.reactive.generic.transfer.model.msg.RequestHeader;
import it.pjsoft.reactive.generic.transfer.model.msg.ResponseHeader;

@Component(service = ReactiveComponent.class, name = "ePizzino_login", property = { "component.qualifier=reactive",
		"component.layer=" + RtContext.LAYER_INNER_BOUNDARY })
public class EPizzinoLoginService implements ReactiveComponent<GenericResponse, GenericRequest> {
	private static int ITERATIONS = 1000;
	private static sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
	private static sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	@Reference(service = EPizzinoConfigDAO.class)
	private EPizzinoConfigDAO dao = null;

	@Override
	public GenericResponse execute(GenericRequest req) throws ReactiveException {
		RequestHeader h = req.getHeader();
		String method = h.getMethod();
		MessageBody b = req.getBody();
		GenericResponse res = null;
		switch (method) {
		case "login":
			return login(h, b);
		}
		return mkErrorResponse(h, "404", "Comando Sconosciuto");
	}

	private GenericResponse login(RequestHeader h, MessageBody b) {
		ObjectNode vals = (ObjectNode) ((JaxbBody) b).getContent();
		String user_account = vals.get("account").asText();
		String token = vals.get("token").asText();
		String fp = vals.get("foot_print").asText();
		
		Document operatore = dao.getOperatore(user_account);
		if (operatore == null)
			return mkErrorResponse(h, "404", "Operatore sconosciuto");

/**************
		String saltStr = fp.substring(0,12);
		String ciphertext = fp.substring(12, fp.length());
		
		byte[] salt = decoder.decodeBuffer(saltStr);
//		byte[] ciphertextArray = decoder.decodeBuffer(ciphertext);

		String plaintext = user_account+"/"+token;
		
		String pswd = (String) operatore.remove("pswd");
		char[] password = pswd.toCharArray();
		
//		byte[] salt = new byte[8];
//		Random random = new Random();
//		random.nextBytes(salt);

		PBEKeySpec keySpec = new PBEKeySpec(password);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithSHAAndTwofish-CBC");
		SecretKey key = keyFactory.generateSecret(keySpec);

		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, ITERATIONS);

		// Create a cipher and initialize it for encrypting
		Cipher cipher = Cipher.getInstance("PBEWithSHAAndTwofish-CBC");
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		byte[] ciphertext2 = cipher.doFinal(plaintext.getBytes());
		
		
//		String saltString = encoder.encode(salt);
		String ciphertextString = encoder.encode(ciphertext2);
		
		if(!ciphertextString.equals(ciphertext))
			return mkErrorResponse(h, "500", "Login errato");
		
*********************/
		
		
		JaxbBody body = new JaxbBody();

		return mkSuccessResponse(h, body);
	}

	private String getText(JsonNode node) {
		if (node == null)
			return null;
		if (node instanceof NullNode)
			return null;
		return node.asText();
	}

	private Date getDate(JsonNode node) {
		if (node == null)
			return null;
		if (node instanceof NullNode)
			return null;
		String s = node.asText();
		if (s == null || s.trim().length() == 0)
			return null;
		return Date.valueOf(s);
	}

	private GenericResponse mkSuccessResponse(RequestHeader h, MessageBody body) {
		ResponseHeader resH = new ResponseHeader(h, true, "200", null);
		GenericResponse res = new GenericResponse(resH, body);
		return res;
	}

	private GenericResponse mkErrorResponse(RequestHeader h, String code, String message) {
		ResponseHeader resH = new ResponseHeader(h, false, code, message);
		GenericResponse res = new GenericResponse(resH, null);
		return res;
	}

//	@Reference(service = EPizzinoConfigDAO.class)
//	public void setDAO(final EPizzinoConfigDAO dao) {
//		this.dao = dao;
//	}
//
//	public void unsetDAO(final EPizzinoConfigDAO dao) {
//		this.dao = null;
//	}

}
