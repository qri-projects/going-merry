package com.ggemo.va.goingmerry.business.injectopproxyfields;

import com.ggemo.va.business.pipeline.ListPplBusiness;
import com.ggemo.va.goingmerry.business.injectopproxyfields.vo.InjectOpProxyFieldsContext;
import com.ggemo.va.goingmerry.business.injectopproxyfields.vo.InjectOpProxyFieldsReq;
import com.ggemo.va.goingmerry.business.injectopproxyfields.vo.InjectOpProxyFieldsRes;
import com.ggemo.va.goingmerry.handler.GetClassFieldsHandler;
import com.ggemo.va.step.ClassicOpStep;

public class InjectOpProxyFieldsBusiness
        extends ListPplBusiness<InjectOpProxyFieldsContext, InjectOpProxyFieldsReq, InjectOpProxyFieldsRes> {

    public InjectOpProxyFieldsBusiness() {
        initPpl();
    }

    protected void initPpl() {
        /**
         * 找出所有OpProxy fields
         * 找对应的对象, 找不到则创建
         */

        // 找出bean的类的所有@OpProxy注解的Field
        addStep(new ClassicOpStep<>(context -> context.getBean().getClass(),
                (c, r) -> {
                    c.setBeanFields(r);
                    return c;
                },
                new GetClassFieldsHandler()
        ));



    }

    @Override
    protected InjectOpProxyFieldsContext generateContext(InjectOpProxyFieldsReq req) {
        return null;
    }

    @Override
    protected InjectOpProxyFieldsRes castToRes(InjectOpProxyFieldsContext injectOpProxyFieldsContext) {
        return null;
    }
}
