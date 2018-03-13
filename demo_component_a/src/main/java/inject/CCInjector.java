package inject;

import android.support.v4.util.Pair;
import android.util.Log;

import com.billy.cc.core.component.CC;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kassadin
 */
public class CCInjector {
    private final Map<String, Pair<Method, List<ParamInfo>>> methodInfos = new HashMap<>();
    private List<String> blackList = new ArrayList<String>() {
        {
            add("call");
            add("main");
        }
    };

    public void init(Object object) {
        Method[] declaredMethods = object.getClass().getDeclaredMethods();
        List<Method> methods = Arrays.asList(declaredMethods);
        System.out.println(methods.size());
        for (Method method : methods) {
            String name = method.getName();
            if (blackList.contains(name)) {
                continue;
            }
            System.out.println(name);
            Class<?>[] types = method.getParameterTypes();
            List<ParamInfo> paramInfos = new ArrayList<>();
            ParamInfo paramInfo;

            Annotation[][] annotations = method.getParameterAnnotations();
            for (Annotation[] annotation : annotations) {
                if (annotation.length > 0) {
                    if (annotation[0] instanceof CCParam) {
                        CCParam param = (CCParam) annotation[0];
                        paramInfo = new ParamInfo(param.value(), Object.class);
                        paramInfos.add(paramInfo);
                    }
                }
            }

            // TODO 重载过滤

            methodInfos.put(name, new Pair<>(method, paramInfos));
        }
    }

    public boolean onCall(Object object, CC cc) {
        if (methodInfos.size() == 0) {
            init(object);
        }

        String action = cc.getActionName();


        if (methodInfos.containsKey(action)) {
            Pair<Method, List<ParamInfo>> methodListPair = methodInfos.get(action);
            Method method = methodListPair.first;
            List<ParamInfo> value = methodListPair.second;
            ArrayList<Object> objects = new ArrayList<>();
            for (ParamInfo paramInfo : value) {
                objects.add(cc.getParamItem(paramInfo.name, null));
            }
            Log.d("xx", "onCall: " + objects.size());

            try {

                objects.add(0, cc);
                return (boolean) method.invoke(object, objects.toArray());

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        // faild
        return false;
    }

    protected static class ParamInfo {
        private final String name;

        private final Class<?> type;

        public ParamInfo(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }
    }
}
