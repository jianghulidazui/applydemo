package com.tru.test;

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import com.tru.test.ClassAppender

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class MyTransform extends Transform {

    Project project;

    public MyTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "MyTrans";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    // 指定Transform的作用范围
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

//    @Override
//    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
////        inputs.each { TransformInput input ->
////            input.directoryInputs.each { DirectoryInput directoryInput ->
////                def dest = outputProvider.getContentLocation(directoryInput.name
////                        ,directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
////                FileUtils.copyDirectory(directoryInput.file, dest)
////            }
////
////            input.jarInputs.each { JarInput jarInput ->
////                def jarName = jarInput.name
////                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
////                if (jarName.endsWith(".jar")) {
////                    jarName = jarName.substring(0, jarName.length() - 4)
////                }
////                def dest = outputProvider.getContentLocation(jarName + md5Name
////                        , jarInput.contentTypes, jarInput.scopes, Format.JAR)
////                FileUtils.copyFile(jarInput.file, dest)
////            }
////        }
//
//    }

//    @Override
//    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
//        super.transform(transformInvocation)
//    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {

        //step1:将所有类的路径加入到ClassPool中
        ClassPool classPool = new ClassPool()
        project.android.bootClasspath.each {
            classPool.appendClassPath((String) it.absolutePath)
        }

        //TODO 这里有优化的空间,实际上只要将我们需要的类加进去即可
        ClassAppender.appendAllClasses(inputs, classPool)

        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each { TransformInput input ->
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等
                MyInject.injectDir(directoryInput.file.absolutePath,"com\\example\\applydemo", project, classPool)
                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)

                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->

                //jar文件一般是第三方依赖库jar文件

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }
}
