package org.mesh4j.meshes.scheduling;

import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.Schedule;
import org.mesh4j.meshes.model.SchedulingOption;

public class ScheduleManager {
	
	private static ScheduleManager instance;
	private Map<String, List<String>> scheduledTasksPerMesh;
	private Scheduler scheduler;
	
	private ScheduleManager() {
		scheduledTasksPerMesh = new HashMap<String, List<String>>();
		scheduler = new Scheduler();
		scheduler.start();
	}
	
	public synchronized static ScheduleManager getInstance() {
		if (instance == null) {
			instance = new ScheduleManager();
		}
		return instance;
	}
	
	public void initialize() throws Exception {
		for (Mesh mesh : ConfigurationManager.getInstance().getAllMeshes()) {
			scheduleMesh(mesh);
		}
		
		ConfigurationManager.getInstance().addListDataListener(new MeshListListener());
	}
	
	public void scheduleMesh(Mesh mesh) {
		
		// Remove existing schedule for this mesh
		if (isScheduled(mesh))
			unscheduleMesh(mesh);
		
		// Schedule every data set in the mesh
		List<String> taskIds = new ArrayList<String>();
		for (DataSource dataSource : mesh.getDataSources()) {
			Task task = new DataSourceSyncTask(dataSource);
			Schedule schedule = dataSource.getSchedule();
			String pattern = getSchedulingPattern(schedule.getSchedulingOption());
			if (pattern != null) {
				String taskId = scheduler.schedule(pattern, task);
				taskIds.add(taskId);
			}
		}
		scheduledTasksPerMesh.put(mesh.getName(), taskIds);
	}
	
	public boolean isScheduled(Mesh mesh) {
		return scheduledTasksPerMesh.containsKey(mesh.getName());
	}
	
	public void unscheduleMesh(Mesh mesh) {
		unscheduleMesh(mesh.getName());
	}
	
	public void unscheduleMesh(String meshName) {
		List<String> taskIds = scheduledTasksPerMesh.get(meshName);
		if (taskIds == null)
			return;
		
		for (String taskId : taskIds) {	
			scheduler.deschedule(taskId);
		}
		
		scheduledTasksPerMesh.remove(meshName);
	}
	
	private String getSchedulingPattern(SchedulingOption schedulingOption) {
		
		if (schedulingOption == null)
			return null;
		
		switch (schedulingOption) {
		case FIVE_MINUTES:
			return "*/5 * * * *"; // Every five minutes
		case MANUALLY:
			return null;
		case ONE_DAY:
			return "0 12 * * *"; // Every day at 12:00
		case ONE_HOUR:
			return "5 * * * *"; // Every hour, on the our
		case TEN_MINUTES:
			return "*/10 * * * *"; // Every ten minutes
		}
		
		return null;
	}
	
	private final class MeshListListener implements ListDataListener {
		@SuppressWarnings("unchecked")
		@Override
		public void intervalRemoved(ListDataEvent e) {
			List<Mesh> currentMeshes = (List<Mesh>) e.getSource();
			List<String> currentMeshesNames = new ArrayList<String>();
			
			for (Mesh mesh : currentMeshes) {
				currentMeshesNames.add(mesh.getName());
			}
			
			List<String> meshesToUnschedule = new ArrayList<String>(); 
			
			for (String meshName : scheduledTasksPerMesh.keySet()) {
				if (!currentMeshesNames.contains(meshName)) {
					meshesToUnschedule.add(meshName);
				}
			}
			
			for (String meshName : meshesToUnschedule) {
				unscheduleMesh(meshName);
			}
		}

		@Override
		public void intervalAdded(ListDataEvent e) {
			updateMeshes(e);
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			updateMeshes(e);
		}
		
		@SuppressWarnings("unchecked")
		private void updateMeshes(ListDataEvent e) {
			List<Mesh> meshes = (List<Mesh>) e.getSource();
			for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
				scheduleMesh(meshes.get(i));
			}			
		}
	}
}
