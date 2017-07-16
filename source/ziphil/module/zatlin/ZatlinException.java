package ziphil.module.zatlin;


public class ZatlinException extends RuntimeException {

  private String message = "";
  private String fullMessage = "";

  public ZatlinException() {
    super();
  }

  public ZatlinException(String message) {
    super();
    this.message = message;
    makeFullMessage();
  }

  private void makeFullMessage() {
    StringBuilder subFullMessage = new StringBuilder();
    subFullMessage.append("Runtime Error: ");
    subFullMessage.append(message);
    fullMessage = subFullMessage.toString();
  }

  public String getMessage() {
    return this.message;
  }

  public String getFullMessage() {
    return this.fullMessage;
  }

}