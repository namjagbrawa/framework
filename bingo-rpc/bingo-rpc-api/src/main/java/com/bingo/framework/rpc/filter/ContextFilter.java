package com.bingo.framework.rpc.filter;

import java.util.HashMap;
import java.util.Map;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.extension.Activate;
import com.bingo.framework.rpc.Filter;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcContext;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.RpcInvocation;

/**
 * ContextInvokerFilter
 * 
 * @author william.liangf
 */
@Activate(group = Constants.PROVIDER, order = -10000)
public class ContextFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> attachments = invocation.getAttachments();
        if (attachments != null) {
            attachments = new HashMap<String, String>(attachments);
            attachments.remove(Constants.PATH_KEY);
            attachments.remove(Constants.GROUP_KEY);
            attachments.remove(Constants.VERSION_KEY);
            attachments.remove(Constants.BINGO_VERSION_KEY);
            attachments.remove(Constants.TOKEN_KEY);
            attachments.remove(Constants.TIMEOUT_KEY);
        }
        RpcContext.getContext()
                .setInvoker(invoker)
                .setInvocation(invocation)
//                .setAttachments(attachments)  // modified by lishen
                .setLocalAddress(invoker.getUrl().getHost(),
                        invoker.getUrl().getPort());

        // modified by lishen
        if (attachments != null) {
            if (RpcContext.getContext().getAttachments() != null) {
                RpcContext.getContext().getAttachments().putAll(attachments);
            } else {
                RpcContext.getContext().setAttachments(attachments);
            }
        }

        if (invocation instanceof RpcInvocation) {
            ((RpcInvocation)invocation).setInvoker(invoker);
        }
        try {
            return invoker.invoke(invocation);
        } finally {
            RpcContext.removeContext();
        }
    }
}