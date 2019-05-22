package com.example.myscheduler

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.format.DateFormat
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.IllegalArgumentException as IllegalArgumentException1
import android.widget.Switch
import kotlinx.android.synthetic.main.activity_schedule_edit.view.*


class ScheduleEditActivity : AppCompatActivity() {

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
//        realm = Realm.getDefaultInstance() //Realmのインスタンスを取得
        val realmConfig = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.getInstance(realmConfig)
        val switch = findViewById<Switch>(R.id.switch1) as Switch
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()

        val scheduleId = intent?.getLongExtra("schedule_id", -1L)
        if (scheduleId != -1L) {
            val schedule = realm.where<Schedule>().equalTo("id", scheduleId).findFirst()
            dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.date))
            titleEdit.setText(schedule?.title)
            detailEdit.setText(schedule?.detail)
            passEdit.setText(String.format("%s", schedule?.pass))
            editor.putInt("check", schedule?.check!!)
                .apply()
            if (pref.getInt("check", 0) == 1) {
                switch.isChecked = true
            }
            delete.visibility = View.VISIBLE
        } else {
            delete.visibility = View.INVISIBLE
        }

        save.setOnClickListener { //保存ボタンがタップされた時の処理
            when (scheduleId){
                -1L -> {
                    realm.executeTransaction {
                        val maxId = realm.where<Schedule>().max("id")
                        val nextId = (maxId?.toLong() ?: 0L) + 1
                        val schedule = realm.createObject<Schedule>(nextId)
                        dateEdit.text.toString().toDate("yyyy/MM/dd")?.let {
                            schedule.date = it
                        }
                        schedule.title = titleEdit.text.toString()
                        schedule.detail = detailEdit.text.toString()
                        schedule.pass = passEdit.text.toString()
                        if (switch.isChecked == true) {
                            schedule.check = 1
                            editor.putInt("check", 0)
                                .apply()
                        } else {
                            schedule.check = 0
                            editor.putInt("check", 0)
                                .apply()
                        }
                        alert("追加しました") {
                            yesButton {finish()}
                        }.show()
                    }
                }
                else -> {
                    realm.executeTransaction {
                        val schedule = realm.where<Schedule>().equalTo("id", scheduleId).findFirst()
                        dateEdit.text.toString().toDate("yyyy/MM/dd")?.let {
                            schedule?.date = it
                        }
                        schedule?.title = titleEdit.text.toString()
                        schedule?.detail = detailEdit.text.toString()
                        schedule?.pass = passEdit.text.toString()
                        if (switch.isChecked == true) {
                            schedule?.check = 1
                            editor.putInt("check", 0)
                                .apply()
                        } else {
                            schedule?.check = 0
                            editor.putInt("check", 0)
                                .apply()
                        }
                    }
                    alert ("修正しました") {
                        yesButton { finish() }
                    }.show()
                }
            }
        }
        delete.setOnClickListener {
            realm.executeTransaction {
                realm.where<Schedule>().equalTo("id", scheduleId)?.findFirst()?.deleteFromRealm()
            }
            alert("削除しました") {
                yesButton { finish() }
            }.show()
        }
    }

    override fun onDestroy() { // データベースを閉じる処理
        super.onDestroy()
        realm.close()
    }

    fun String.toDate(pattern: String = "yyyy/MM/dd HH:mm"): Date? {
        val sdFormat = try {
            SimpleDateFormat(pattern)
        } catch (e: IllegalArgumentException) {
            null
        }
        val date = sdFormat?.let {
            try {
                it.parse(this)
            } catch (e: ParseException) {
                null
            }
        }
        return date
    }
}

