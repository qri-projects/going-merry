package com.ggemo.va.goingmerry.aotuwired.proxybuilder;

import com.ggemo.va.goingmerry.service.base.GmService;

public interface AutowiredGmServiceProxyBuilder {
    <Con, S extends GmService<Con>> S build(Class<S> serviceClazz);
}
