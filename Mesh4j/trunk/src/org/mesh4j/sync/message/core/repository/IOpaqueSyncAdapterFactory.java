package org.mesh4j.sync.message.core.repository;

import org.mesh4j.sync.adapters.ISyncAdapterFactory;

public interface IOpaqueSyncAdapterFactory extends ISyncAdapterFactory {

	String createSourceId(String sourceId);

}
