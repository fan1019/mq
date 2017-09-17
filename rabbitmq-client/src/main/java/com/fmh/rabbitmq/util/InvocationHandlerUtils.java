package com.fmh.rabbitmq.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class InvocationHandlerUtils {

    private InvocationHandlerUtils(){

    }

    public static Object delegateMethodInvocation(final Method method, final Object[] args,final Object target) throws Throwable{
        try {
            return method.invoke(target,args);
        }catch (InvocationTargetException e){
            throw e.getTargetException();
        }
    }
}

