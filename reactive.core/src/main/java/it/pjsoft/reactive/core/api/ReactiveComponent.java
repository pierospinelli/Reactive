package it.pjsoft.reactive.core.api;

public interface ReactiveComponent<T, I> {
	public T execute(I input) throws ReactiveException;
}
