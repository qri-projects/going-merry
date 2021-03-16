package com.ggemo.va.goingmerry.aotuwired.proxybuilder;

import com.ggemo.va.goingmerry.service.base.GmService;
/**
 * <p>构建{@link GmService}的代理bean的构建器
 */
public interface AutowiredGmServiceProxyBuilder {
    <Con, S extends GmService<Con>> S build(Class<S> serviceClazz);
}
