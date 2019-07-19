package com.tru.test;

import javassist.ClassPool
import javassist.CtClass
import javassist.CtConstructor
import org.gradle.api.Project;

public class MyInject {
    private static ClassPool pool = ClassPool.getDefault()
    private static String injectStr = "System.out.println(\"I Love HuaChao\" ); ";

    public static void injectDir(String path, String packageName, Project project,ClassPool mPool) {
        pool.appendClassPath(path)
        pool.appendClassPath(project.android.bootClasspath[0].toString())
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath
                //确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {
                    System.out.println(filePath);
                    // 判断当前目录是否是在我们的应用包里面
                    int index = filePath.indexOf(packageName);
                    boolean isMyPackage = index != -1;
                    if (isMyPackage) {
                        int end = filePath.length() - 6 // .class = 6
                        String className = filePath.substring(index, end).replace('\\', '.').replace('/', '.')
                        //开始修改class文件
//                        CtClass c1 = pool.get("com.example.applydemo.Hello");
//                        if (c1.isFrozen()) {
//                            c1.defrost()
//                        }
//                        pool.importPackage("com.example.applydemo.Parent");
//                        c1.setSuperclass(pool.get("com.example.applydemo.Parent"))
//                        c1.writeFile(path)
//                        c1.detach()


                        CtClass c = mPool.getCtClass(className)

//                        if (c.isFrozen()) {
//                            c.defrost()
//                        }
//                        CtClass tc = pool.getCtClass("android.widget.TextView")
//                        if (tc.isFrozen()) {
//                            tc.defrost()
//                        }
//                        tc.setSuperclass(pool.get("com.example.applydemo.NanoTextView"))
                        if (c.getSuperclass().name.equals("com.example.applydemo.Parent")) {
                            c.setSuperclass(mPool.get("com.example.applydemo.Uncle"))
                        }

                        CtConstructor[] cts = c.getDeclaredConstructors()
                        mPool.importPackage("android.util.Log");
                        if (cts == null || cts.length == 0) {
                            //手动创建一个构造函数
                            CtConstructor constructor = new CtConstructor(new CtClass[0], c)
                            constructor.insertBeforeBody(injectStr)
                            c.addConstructor(constructor)
                        } else {
                            cts[0].insertBeforeBody(injectStr)
                        }
                        c.writeFile(path)
                        c.detach()
                    }
                }
            }
        }
    }
}
