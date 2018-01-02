package it.pjsoft.reactive.core.shell.internal;

import java.util.Map;

import org.apache.karaf.scheduler.ScheduleOptions;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.debug.api.Debug;

@Component(service = StartScheduler.class, name = "startScheduler.command", property = {
		Debug.COMMAND_SCOPE + "=reactive", Debug.COMMAND_FUNCTION + "=startScheduler" })
public class StartScheduler {

	private SchedulerCtrl schedulerCtrl;
		
	@Reference
	public void setSchedulerCtrl(SchedulerCtrl schedulerCtrl) {
		this.schedulerCtrl = schedulerCtrl;
	}
	public void unsetSchedulerCtrl(SchedulerCtrl schedulerCtrl) {
		this.schedulerCtrl = null;
	}


	public void startScheduler() throws Exception {
		if(schedulerCtrl.jobs!=null) {
			for (Map.Entry<Object, ScheduleOptions> entry : schedulerCtrl.jobs.entrySet()) {
				schedulerCtrl.getScheduler().schedule(entry.getKey(), entry.getValue());
			}
			schedulerCtrl.jobs = null;
		}
	}
}