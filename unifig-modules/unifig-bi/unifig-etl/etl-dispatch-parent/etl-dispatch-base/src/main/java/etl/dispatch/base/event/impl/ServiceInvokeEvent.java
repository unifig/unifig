package etl.dispatch.base.event.impl;

import etl.dispatch.base.event.AbstractWebVisitEvent;

/**
 * @Title:与外部系统服务接口调用事件,如:webservice、RMI、HttpClient等
 *
 */

public class ServiceInvokeEvent extends AbstractWebVisitEvent {
    private static final long serialVersionUID = -6779226084902143975L;
    public ServiceInvokeEvent(Object source) {
        super(source);

    }
}
