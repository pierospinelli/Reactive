package it.pjsoft.reactive.core.shell.internal;

import java.util.Map;

import org.apache.karaf.scheduler.ScheduleOptions;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import osgi.enroute.debug.api.Debug;

@Component(service = StopScheduler.class, name = "stopScheduler.command", property = {
		Debug.COMMAND_SCOPE + "=reactive", Debug.COMMAND_FUNCTION + "=stopScheduler" })
public class StopScheduler {

	private SchedulerCtrl schedulerCtrl;
		
	@Reference
	public void setSchedulerCtrl(SchedulerCtrl schedulerCtrl) {
		this.schedulerCtrl = schedulerCtrl;
	}
	public void unsetSchedulerCtrl(SchedulerCtrl schedulerCtrl) {
		this.schedulerCtrl = null;
	}


	public void stopScheduler() throws Exception {
		if(!schedulerCtrl.getScheduler().getJobs().isEmpty()) {
			schedulerCtrl.jobs = schedulerCtrl.getScheduler().getJobs();
			for (Map.Entry<Object, ScheduleOptions> entry : schedulerCtrl.jobs.entrySet()) {
				schedulerCtrl.getScheduler().unschedule(entry.getValue().name());
			}
		}
	}
}