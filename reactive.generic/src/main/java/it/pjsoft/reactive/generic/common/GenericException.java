package it.pjsoft.reactive.generic.common;

//import org.apache.commons.lang3.exception.ExceptionContext;

public class GenericException extends Exception /*ContextedException */{
	private static final long serialVersionUID = 1L;
	private int code = 500;
	
	public int getCode(){
		return code;
	}
	
	public GenericException() {
		// TODO Auto-generated constructor stub
	}

	public GenericException(String message) {
		super(message);
	}

	public GenericException(Throwable cause) {
		super(cause);
	}

	public GenericException(String message, Throwable cause) {
		super(message, cause);
	}



	

	public GenericException(int code) {
		super();
		this.code = code;
	}

	public GenericException(int code, String message) {
		super(message);
		this.code = code;
	}

	public GenericException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public GenericException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

}
