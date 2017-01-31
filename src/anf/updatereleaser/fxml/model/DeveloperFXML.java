/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.fxml.model;

import anf.updatereleaser.logic.Developer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Sergio
 */
public class DeveloperFXML {
    
        private StringProperty email;
        private StringProperty name;
        private StringProperty id;

        public DeveloperFXML(Developer developer){
            email = new SimpleStringProperty(developer.email);
            name =new SimpleStringProperty(developer.name);
            id = new SimpleStringProperty(developer.id);
        }

    public StringProperty getEmail() {
        return email;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getId() {
        return id;
    }
        
        
    
}
