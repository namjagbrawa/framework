package com.bingo.framework.registry.zookeeper;

import com.bingo.framework.common.URL;
import com.bingo.framework.registry.Registry;
import com.bingo.framework.registry.support.AbstractRegistryFactory;
import com.bingo.framework.remoting.zookeeper.ZookeeperTransporter;

/**
 * ZookeeperRegistryFactory.
 * 
 * @author william.liangf
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {
	
	private ZookeeperTransporter zookeeperTransporter;

    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
		this.zookeeperTransporter = zookeeperTransporter;
	}

	public Registry createRegistry(URL url) {
        return new ZookeeperRegistry(url, zookeeperTransporter);
    }

}