package org.tizen.common.daemon;

public interface Server {
  void boot() throws ServerException;
  
  void down() throws ServerException;
}
