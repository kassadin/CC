package com.billy.cc.demo.component.a;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;

import inject.CCInjector;
import inject.CCParam;

/**
 * @author billy.qi
 */
public class ComponentA implements IComponent {

    private CCInjector ccInjector = new CCInjector();

    @Override
    public String getName() {
        //组件的名称，调用此组件的方式：
        // CC.obtainBuilder("ComponentA")...build().callAsync()
        return "demo.ComponentA";
    }

    @Override
    public boolean onCall(CC cc) {
        return ccInjector.onCall(this, cc);
    }

    public boolean lifecycleFragmentAddText(CC cc,
                                            @CCParam("fragment") LifecycleFragment fragment,
                                            @CCParam("text") String text) {
        if (fragment != null) {
            fragment.addText(text);
            CC.sendCCResult(cc.getCallId(), CCResult.success());
        } else {
            CC.sendCCResult(cc.getCallId(), CCResult.error("no fragment params"));
        }
        return false;
    }

    public boolean getLifecycleFragment(CC cc) {
        CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new LifecycleFragment())
                                                .addData("int", 1)
        );
        return false;
    }

    public boolean getInfo(CC cc) {
        String userName = "billy";
        CC.sendCCResult(cc.getCallId(), CCResult.success("userName", userName));
        return false;
    }

    public boolean showActivityA(CC cc) {
        Context context = cc.getContext();
        Intent intent = new Intent(context, ActivityA.class);
        if (!(context instanceof Activity)) {
            //调用方没有设置context或app间组件跳转，context为application
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        CC.sendCCResult(cc.getCallId(), CCResult.success());
        return false;
    }
}
