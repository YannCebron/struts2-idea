public class MyClass {

public String validActionMethod() { return null; }
public String validActionMethodWithException() throws Exception { return null; }
public String invalidActionMethodDueToWrongExceptionType() throws IllegalStateException { return null; }
public boolean invalidActionMethodDueToWrongReturnType() throws Exception { return null; }

public void setParam1(String value) {};

}