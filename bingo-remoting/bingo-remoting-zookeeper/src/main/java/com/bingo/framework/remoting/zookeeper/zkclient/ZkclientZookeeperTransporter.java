package com.bingo.framework.remoting.zookeeper.zkclient;

import com.bingo.framework.common.URL;
import com.bingo.framework.remoting.zookeeper.ZookeeperClient;
import com.bingo.framework.remoting.zookeeper.ZookeeperTransporter;

public class ZkclientZookeeperTransporter implements ZookeeperTransporter {

	public ZookeeperClient connect(URL url) {
		return new ZkclientZookeeperClient(url);
	}

}
