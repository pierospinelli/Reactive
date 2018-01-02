package it.pjsoft.reactive.core.shell.internal;

import java.util.Map;

import org.apache.karaf.scheduler.ScheduleOptions;
import org.apache.karaf.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = SchedulerCtrl.class)
public class SchedulerCtrl {

	private Scheduler scheduler;
	public Map<Object, ScheduleOptions> jobs = null;
	
	@Reference
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	public void unsetScheduler(Scheduler scheduler) {
		this.scheduler = null;
	}

	public Scheduler getScheduler() {
		return scheduler;
	}
	

}
