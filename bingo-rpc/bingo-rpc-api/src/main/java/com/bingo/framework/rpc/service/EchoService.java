package com.bingo.framework.rpc.service;

/**
 * Echo service.
 * 
 * @author qian.lei
 * @export
 */
public interface EchoService {

    /**
     * echo test.
     * 
     * @param message message.
     * @return message.
     */
    Object $echo(Object message);

}