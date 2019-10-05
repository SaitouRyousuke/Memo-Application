package com.example.myscheduler

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class MySchedulerApplication : Application() { //Applicationクラスを継承

    override fun onCreate() { //ApplicationクラスのonCreateメソッドをオーバーライド
        super.onCreate()
        Realm.init(this) //Realm初期化
    }
}