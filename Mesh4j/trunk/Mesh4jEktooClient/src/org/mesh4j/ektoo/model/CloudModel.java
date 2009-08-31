package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class CloudModel extends AbstractModel {

	// MODEL VARIABLES
	private String baseUri;
	private String meshName = null;
	private String datasetName = null;

	// BUSINESS METHODS
	public CloudModel(String baseUri) {
		Guard.argumentNotNullOrEmptyString(baseUri, "baseUri");
		this.baseUri = baseUri;
	}

	public void setMeshName(String mesh) {
		firePropertyChange(CloudUIController.MESH_NAME_PROPERTY, this.meshName, this.meshName = mesh);
	}

	public String getMeshName() {
		return meshName;
	}

	public void setDatasetName(String dataset) {
		firePropertyChange(CloudUIController.DATASET_NAME_PROPERTY,
				this.datasetName, this.datasetName = dataset);
	}

	public String getDatasetName() {
		return datasetName;
	}

	public String getBaseUri() {
		return this.baseUri;
	}

	public void setBaseUri(String baseUri) {
		firePropertyChange(CloudUIController.SYNC_SERVER_URI,
				this.baseUri, this.baseUri = baseUri);
	}

	public String getUri() {
		String url = this.baseUri;
		if(url != null && url.length() > 0){
			if(getMeshName() != null && getMeshName().length() > 0){
				url = url.concat("/").concat(getMeshName());
				
				if(getDatasetName() != null && getDatasetName().length() > 0){
					url = url.concat("/").concat(getDatasetName());
				}
			}
		}
		return url;
	}

	public String toString() {
		return "Cloud | " + getUri();
	}

}
