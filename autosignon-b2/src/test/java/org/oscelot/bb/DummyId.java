package org.oscelot.bb;

import blackboard.persist.DataType;
import blackboard.persist.Id;

/**
 * Created with IntelliJ IDEA.
 * User: wfuller
 * Date: 10/01/14
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class DummyId extends Id {

  public DummyId(DataType dataType, String value) {
    super(dataType);
    this.value = value;
  }

  String value;
  @Override
  public boolean isSet() {
    return value != null;
  }

  @Override
  public boolean equals(Object o) {
    return value != null && value.equals(o);
  }

  @Override
  public int compareTo(Id id) throws ClassCastException {
    return value.compareTo(id.toString());
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toExternalString() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public void setContainer() {
  }
}
