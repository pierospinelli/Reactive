package it.pjsoft.reactive.generic.transfer.model.msg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.pjsoft.reactive.generic.transfer.model.util.FeaturesAdapter;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace="http://www.eng.it/reactive/generic")
//@JsonSerialize(using=BinaryBodySerializer.class)
//@JsonDeserialize(using=BinaryBodyDeserializer.class)
public class BinaryBody extends MessageBody{
	private static final long serialVersionUID = 1L;
	
	public static final String FEATURE_MIME_TYPE="mimeType";
	public static final String FEATURE_COMPRESSED="compressed"; //true, false
	public static final String FEATURE_CYPHERED="ciphered"; //true, false
	public static final String FEATURE_SIGNATURE="signature";
	
//	@XmlElementWrapper(name="features", required=false, nillable=false)
//	@XmlElement(name="feature", required=false, nillable=false)
	@XmlJavaTypeAdapter(FeaturesAdapter.class)
	private Map<String,String> features=new TreeMap<>();

	@XmlElement(required=false, nillable=false)
	private byte[] content=null;

	@JsonIgnore
	public long getSize() {
		if(content==null)
			return 0;
		return content.length;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public Map<String, String> getFeatures() {
		return features;
	}
	
	@JsonIgnore
	public InputStream getContentInputStream() throws IOException {
		if(content==null)
			return null;
		return new ByteArrayInputStream(content);
	}

	public final void setContentInputStream(InputStream is) throws IOException {
		byte[] buff=new byte[4096];
		OutputStream os=getContentOutputStream();
		try{
			for(;;){
				int l=is.read(buff);
				if(l<0)
					break;
				os.write(buff, 0, l);
			}
		}finally{
			try {
				os.close();
			} catch (Exception e) {}
		}
	}

	@JsonIgnore
	public OutputStream getContentOutputStream() throws IOException{
		return new OutputStream(){
			ByteArrayOutputStream baos=new ByteArrayOutputStream();

			@Override
			public void write(int b) throws IOException {
				baos.write(b);
			}
			
			@Override
			public void write(byte[] b) throws IOException {
				baos.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				baos.write(b, off, len);
			}

			@Override
			public void flush() throws IOException {
				baos.flush();
			}

			@Override
			public void close() throws IOException {
				baos.close();
				BinaryBody.this.content=baos.toByteArray();
			}
						
		};
	}
	
}
