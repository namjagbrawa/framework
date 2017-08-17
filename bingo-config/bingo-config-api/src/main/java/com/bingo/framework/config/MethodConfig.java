package com.bingo.framework.config;

import java.util.List;

import com.bingo.framework.common.Constants;
import com.bingo.framework.config.support.Parameter;

/**
 * MethodConfig
 * 
 * @author william.liangf
 * @export
 */
public class MethodConfig extends AbstractMethodConfig {

    private static final long serialVersionUID = 884908855422675941L;

    // 方法名
    private String            name;
    
    // 统计参数
    private Integer           stat;

    // 是否重试
    private Boolean           retry;

    // 是否为可靠异步
    private Boolean           reliable;

    // 方法使用线程数限制
    private Integer           executes;
    
    // 是否过时
    private Boolean           deprecated;

    // 是否需要开启stiky策略
    private Boolean           sticky;

    // 是否需要返回
    private Boolean           isReturn;
    
    //异步调用回调实例
    private Object            oninvoke;

    //异步调用回调方法
    private String            oninvokeMethod;
    
    //异步调用回调实例
    private Object            onreturn;

    //异步调用回调方法
    private String            onreturnMethod;
    
    //异步调用异常回调实例
    private Object            onthrow;
    
    //异步调用异常回调方法
    private String            onthrowMethod;
    
    private List<ArgumentConfig> arguments;
    
    @Parameter(excluded = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkMethodName("name", name);
        this.name = name;
        if (id == null || id.length() == 0) {
            id = name;
        }
    }
    
    public Integer getStat() {
        return stat;
    }
    
    @Deprecated
    public void setStat(Integer stat) {
        this.stat = stat;
    }

    @Deprecated
    public Boolean isRetry() {
        return retry;
    }

    @Deprecated
    public void setRetry(Boolean retry) {
        this.retry = retry;
    }

    @Deprecated
    public Boolean isReliable() {
        return reliable;
    }

    @Deprecated
    public void setReliable(Boolean reliable) {
        this.reliable = reliable;
    }

    public Integer getExecutes() {
        return executes;
    }

    public void setExecutes(Integer executes) {
        this.executes = executes;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    @SuppressWarnings("unchecked")
    public void setArguments(List<? extends ArgumentConfig> arguments) {
        this.arguments = (List<ArgumentConfig>) arguments;
    }

    public List<ArgumentConfig> getArguments() {
        return arguments;
    }
    
    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    @Parameter(key = Constants.ON_RETURN_INSTANCE_KEY, excluded = true, attribute = true)
    public Object getOnreturn() {
        return onreturn;
    }
    
    public void setOnreturn(Object onreturn) {
        this.onreturn = onreturn;
    }
    
    @Parameter(key = Constants.ON_RETURN_METHOD_KEY, excluded = true, attribute = true)
    public String getOnreturnMethod() {
        return onreturnMethod;
    }

    public void setOnreturnMethod(String onreturnMethod) {
        this.onreturnMethod = onreturnMethod;
    }

    @Parameter(key = Constants.ON_THROW_INSTANCE_KEY, excluded = true, attribute = true)
    public Object getOnthrow() {
        return onthrow;
    }

    public void setOnthrow(Object onthrow) {
        this.onthrow = onthrow;
    }
    
    @Parameter(key = Constants.ON_THROW_METHOD_KEY, excluded = true, attribute = true)
    public String getOnthrowMethod() {
        return onthrowMethod;
    }

    public void setOnthrowMethod(String onthrowMethod) {
        this.onthrowMethod = onthrowMethod;
    }
    
    @Parameter(key = Constants.ON_INVOKE_INSTANCE_KEY, excluded = true, attribute = true)
    public Object getOninvoke() {
        return oninvoke;
    }
    
    public void setOninvoke(Object oninvoke) {
        this.oninvoke = oninvoke;
    }
    
    @Parameter(key = Constants.ON_INVOKE_METHOD_KEY, excluded = true, attribute = true)
    public String getOninvokeMethod() {
        return oninvokeMethod;
    }
    
    public void setOninvokeMethod(String oninvokeMethod) {
        this.oninvokeMethod = oninvokeMethod;
    }

    public Boolean isReturn() {
        return isReturn;
    }

    public void setReturn(Boolean isReturn) {
        this.isReturn = isReturn;
    }

}