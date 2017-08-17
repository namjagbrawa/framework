package com.bingo.framework.config;

/**
 * ConsumerConfig
 * 
 * @author william.liangf
 * @export
 */
public class ConsumerConfig extends AbstractReferenceConfig {

    private static final long serialVersionUID = 2827274711143680600L;

    // 是否为缺省
    private Boolean             isDefault;
    
    @Override
    public void setTimeout(Integer timeout) {
        super.setTimeout(timeout);
        String rmiTimeout = System.getProperty("sun.rmi.transport.tcp.responseTimeout");
        if (timeout != null && timeout > 0
                && (rmiTimeout == null || rmiTimeout.length() == 0)) {
            System.setProperty("sun.rmi.transport.tcp.responseTimeout", String.valueOf(timeout));
        }
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

}