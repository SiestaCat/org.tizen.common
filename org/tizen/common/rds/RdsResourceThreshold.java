package org.tizen.common.rds;

import java.util.ArrayList;
import java.util.List;

public class RdsResourceThreshold {
  private int totalCount;
  
  private long totalSize;
  
  private int largeResourceCount;
  
  private long sumOfLargeResourceSize;
  
  private List<DeltaResourceInfo> largeResources = new ArrayList<>();
  
  public RdsResourceThreshold() {}
  
  public RdsResourceThreshold(int totalCount, long totalSize, int largeResourceCount, long sumOfLargeResourceSize, List<DeltaResourceInfo> largeResources) {
    this.totalCount = totalCount;
    this.totalSize = totalSize;
    this.largeResourceCount = largeResourceCount;
    this.sumOfLargeResourceSize = sumOfLargeResourceSize;
    this.largeResources = largeResources;
  }
  
  public int getTotalCount() {
    return this.totalCount;
  }
  
  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }
  
  public long getTotalSize() {
    return this.totalSize;
  }
  
  public void setTotalSize(long totalSize) {
    this.totalSize = totalSize;
  }
  
  public int getLargeResourceCount() {
    return this.largeResourceCount;
  }
  
  public void setLargeResourceCount(int largeResourceCount) {
    this.largeResourceCount = largeResourceCount;
  }
  
  public long getSumOfLargeResourceSize() {
    return this.sumOfLargeResourceSize;
  }
  
  public void setSumOfLargeResourceSize(long sumOfLargeResourceSize) {
    this.sumOfLargeResourceSize = sumOfLargeResourceSize;
  }
  
  public List<DeltaResourceInfo> getLargeResources() {
    return this.largeResources;
  }
  
  public void setLargeResources(List<DeltaResourceInfo> largeResources) {
    this.largeResources = largeResources;
  }
}
