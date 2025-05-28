module my.kursova21 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires annotations;
    requires jdk.jfr;
    requires org.slf4j;
    requires java.desktop;
    requires java.naming;

    opens my.kursova21 to javafx.fxml;
    exports model.objects.microobjects to com.almasb.fxgl.core;
    exports model.objects.macroobjects to com.almasb.fxgl.core;
    exports model.objects.nanoobjects to com.almasb.fxgl.core;
    exports model.items;
    exports my.kursova21;
    exports model.objects.nanoobjects.bullets to com.almasb.fxgl.core;
    exports model.objects.nanoobjects.chests to com.almasb.fxgl.core;
}