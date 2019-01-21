package org.conqueror.es.client.index.updater;

import java.util.Map;


public interface Updater {

	Object update(Map<String, Object> source, Object preprocessed);

}
