package etl.dispatch.base.event;

import org.springframework.context.ApplicationEvent;

public abstract class AbstractWebVisitEvent extends ApplicationEvent {
    private static final long serialVersionUID = -4419575410503118959L;
    public AbstractWebVisitEvent(Object source) {
        super(source);
    }
}
