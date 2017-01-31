/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.main;


import anf.updatereleaser.controller.VertxController;
import anf.updatereleaser.logic.Project;
import anf.updatereleaser.ui.ANFInfoDialogController;
import anf.updatereleaser.ui.ANFUpdateReleaserRootController;
import anf.updatereleaser.utils.GeneralUtils;
import anf.updatereleaser.utils.JsonMessage;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Sergio
 */
public class ANFUpdateReleaserMain extends Application {
   
   
    private ObservableList<Project> projectData;
    private BorderPane rootLayout;
    private Stage primaryStage;
    private MenuItem menuItemConnectRoot;
    private static Vertx vertx;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ANF Update Releaser");
        
        String pathIcon = "/anf/updatereleaser/ui/anf.png";
        Image icon = new Image(ANFUpdateReleaserMain.class.getResourceAsStream(pathIcon));
        primaryStage.getIcons().add(icon);
        
        initRootLayout();
        showUpdateReleaser();
        initConsumers();
        
    }
    
    
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ANFUpdateReleaserMain.class.getResource("/anf/updatereleaser/ui/ANFUpdateReleaserRoot.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
           
            primaryStage.setOnCloseRequest(event->{
               vertx.eventBus().send("controllers.vertxcontroller.shutdown_connection", 
                        JsonMessage.jsonShutdownConnnection());
               vertx.close();
               Platform.exit();
            });
            
            primaryStage.show();
            
            ANFUpdateReleaserRootController controller = loader.getController();
            
            
          
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
        
    }
    
    
    
    public void showUpdateReleaser() {
        try {
         
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ANFUpdateReleaserMain.class.getResource("/anf/updatereleaser/ui/ANFProjectsInfoWindows.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

        
            rootLayout.setCenter(personOverview);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(new FileOutputStream(new File("updatereleaser.out.log"))));
            System.setErr(new PrintStream(new FileOutputStream(new File("updatereleaser.err.log"))));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ANFUpdateReleaserMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        vertx = Vertx.vertx();
        VertxController  controller = VertxController.getInstance();
        controller.setVertx(vertx);
        controller.startClientTCP();
        //Platform.setImplicitExit(false);
        vertx.executeBlocking(future->{launch(args);}, null);
   
       
      
 }

    
    
   
    
   
    
    public ObservableList<Project> getProjectData() {
        return projectData;
    }
    
    private void consumerInfoDialog(){
        MessageConsumer<JsonObject>consumer = vertx.eventBus().consumer("main.anfupdatereleasermain.info_dialog");
        consumer.handler(res->{
            JsonObject json =res.body();
            showMessage(primaryStage, json);
            
        });
    }
    
    private void initConsumers(){
        consumerInfoDialog();
    }
    
    public void showMessage(Stage primaryStage, JsonObject json){
        Platform.runLater(()->{
         FXMLLoader loader = new FXMLLoader();
         loader.setLocation(ANFInfoDialogController.class.getResource("ANFInfoDialog.fxml"));
       try{
        AnchorPane pane = (AnchorPane) loader.load();
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("ANF UPDATE RELEASER");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(pane);
        dialogStage.setScene(scene);
        
        String pathIcon = "/anf/updatereleaser/ui/anf.png";
        Image icon = new Image(ANFUpdateReleaserMain.class.getResourceAsStream(pathIcon));
        dialogStage.getIcons().add(icon);
        
        ANFInfoDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage); //put dialog stage
        controller.setJsonMessage(json); // put json for information
        controller.setLabelText(); //make string from json && put into labelInfo
        
        dialogStage.showAndWait();
       }catch(Throwable ex){
           ex.printStackTrace();
       }
        
        });
       
        
       
    }
    
    
    
    
}
