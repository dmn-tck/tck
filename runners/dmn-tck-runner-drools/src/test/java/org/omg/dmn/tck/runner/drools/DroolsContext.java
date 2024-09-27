package org.omg.dmn.tck.runner.drools;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.omg.dmn.tck.runner.junit4.TestSuiteContext;

public class DroolsContext implements TestSuiteContext {

    private DMNRuntime dmnRuntime;
    private DMNModel dmnmodel;

    public DroolsContext() {
    }

    public DMNModel getDMNModel() {
        return dmnmodel;
    }

    public void setDMNModel(final DMNModel dmnmodel) {
        this.dmnmodel = dmnmodel;
    }

    public DMNRuntime getDMNRuntime() {
        return dmnRuntime;
    }

    public void setDMNRuntime(final DMNRuntime dmnRuntime) {
        this.dmnRuntime = dmnRuntime;
    }
}
