/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.controller;

import anf.updatereleaser.fxml.model.ProjectFXML;
import anf.updatereleaser.logic.Project;
import anf.updatereleaser.main.ANFUpdateReleaserMain;
import anf.updatereleaser.verticles.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Sergio
 */
public class VertxController{
    
    private Vertx vertx;
    private EventBus bus;
    private boolean isAlive;
    private static VertxController vertxController;
    
    
    private VertxController(){
                
    }
    
    public void startClientTCP(){
        Verticle clientTCP = new Client();
        vertx.deployVerticle(clientTCP);
    }
    
    public static VertxController getInstance(){
        if(vertxController == null){
            vertxController = new VertxController();
        }
        return vertxController;
    }

    public void setVertx(Vertx vertx) {
        this.vertx = vertx;
        bus = vertx.eventBus();
        initConsumers();
    }

    public Vertx getVertx() {
        return vertx;
    }
    
    public void  consumerGetProjects(){
        MessageConsumer <String> consumer = bus.consumer("controller.vertxcontroller.get_projects");
        consumer.handler(res->{
            bus.send("verticles.client.get_projects_info","Give me projects",replay->{
                if(replay.succeeded()){
                    res.reply(replay.result().body());
                }
            });
        });
        
       
           
          
        
    }
    
    public void consumerServerStatus(){
        MessageConsumer <String> consumer = bus.consumer("controller.vertxcontroller.server_status");
        consumer.handler(res->{
            bus.send("verticles.client.server_status","Hola" ,replay->{
           if(replay.succeeded()){
               //System.out.println("VertxController:serverStatus:replay: "+ replay.result().body());
             isAlive = Boolean.valueOf(String.valueOf(replay.result().body())) ;
             boolean result =(boolean) replay.result().body();
             res.reply(result);
            
             }});
        
        });
        
        
    }
    
    private void consumerSendProjectVersionToRelease(){
        MessageConsumer<JsonObject>consumer=bus.consumer("controller.vertxcontroller.send_project_version_to_release");
        consumer.handler(res->{
            //System.out.println("controller: VertxController: consumerSendProjectVersionToRelease: res.body(): " + res.body().encodePrettily());
            bus.send("verticles.client.send_project_version_to_release",res.body());
        });
    }
    
    private void consumerVersionAlreadyReleased(){
        MessageConsumer<JsonObject>consumer=bus.consumer("controller.vertxcontroller.version_already_released");
        consumer.handler(res->{
           bus.send("main.anfupdatereleasermain.info_dialog",res.body());
        });
    }
    
    private void consumerStatusTransfer(){
        MessageConsumer<JsonObject>consumer = bus.consumer("controllers.vertxcontroller.status_transfer");
        consumer.handler(res->{
            bus.send("ui.anfprojectinfowindowscontroller.status_transfer", res.body());
        });
    }
    
    private void consumerActivateLoopGetStatusTransfer(){
        MessageConsumer<JsonObject>consumer = bus.consumer("controller.vertxcontroller.loop_get_status_transfer");
        consumer.handler(res->{
        bus.send("verticles.client.activate_loop_get_status_transfer", res.body());
        
        });
    }
    
    public void consumerOnTransfer(){
        MessageConsumer<JsonObject>consumer = bus.consumer("controllers.vertxcontroller.on_transfer");
        consumer.handler(res->{
        bus.send("ui.anfprojectinfowindowscontroller.status_transfer",res.body());
        });
    }
    
    public void consumerServerExploitationDown(){
        MessageConsumer<JsonObject>consumer=bus.consumer("controllers.vertxcontroller.server_exploitation_down");
        consumer.handler(res->{
            bus.send("main.anfupdatereleasermain.info_dialog",res.body());
        });
    }
    
    public void consumerShutdownConnection(){
        MessageConsumer<JsonObject>consumer = bus.consumer("controllers.vertxcontroller.shutdown_connection");
        consumer.handler(res->{bus.send("verticles.client.shutdown_connection", res.body());});
        
    }
    
    private void initConsumers(){
        consumerServerStatus();
        consumerGetProjects();
        consumerSendProjectVersionToRelease();
        consumerVersionAlreadyReleased();
        consumerStatusTransfer();
        consumerActivateLoopGetStatusTransfer();
        consumerOnTransfer();
        consumerServerExploitationDown();
        consumerShutdownConnection();
    }
    
    
    
    
    /*
    ObservableList <ProjectFXML> projectData = FXCollections.observableArrayList();
    
    
    
    */
               
    
    
    
    
    
}
