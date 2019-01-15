package org.conqueror.es.index.updater;

import java.util.Map;


public interface Updater {

	Object update(Map<String, Object> source, Object preprocessed);

}
