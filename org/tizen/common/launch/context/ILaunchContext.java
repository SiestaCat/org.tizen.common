package org.tizen.common.launch.context;

public interface ILaunchContext<Object> {
  Object[] getKeys();
  
  Object getValue(Object paramObject);
  
  Object setValue(Object paramObject1, Object paramObject2);
}
