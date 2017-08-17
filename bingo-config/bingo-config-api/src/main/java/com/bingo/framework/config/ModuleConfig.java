package com.bingo.framework.config;

import java.util.ArrayList;
import java.util.List;

import com.bingo.framework.config.support.Parameter;

/**
 * ModuleConfig
 * 
 * @author william.liangf
 * @export
 */
public class ModuleConfig extends AbstractConfig {

    private static final long    serialVersionUID = 5508512956753757169L;

    // 模块名称
    private String               name;

    // 模块版本
    private String               version;

    // 应用负责人
    private String               owner;

    // 组织名(BU或部门)
    private String               organization;

    // 注册中心
    private List<RegistryConfig> registries;

    // 服务监控
    private MonitorConfig        monitor;

    // 是否为缺省
    private Boolean              isDefault;

    public ModuleConfig() {
    }
    
    public ModuleConfig(String name) {
        setName(name);
    }
    
    @Parameter(key = "module", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName("name", name);
        this.name = name;
        if (id == null || id.length() == 0) {
            id = name;
        }
    }

    @Parameter(key = "module.version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        checkName("owner", owner);
        this.owner = owner;
    }

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
	    checkName("organization", organization);
		this.organization = organization;
	}

    public RegistryConfig getRegistry() {
        return registries == null || registries.size() == 0 ? null : registries.get(0);
    }

    public void setRegistry(RegistryConfig registry) {
        List<RegistryConfig> registries = new ArrayList<RegistryConfig>(1);
        registries.add(registry);
        this.registries = registries;
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    @SuppressWarnings({ "unchecked" })
    public void setRegistries(List<? extends RegistryConfig> registries) {
        this.registries = (List<RegistryConfig>)registries;
    }

    public MonitorConfig getMonitor() {
        return monitor;
    }

    public void setMonitor(MonitorConfig monitor) {
        this.monitor = monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = new MonitorConfig(monitor);
    }

    public Boolean isDefault() {
        return isDefault;
    }

    public void setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

}