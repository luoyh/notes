

public class JavaScreenCapture {
  public static void main(String[] args) {
    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle screenRectangle = new Rectangle(dimension);
    Robot robot = new Robot();
    ImageIO.write(robot.createScreenCapture(screenRectangle), "png", new File("/tmp/screen.png"));
  }
}
