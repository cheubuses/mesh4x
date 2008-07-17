package com.mesh4j.sync.message.core;

import java.util.List;

import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.Item;

public interface IMessageSyncAware {

	void beginSync(ISyncSession syncSession);
	
	void endSync(ISyncSession syncSession, List<Item> conflicts);

}
