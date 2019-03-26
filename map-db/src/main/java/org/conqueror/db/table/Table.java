package org.conqueror.db.table;

import org.conqueror.common.serialize.ThunderSerializable;

import java.io.Closeable;
import java.util.Collection;

public interface Table<K extends ThunderSerializable,V extends ThunderSerializable>  extends Closeable {

	boolean insert(K key, V value);

	boolean update(K key, V value);

	boolean delete(K key);

	V find(K key);

	Collection<K> keys();

	Collection<V> values();

}
