/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.ui;


import anf.updatereleaser.controller.VertxController;
import anf.updatereleaser.main.ANFUpdateReleaserMain;
import anf.updatereleaser.utils.JsonMessage;
import io.vertx.core.Context;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

/**
 * FXML Controller class
 *
 * @author Sergio
 */
public class ANFUpdateReleaserRootController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private EventBus bus;
    private VertxController controller;
    private Vertx vertx;
   
   
    
    
    @FXML
    private MenuItem menuItemConnect;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        controller = VertxController.getInstance();
        vertx = controller.getVertx();
        bus=vertx.eventBus();
        
        
    }
    @FXML
    private void handleConnect(){
        
        bus.send("controller.vertxcontroller.server_status","Hola", ar ->{
            if(ar.succeeded()){
        
            JsonObject json = JsonMessage.messageGetStatusServer((boolean)ar.result().body());
            bus.send("ui.anfprojectsinfowindowscontrollers.list",json);
            }
        });
        
       
       }
    }
    
   
    
    
    
    

