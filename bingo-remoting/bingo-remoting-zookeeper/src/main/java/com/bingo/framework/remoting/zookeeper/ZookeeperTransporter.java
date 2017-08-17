package com.bingo.framework.remoting.zookeeper;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.Adaptive;
import com.bingo.framework.common.extension.SPI;

@SPI("zkclient")
public interface ZookeeperTransporter {

	@Adaptive({Constants.CLIENT_KEY, Constants.TRANSPORTER_KEY})
	ZookeeperClient connect(URL url);

}
