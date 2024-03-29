package org.mesh4j.sync.message;

public interface IMessage {

	String getProtocol();
	
	String getMessageType();
	
	String getSessionId();
	
	String getData();

	IEndpoint getEndpoint();

	String getOrigin();
	
	void setOrigin(String origin);

	boolean isAckRequired();

	int getSessionVersion();
	
}
