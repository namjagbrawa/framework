package com.bingo.framework.registry;

import com.bingo.framework.common.Node;
import com.bingo.framework.common.URL;
import com.bingo.framework.registry.support.AbstractRegistry;

/**
 * Registry. (SPI, Prototype, ThreadSafe)
 * 
 * @see RegistryFactory#getRegistry(URL)
 * @see AbstractRegistry
 * @author william.liangf
 */
public interface Registry extends Node, RegistryService {
}