package org.conqueror.lion.db.table;

import org.conqueror.lion.exceptions.db.DBException;
import org.conqueror.lion.serialize.LionSerializable;
import org.mapdb.DB;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public abstract class JobTable<K extends LionSerializable, V extends LionSerializable> extends ReentrantLock implements Table<K, V> {

	private HTreeMap<K, V> map;

	public JobTable(DB db) {
		map = db.hashMap(getTableName(this.getClass()), getKeySerializer(), getValueSerializer()).createOrOpen();
	}

	@Override
	public boolean insert(K key, V value) {
		lock();
		try {
			return (!map.isClosed()) && map.putIfAbsentBoolean(key, value);
		} finally {
			unlock();
		}
	}

	@Override
	public boolean update(K key, V value) {
		lock();
		try {
			return (!map.isClosed()) && Objects.nonNull(map.get(key)) && Objects.nonNull(map.put(key, value));
		} finally {
			unlock();
		}
	}

	@Override
	public boolean delete(K key) {
		lock();
		try {
			return (!map.isClosed()) && Objects.nonNull(map.remove(key));
		} finally {
			unlock();
		}
	}

	@Override
	public V find(K key) {
		return map.isClosed()? null : map.get(key);
	}

	@Override
	public Collection<K> keys() {
		return map.isClosed()? null : map.getKeys();
	}

	@Override
	public Collection<V> values() {
		return map.isClosed()? null : map.getValues();
	}

	@Override
	public void close() {
		lock();
		try {
			if (!map.isClosed()) map.close();
		} finally {
			unlock();
		}
	}

	public abstract Serializer<K> getKeySerializer();

	public abstract Serializer<V> getValueSerializer();

	public static <T extends JobTable> T newInstance(Class<T> jobTableClass, DB db) throws DBException {
		try {
			return jobTableClass.getDeclaredConstructor(db.getClass()).newInstance(db);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new DBException(e);
		}
	}

	public static String getTableName(@Nonnull Class<? extends Table> tableClass) {
		return tableClass.getName();
	}

}
