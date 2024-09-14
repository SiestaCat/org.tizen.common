package org.tizen.common;

import org.eclipse.core.resources.IProject;

public interface ITizenProjectAdapter {
  boolean canHandle(IProject paramIProject);
  
  ITizenProject getTizenProject(IProject paramIProject);
}
