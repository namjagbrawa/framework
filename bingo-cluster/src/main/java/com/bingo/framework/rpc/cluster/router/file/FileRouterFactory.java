
package com.bingo.framework.rpc.cluster.router.file;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.utils.IOUtils;
import com.bingo.framework.rpc.cluster.Router;
import com.bingo.framework.rpc.cluster.RouterFactory;
import com.bingo.framework.rpc.cluster.router.script.ScriptRouterFactory;

public class FileRouterFactory implements RouterFactory {
    
    public static final String NAME = "file";
    
    private RouterFactory routerFactory;
    
    public void setRouterFactory(RouterFactory routerFactory) {
        this.routerFactory = routerFactory;
    }
    
    public Router getRouter(URL url) {
        try {
            // File URL 转换成 其它Route URL，然后Load
            // file:///d:/path/to/route.js?router=script ==> script:///d:/path/to/route.js?type=js&rule=<file-content>
            String protocol = url.getParameter(Constants.ROUTER_KEY, ScriptRouterFactory.NAME); // 将原类型转为协议
            String type = null; // 使用文件后缀做为类型
            String path = url.getPath();
            if (path != null) {
                int i = path.lastIndexOf('.');
                if (i > 0) {
                    type = path.substring(i + 1);
                }
            }
            String rule = IOUtils.read(new FileReader(new File(url.getAbsolutePath())));
            URL script = url.setProtocol(protocol).addParameter(Constants.TYPE_KEY, type).addParameterAndEncoded(Constants.RULE_KEY, rule);
            
            return routerFactory.getRouter(script);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}