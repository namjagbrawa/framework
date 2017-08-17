
package com.bingo.framework.rpc.cluster.support;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.bingo.framework.common.Constants;
import com.bingo.framework.common.URL;
import com.bingo.framework.common.extension.ExtensionLoader;
import com.bingo.framework.common.logger.Logger;
import com.bingo.framework.common.logger.LoggerFactory;
import com.bingo.framework.common.utils.ConfigUtils;
import com.bingo.framework.common.utils.NamedThreadFactory;
import com.bingo.framework.rpc.Invocation;
import com.bingo.framework.rpc.Invoker;
import com.bingo.framework.rpc.Result;
import com.bingo.framework.rpc.RpcException;
import com.bingo.framework.rpc.RpcInvocation;
import com.bingo.framework.rpc.RpcResult;
import com.bingo.framework.rpc.cluster.Directory;
import com.bingo.framework.rpc.cluster.Merger;
import com.bingo.framework.rpc.cluster.merger.MergerFactory;

/**
 * @author <a href="mailto:gang.lvg@alibaba-inc.com">kimi</a>
 */
@SuppressWarnings( "unchecked" )
public class MergeableClusterInvoker<T> implements Invoker<T> {

    private static final Logger log = LoggerFactory.getLogger(MergeableClusterInvoker.class);

    private ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("mergeable-cluster-executor", true));
    
    private final Directory<T> directory;

    public MergeableClusterInvoker(Directory<T> directory) {
        this.directory = directory;
    }

    @SuppressWarnings("rawtypes")
	public Result invoke(final Invocation invocation) throws RpcException {
        List<Invoker<T>> invokers = directory.list(invocation);
        
        String merger = getUrl().getMethodParameter( invocation.getMethodName(), Constants.MERGER_KEY );
        if ( ConfigUtils.isEmpty(merger) ) { // 如果方法不需要Merge，退化为只调一个Group
            for(final Invoker<T> invoker : invokers ) {
                if (invoker.isAvailable()) {
                    return invoker.invoke(invocation);
                }
            }
            return invokers.iterator().next().invoke(invocation);
        }
        
        Class<?> returnType;
        try {
            returnType = getInterface().getMethod(
                    invocation.getMethodName(), invocation.getParameterTypes() ).getReturnType();
        } catch ( NoSuchMethodException e ) {
            returnType = null;
        }
        
        Map<String, Future<Result>> results = new HashMap<String, Future<Result>>();
        for( final Invoker<T> invoker : invokers ) {
            Future<Result> future = executor.submit( new Callable<Result>() {
                public Result call() throws Exception {
                    return invoker.invoke(new RpcInvocation(invocation, invoker));
                }
            } );
            results.put( invoker.getUrl().getServiceKey(), future );
        }

        Object result = null;
        
        List<Result> resultList = new ArrayList<Result>( results.size() );
        
        int timeout = getUrl().getMethodParameter( invocation.getMethodName(), Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT );
        for ( Map.Entry<String, Future<Result>> entry : results.entrySet() ) {
            Future<Result> future = entry.getValue();
            try {
                Result r = future.get(timeout, TimeUnit.MILLISECONDS);
                if (r.hasException()) {
                    log.error(new StringBuilder(32).append("Invoke ")
                                  .append(getGroupDescFromServiceKey(entry.getKey()))
                                  .append(" failed: ")
                                  .append(r.getException().getMessage()).toString(),
                              r.getException());
                } else {
                    resultList.add(r);
                }
            } catch ( Exception e ) {
                throw new RpcException( new StringBuilder( 32 )
                                                .append( "Failed to invoke service " )
                                                .append( entry.getKey() )
                                                .append( ": " )
                                                .append( e.getMessage() ).toString(),
                                        e );
            }
        }
        
        if (resultList.size() == 0) {
            return new RpcResult((Object)null);
        } else if (resultList.size() == 1) {
            return resultList.iterator().next();
        }

        if (returnType == void.class) {
            return new RpcResult((Object)null);
        }

        if ( merger.startsWith(".") ) {
            merger = merger.substring(1);
            Method method;
            try {
                method = returnType.getMethod( merger, returnType );
            } catch ( NoSuchMethodException e ) {
                throw new RpcException( new StringBuilder( 32 )
                                                .append( "Can not merge result because missing method [ " )
                                                .append( merger )
                                                .append( " ] in class [ " )
                                                .append( returnType.getClass().getName() )
                                                .append( " ]" )
                                                .toString() );
            }
            if ( method != null ) {
                if ( !Modifier.isPublic( method.getModifiers() ) ) {
                    method.setAccessible( true );
                }
                result = resultList.remove( 0 ).getValue();
                try {
                    if ( method.getReturnType() != void.class
                            && method.getReturnType().isAssignableFrom( result.getClass() ) ) {
                        for ( Result r : resultList ) {
                            result = method.invoke( result, r.getValue() );
                        }
                    } else {
                        for ( Result r : resultList ) {
                            method.invoke( result, r.getValue() );
                        }
                    }
                } catch ( Exception e ) {
                    throw new RpcException( 
                            new StringBuilder( 32 )
                                    .append( "Can not merge result: " )
                                    .append( e.getMessage() ).toString(), 
                            e );
                }
            } else {
                throw new RpcException(
                        new StringBuilder( 32 )
                                .append( "Can not merge result because missing method [ " )
                                .append( merger )
                                .append( " ] in class [ " )
                                .append( returnType.getClass().getName() )
                                .append( " ]" )
                                .toString() );
            }
        } else {
            Merger resultMerger;
            if (ConfigUtils.isDefault(merger)) {
                resultMerger = MergerFactory.getMerger(returnType);
            } else {
                resultMerger = ExtensionLoader.getExtensionLoader(Merger.class).getExtension(merger);
            }
            if (resultMerger != null) {
                List<Object> rets = new ArrayList<Object>(resultList.size());
                for(Result r : resultList) {
                    rets.add(r.getValue());
                }
                result = resultMerger.merge(
                        rets.toArray((Object[])Array.newInstance(returnType, 0)));
            } else {
                throw new RpcException( "There is no merger to merge result." );
            }
        }
        return new RpcResult( result );
    }

    public Class<T> getInterface() {
        return directory.getInterface();
    }

    public URL getUrl() {
        return directory.getUrl();
    }

    public boolean isAvailable() {
        return directory.isAvailable();
    }

    public void destroy() {
        directory.destroy();
    }

    private String getGroupDescFromServiceKey(String key) {
        int index = key.indexOf("/");
        if (index > 0) {
            return new StringBuilder(32).append("group [ ")
                .append(key.substring(0, index)).append(" ]").toString();
        }
        return key;
    }
}
