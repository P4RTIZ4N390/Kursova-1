package model;

import com.almasb.fxgl.entity.component.Component;

public class TriggerComponent extends Component {
    private double radius;

    public TriggerComponent(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}