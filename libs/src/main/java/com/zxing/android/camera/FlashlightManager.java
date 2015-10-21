package com.zxing.android.camera;/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is used to activate the weak light on some camera phones (not
 * flash) in order to illuminate surfaces for scanning. There is no official way
 * to do this, but, classes which allow access to this function still exist on
 * some devices. This therefore proceeds through a great deal of reflection.
 *
 * See <a href=
 * "http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-programatically/"
 * > http://almondmendoza.com/2009/01/05/changing-the-screen-brightness-
 * programatically/</a> and <a href=
 * "http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo/DroidLED.java"
 * > http://code.google.com/p/droidled/source/browse/trunk/src/com/droidled/demo
 * /DroidLED.java</a>. Thanks to Ryan Alford for pointing out the availability
 * of this class.
 */
final class FlashlightManager {

	private static final String TAG = FlashlightManager.class.getSimpleName();

	private static final Object iHardwareService;
	private static final Method setFlashEnabledMethod;
	static {
		iHardwareService = getHardwareService();
		setFlashEnabledMethod = getSetFlashEnabledMethod(iHardwareService);
		if (iHardwareService == null) {
			Log.v(TAG, "This device does supports control of a flashlight");
		} else {
			Log.v(TAG, "This device does not support control of a flashlight");
		}
	}

	private FlashlightManager() {
	}

	/**
	 * 获取硬件服务
	 * @return
	 */
	private static Object getHardwareService() {
		Object obj = null;
		//获取ServiceManager
		Class<?> serviceManagerClass = maybeForName("android.os.ServiceManager");
		//获取getService 方法
		Method getServiceMethod = maybeGetMethod(serviceManagerClass,
				"getService", String.class);
		Object hardwareService = invoke(getServiceMethod, null, "hardware");
		Class<?> iHardwareServiceStubClass = maybeForName("android.os.IHardwareService$Stub");
		Method asInterfaceMethod = maybeGetMethod(iHardwareServiceStubClass,
				"asInterface", IBinder.class);
		if (serviceManagerClass != null &&getServiceMethod != null&&hardwareService!=null&&iHardwareServiceStubClass!=null && asInterfaceMethod!=null) {
			obj =  invoke(asInterfaceMethod, null, hardwareService);
		}
		return obj;
	}

	/**
	 * 获取设置闪光灯激活 方法
	 * @param iHardwareService
	 * @return
	 */
	private static Method getSetFlashEnabledMethod(Object iHardwareService) {
		Class<?> proxyClass = null;
		if (iHardwareService != null) {
			proxyClass = iHardwareService.getClass();
		}
		return maybeGetMethod(proxyClass, "setFlashlightEnabled", boolean.class);
	}

	/**
	 * 通过获取 class
	 * @param name 类名
	 * @return
	 */
	private static Class<?> maybeForName(String name) {
		Class<?> clazz = null;
		try {
			clazz =  Class.forName(name);
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch (RuntimeException re) {
			Log.w(TAG, "Unexpected error while finding class " + name, re);
		}
		return clazz;
	}

	/**
	 * 通过反射 获取方法
	 * @param clazz 类名
	 * @param name 方法名
	 * @param argClasses 方法参数
	 * @return
	 */
	private static Method maybeGetMethod(Class<?> clazz, String name,
			Class<?>... argClasses) {
		Method  method = null;
		try {
			method =  clazz.getMethod(name, argClasses);
		} catch (NoSuchMethodException nsme) {
			nsme.printStackTrace();
		} catch (RuntimeException re) {
			Log.w(TAG, "Unexpected error while finding method " + name, re);
			re.printStackTrace();
		}
		return method;
	}

	/**
	 * 执行方法
	 * @param method 方法
	 * @param instance 实例
	 * @param args 参数
	 * @return
	 */
	private static Object invoke(Method method, Object instance, Object... args) {
		Object obj = null;
		try {
			obj = method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			Log.w(TAG, "Unexpected error while invoking " + method, e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			Log.w(TAG, "Unexpected error while invoking " + method,
					e.getCause());
			e.printStackTrace();
		} catch (RuntimeException re) {
			Log.w(TAG, "Unexpected error while invoking " + method, re);
			re.printStackTrace();
		}
		return obj;
	}

	/**
	 * 激活闪光灯
	 */
	static void enableFlashlight() {
		setFlashlight(true);
	}

	/**
	 * 关闭闪光灯
	 */
	static void disableFlashlight() {
		setFlashlight(false);
	}

	private static void setFlashlight(boolean active) {
		if (iHardwareService != null) {
			invoke(setFlashEnabledMethod, iHardwareService, active);
		}
	}

}
