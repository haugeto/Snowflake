package org.snowflake.views.velocity.scaffolding;

public class ModelObjectAdapter {

    final Object target;

    public ModelObjectAdapter(Object target) {
        this.target = target;
    }

    public String getValue() {
        return (target == null) ? "" : target.toString();
    }

}
