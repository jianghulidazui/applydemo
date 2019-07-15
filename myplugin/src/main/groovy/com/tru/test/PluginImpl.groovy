package com.tru.test

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginImpl implements Plugin<Project> {
    @Override
    void apply(Project project) {
        System.out.println("========================")
        System.out.println("hello gradle plugin!")
        System.out.println("========================")

//        project.getTasks().create("hello", TestTask.class, new Action<TestTask>() {
//            @Override
//            void execute(TestTask t) {
//                t.str = "nick and"
//                t.say()
//                t.str = "hello"
//            }
//        })
//
//        project.extensions.create('tc',TestExtension);
//        project.extensions.create('address', Address);
//
//
//        project.task('readExtension') {
//            def address=project['address']
//
//            println project['tc'].myName
//            println address.province+" "+address.city
//
//        }

        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new MyTransform(project))

    }
}
