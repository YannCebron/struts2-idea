public class MyClass {

  private String myField;

  public String validActionMethod() {
    return null;
  }

  public String validActionMethodWithException() throws Exception {
    return null;
  }

  public String getValidActionMethodNoUnderlyingField() {
    return null;
  }

  // invalid action-method
  public String getMyField() {
    return myField;
  }

  public String invalidActionMethodDueToWrongExceptionType() throws IllegalStateException {
    return null;
  }

  public boolean invalidActionMethodDueToWrongReturnType() throws Exception {
    return false;
  }

  public void setParam1(String value) {
  }

}