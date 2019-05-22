package com.example.myscheduler

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MySchedulerApplication : Application() { //Applicationクラスを継承
//    private lateinit var realm: Realm //Realmクラスのプロパティを用意

    override fun onCreate() { //ApplicationクラスのonCreateメソッドをオーバーライド
        super.onCreate()
        Realm.init(this) //Realm初期化
//        realm = Realm.getDefaultInstance()
//        val realmConfig = RealmConfiguration.Builder()
//            .deleteRealmIfMigrationNeeded()
//            .build()
//        realm = Realm.getInstance(realmConfig)
    }
}