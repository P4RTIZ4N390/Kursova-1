package my.kursova21;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import javafx.scene.input.KeyCode;

public class Lab5 extends Lab4{
    public static void main(String[] args) {launch(args);}

    @Override
    protected void setupControls() {
        super.setupControls();
        Input input = FXGL.getInput();

        input.addAction(new UserAction("Search") {
            @Override
            protected void onActionBegin() {
                super.onActionBegin();

            }
        }, KeyCode.S);
    }
}
