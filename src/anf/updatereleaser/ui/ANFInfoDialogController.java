/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.ui;

import io.vertx.core.json.JsonObject;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Sergio
 */
public class ANFInfoDialogController{

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label labelInfo;
    @FXML
    private Button ok;
    
    
    private Stage dialogStage;
    private JsonObject json;
    
    @FXML
    private void initialize(){
        
    }
    
    
    public void setDialogStage(Stage dialogStage){
        this.dialogStage=dialogStage;
    }
    
    public void setJsonMessage(JsonObject json){
        this.json=json;
    }
    
    public void setLabelText(){
        labelInfo.setWrapText(true);
        String type = json.getString("type");
        switch (type){
            case "versionProjectAlreadyReleased":labelInfo.setText("La versión " + json.getString("version") + " del " + json.getString("projectName")+ " ya está liberada");break;
            case "statusTransfer":labelInfo.setText("Transferencia terminada");break;
            case "onTransfer":labelInfo.setText("En estos momentos se está transfiriendo un proyecto: Intente más tarde");break;
            case "ui":labelInfo.setText(json.getString("message"));break;
        }
        
    }
            
    
    
  @FXML
  private void handleOk(){
      dialogStage.close();
  }
    
}
