/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.ui;

import anf.updatereleaser.controller.VertxController;
import anf.updatereleaser.fxml.model.DeveloperFXML;
import anf.updatereleaser.fxml.model.ProjectFXML;
import anf.updatereleaser.utils.GeneralUtils;
import anf.updatereleaser.utils.JsonMessage;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Sergio
 */
public class ANFProjectsInfoWindowsController implements Initializable {

    @FXML
    private TableView <ProjectFXML> tableProject;
    @FXML
    private TableColumn <ProjectFXML , String> tableColumnProject;
    @FXML
    private TableView <DeveloperFXML> tableDevelopers;
    @FXML
    private TableColumn <DeveloperFXML , String> tableColumnName;
    @FXML
    private TableColumn <DeveloperFXML , String> tableColumnMail;
    @FXML
    private Label labelProjectName;
    @FXML
    private Label labelLastVersion;
    @FXML
    private Button buttonReleaseVersion;
    @FXML
    private Button buttonExit;
    @FXML
    private Label labelProgressInformation;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ComboBox <String> comboBoxVersion;
    
    private VertxController controller;
    private Vertx vertx;
    private EventBus bus;
    private ObservableList<ProjectFXML>list;
    private ObservableList<DeveloperFXML> listDevelopers;
    private ObservableList<String> listVersion;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        labelProgressInformation.setVisible(false);
        progressIndicator.setVisible(false);
        controller = VertxController.getInstance();
        vertx = controller.getVertx();
        bus=vertx.eventBus();
        initConsumers();
        labelLastVersion.setText("");
        labelProjectName.setText("");
        tableColumnProject.setCellValueFactory(cellData -> cellData.getValue().getName());
        tableColumnName.setCellValueFactory(cellData -> cellData.getValue().getName());
        tableColumnMail.setCellValueFactory(cellData -> cellData.getValue().getEmail());
        comboBoxVersion.setVisible(false);
        
        tableProject.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> setProjectDetail(newValue));
        
        
        
    }
    
    private void consumerList(){
        MessageConsumer<JsonObject>consumer=bus.consumer("ui.anfprojectsinfowindowscontrollers.list");
        consumer.handler(res->{
            JsonObject jsonMessage = res.body();
            //System.out.println("ANFProjectsInfoWindowsController: consumerList: jsonMessageLenght: " + jsonMessage.size());
            if(jsonMessage.getString("type").equalsIgnoreCase("get"))
                if(jsonMessage.getString("what").equalsIgnoreCase("statusServer"))
                    if(jsonMessage.getBoolean("status")){ // True : Server Alive
                        bus.send("controller.vertxcontroller.get_projects", "Give me projects", ar->{
                            JsonArray listProjects =(JsonArray) ar.result().body();
                           // System.out.println("ANFProjectsInfoWindowaController: consumerList: listProjects: "+listProjects);
                            //System.out.println(GeneralUtils.logicTofxml(listProjects).get(0).getName());
                            list = GeneralUtils.logicTofxml(listProjects);
                            vertx.executeBlocking(future -> {
                                Platform.runLater(()->{tableProject.setItems(list);});
                                
                                
                            }, resp->{return;});
                            });
//                       System.out.println(list.get(0).getName());
                    }
        });
       
    }
    
    private void setProjectDetail(ProjectFXML project){
       Platform.runLater(()->{
       if(project!=null){
        labelProjectName.setText(project.getName().get());
            //System.out.println(project.getDevelopers());
            //System.out.println(project.getDevelopers());
             listDevelopers = project.getDevelopers();
            
            listVersion=project.getVersions();
            int posLastVersion = listVersion.size() -1;
            //System.out.println(listVersion.size());
           labelLastVersion.setText(listVersion.get(posLastVersion));
            //comboBoxVersion = new ComboBox<>();
            comboBoxVersion.setItems(listVersion);
            
            //System.out.println(listDevelopers.get(0).getName().get());
            //System.out.println(listDevelopers.get(0).getEmail().get());
            tableDevelopers.setItems(listDevelopers);
            comboBoxVersion.setVisible(true);
        }else{
            labelProjectName.setText("");
            comboBoxVersion.setItems(FXCollections.observableArrayList());
            comboBoxVersion.setPromptText("Versiones disponibles");
            tableDevelopers.setItems(FXCollections.observableArrayList());
            labelLastVersion.setText("");
        }
       });
        
        
    }
    
    private void initConsumers(){
        consumerList();
        consumerStatusTransfer();
        
        //handleReleaseVersion();
    }
    
    @FXML
    private void handleReleaseVersion(){
        JsonObject json = null;
        
        if(labelProjectName.getText().isEmpty()){
           json = JsonMessage.jsonUiTableHaveNotItemSelected();
            uIValidation(json);
            
        }else if(comboBoxVersion.getSelectionModel().getSelectedItem()==null){
            
            json = JsonMessage.jsonUiComboHaveNotVersionSelected();
            uIValidation(json);
        }else{
        ProjectFXML project = tableProject.getSelectionModel().getSelectedItem();
        String version = comboBoxVersion.getValue();
        json = JsonMessage.messageReleaseVersion(project.getId().get(), version,project.getName().get());
        //System.out.println("ui: ANFProjectsInfoWindiwsController: handleReleaseVersion: json:" + json.encodePrettily());
        bus.send("controller.vertxcontroller.send_project_version_to_release", json);
        }
        
    }
    
    @FXML
    private void handleExitButton(){
        bus.send("controllers.vertxcontroller.shutdown_connection", JsonMessage.jsonShutdownConnnection());
        vertx.close();
        Platform.exit();
    }
    
    private void consumerStatusTransfer(){
        MessageConsumer<JsonObject>consumer=bus.consumer("ui.anfprojectinfowindowscontroller.status_transfer");
        consumer.handler(res->{
            JsonObject json = res.body();
            String type = json.getString("type");
            switch (type){
                case "initTransfer": updateProgresBarToInitTransfer(json);break;
                case "statusTransfer": updateProgresBarToTransferingStatus(json);break;
                case "onTransfer" : notifyProjectOnTransfer(json);break;
            }
        
        });
    }
    
    private void uIValidation(JsonObject json){
     
        bus.send("main.anfupdatereleasermain.info_dialog",json);
       
    }

    private void updateProgresBarToInitTransfer(JsonObject json) {
        Platform.runLater(()->{
        progressIndicator.setVisible(true);
        labelProgressInformation.setText("Iniciando transferencia");
        labelProgressInformation.setVisible(true);
        buttonReleaseVersion.setDisable(true);
        buttonExit.setDisable(true);
        });
        
        
        bus.send("controller.vertxcontroller.loop_get_status_transfer", JsonMessage.jsonActivateLoopStatusTransfer(json.getString("projectId")));
        
       
    }

    private void updateProgresBarToTransferingStatus(JsonObject json) {
        String status = json.getString("status");
        
        switch (status){
            case  "Transfiriendo ficheros": functionUpdateProgresBarTransferingFiles(json);break;
            case  "Transferencia satisfactoria": functionUpdateProgresBarTransferDone(json);break;
        }
    }

    private void functionUpdateProgresBarTransferingFiles(JsonObject json) {
        int totalSize = json.getInteger("totalSize");
        int hasArrived = json.getInteger("hasArrived");
       Platform.runLater(()->{
        progressIndicator.setVisible(true);
        labelProgressInformation.setText("Transfiriendo ficheros " +  "("+ hasArrived*100/totalSize+"%" +")");
        labelProgressInformation.setVisible(true);
        buttonReleaseVersion.setDisable(true);
        buttonExit.setDisable(true);
        });
    }

    private void functionUpdateProgresBarTransferDone(JsonObject json) {
        int totalSize = json.getInteger("totalSize");
        int hasArrived = json.getInteger("hasArrived");
         Platform.runLater(()->{
        progressIndicator.setVisible(true);
        labelProgressInformation.setText("Transferencia terminada " +  "("+ hasArrived*100/totalSize+"%" +")");
        labelProgressInformation.setVisible(false);
        buttonReleaseVersion.setDisable(false);
        buttonExit.setDisable(false);
        labelProgressInformation.setVisible(false);
        progressIndicator.setVisible(false);
        
        });
         functionStartTransferInfoDialog(json);
    }
    
     private void notifyProjectOnTransfer(JsonObject json) {
        Platform.runLater(()->{
        labelProgressInformation.setVisible(false);
        buttonReleaseVersion.setDisable(false);
        buttonExit.setDisable(false);
        labelProgressInformation.setVisible(false);
        progressIndicator.setVisible(false);
        
        });
        functionStartTransferInfoDialog(json);
    }

    private void functionStartTransferInfoDialog(JsonObject json) {
        //System.out.println("ui: ANFProjectsInfoWindowsController: functionStartTransferInfoDialog: json: " + json.encodePrettily());
       bus.send("main.anfupdatereleasermain.info_dialog", json);
    }
    
   

   

    
    
   
    
    
    
    
    
    
    
    
}
