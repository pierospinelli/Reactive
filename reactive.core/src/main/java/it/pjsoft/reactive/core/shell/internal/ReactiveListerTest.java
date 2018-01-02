package it.pjsoft.reactive.core.shell.internal;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.api.ReactiveComponent;
import it.pjsoft.reactive.core.api.ReactiveMethod;
import it.pjsoft.reactive.core.internal.Activator;
import osgi.enroute.debug.api.Debug;

@Component(service = ReactiveListerTest.class, name = "list.command", property = { Debug.COMMAND_SCOPE + "=reactive",
		Debug.COMMAND_FUNCTION + "=list" })
public class ReactiveListerTest {

	public void list() {
		try {
			ServiceReference[] sr = Activator.getContext().getServiceReferences(ReactiveComponent.class.getName(), null);
			int i = 0;
			if (sr != null) {
				for (ServiceReference r : sr) {
					System.out.println("- Service " + ++i);
					for (String k : r.getPropertyKeys())
						System.out.println("\t- " + k + ":" + r.getProperty(k));
				}
			}
			sr = Activator.getContext().getServiceReferences(ReactiveMethod.class.getName(), null);
			i = 0;
			if (sr != null) {
				for (ServiceReference r : sr) {
					System.out.println("- Method " + ++i);
					for (String k : r.getPropertyKeys())
						System.out.println("\t- " + k + ":" + r.getProperty(k));
				}
			}
			System.out.println("OK");
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
	}

}
