package Batch1_POSG4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import Batch1_POSG4.dao.LoginSessionDAO;
import Batch1_POSG4.util.Session;

public class App extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            closeCurrentSession();
        }));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("view/POSLogin.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // JavaFX‐level hook: called on Platform.exit(), last window closed, etc.
        closeCurrentSession();
    }

    private void closeCurrentSession() {
        long sid = Session.get().getSessionId();
        if (sid != 0) {
            try {
                new LoginSessionDAO().closeSession(sid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
