package org.conqueror.lion.db.table;

import org.conqueror.lion.serialize.LionSerializable;

import java.io.Closeable;
import java.util.Collection;

public interface Table<K extends LionSerializable,V extends LionSerializable>  extends Closeable {

	boolean insert(K key, V value);

	boolean update(K key, V value);

	boolean delete(K key);

	V find(K key);

	Collection<K> keys();

	Collection<V> values();

}
