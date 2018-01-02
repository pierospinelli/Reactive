package it.pjsoft.reactive.core.api;

//import org.apache.commons.lang3.exception.ExceptionContext;

public class ReactiveException extends Exception /*ContextedException */{
	private static final long serialVersionUID = 1L;
	private int code = 500;
	
	public int getCode(){
		return code;
	}
	
	public ReactiveException() {

	}

	public ReactiveException(String message) {
		super(message);
	}

	public ReactiveException(Throwable cause) {
		super(cause);
	}

	public ReactiveException(String message, Throwable cause) {
		super(message, cause);
	}



	

	public ReactiveException(int code) {
		super();
		this.code = code;
	}

	public ReactiveException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ReactiveException(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public ReactiveException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

}
