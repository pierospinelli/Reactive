package it.pjsoft.reactive.core.shell.internal;


import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.shell.TestEntity;
import it.pjsoft.reactive.core.shell.TestRepository;
import osgi.enroute.debug.api.Debug;

@Component(service = ReactiveSetEntityTest.class,
		name = "setEntity.command", 
		property = { Debug.COMMAND_SCOPE + "=reactive",
					Debug.COMMAND_FUNCTION + "=setEntity" 
					})
public class ReactiveSetEntityTest {

	
	public void setEntity(String id) throws Exception{
		TestEntity e = TestRepository.setCurrentEntity(id);
		if(e==null) {
			System.out.println("No entity with id "+id);
			return;
		}
		System.out.println("Current entity "+e.getId()+" - "+e.getName());
	}

}
