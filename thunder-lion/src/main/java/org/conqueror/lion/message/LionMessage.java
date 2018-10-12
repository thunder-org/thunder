package org.conqueror.lion.message;

import org.conqueror.lion.serialize.LionSerializable;


public interface LionMessage<T extends LionSerializable> extends LionSerializable<T> {

}
