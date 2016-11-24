package spacebeatz;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
			Scene scene = new Scene(parent);
			primaryStage.setTitle("Space Beatz Menu");
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
            primaryStage.setResizable(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
////////////////////////////////////////////////
//VM Args Trouble should hardware acceleration//
////////////////////////////////////////////////
//Check for hardware acceleration              /
//                                             /
// -Dprism.verbose=true                        /
//                                             /
//"sw" or "j2d" for software accelerated       /
//"d3d" or "es2" for hardware accelerated      /
////////////////////////////////////////////////
//Attempt to force hardware acceleration       /
//                                             /
// -Dprism.forceGPU=true                       /
////////////////////////////////////////////////
	public static void main(String[] args) {
		launch(args);
	}
}
