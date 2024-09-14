package org.tizen.common.core.command.policy;

import java.util.HashMap;
import org.tizen.common.core.command.Policy;
import org.tizen.common.util.StringUtil;

public class PolicyRegistry {
  static class Node {
    protected Policy policy;
    
    public Node() {}
    
    public Node(Policy policy) {
      this.policy = policy;
    }
    
    protected final HashMap<String, Node> name2child = new HashMap<>();
    
    public Policy getPolicy() {
      return this.policy;
    }
    
    public void setPolicy(Policy policy) {
      this.policy = policy;
    }
    
    public Node getChild(String name) {
      return this.name2child.get(name);
    }
    
    public void addChild(String name, Node node) {
      this.name2child.put(name, node);
    }
  }
  
  protected Policy noOp = new AbstractPolicy("") {
      public <T> T adapt(Class<T> clazz) {
        return null;
      }
    };
  
  protected Node root = new Node();
  
  public PolicyRegistry(Policy... policies) {
    if (policies == null)
      return; 
    register(policies);
  }
  
  public void register(Policy... policies) {
    byte b;
    int i;
    Policy[] arrayOfPolicy;
    for (i = (arrayOfPolicy = policies).length, b = 0; b < i; ) {
      Policy policy = arrayOfPolicy[b];
      String name = policy.getName();
      Node node = createNode(name);
      if (node.getPolicy() != null)
        throw new IllegalArgumentException("Policy is duplicated"); 
      node.setPolicy(policy);
      b++;
    } 
  }
  
  public Node createNode(String path) {
    String[] fragments = StringUtil.split(path, ".");
    Node iter = this.root;
    for (int i = 0, n = fragments.length; i < n; i++) {
      Node child = iter.getChild(fragments[i]);
      if (child == null) {
        child = new Node();
        iter.addChild(fragments[i], child);
      } 
      iter = child;
    } 
    return iter;
  }
  
  public Policy getPolicy(String path) {
    if (StringUtil.isEmpty(path))
      return this.noOp; 
    String[] fragments = StringUtil.split(path, ".");
    Node iter = this.root;
    Policy policy = null;
    for (int i = 0, n = fragments.length; i < n; i++) {
      if (iter.getPolicy() != null)
        policy = iter.getPolicy(); 
      Node child = iter.getChild(fragments[i]);
      if (child == null)
        return policy; 
      iter = child;
    } 
    if (iter.getPolicy() == null)
      return policy; 
    return iter.getPolicy();
  }
  
  public String toString() {
    return String.valueOf(getClass().getSimpleName()) + "@" + Integer.toHexString(hashCode()).substring(0, 4);
  }
}
