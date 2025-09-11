
module gestao.projetos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    exports com.gestao.projetos;
    exports com.gestao.projetos.controller;
    exports com.gestao.projetos.model;
    
    opens com.gestao.projetos to javafx.fxml;
    opens com.gestao.projetos.controller to javafx.fxml;
    opens com.gestao.projetos.model to javafx.base;
}
