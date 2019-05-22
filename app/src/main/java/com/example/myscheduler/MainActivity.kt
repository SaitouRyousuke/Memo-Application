package com.example.myscheduler

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import kotlinx.android.synthetic.main.content_main.*
import org.jetbrains.anko.*
import android.widget.EditText
import android.widget.Toast
import android.text.InputType
import android.text.InputFilter


class MainActivity : AppCompatActivity() {

    private lateinit var realm: Realm //Realmクラスのプロパティを用意

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//        realm = Realm.getDefaultInstance() //Realmクラスのインスタンスを取得
        val realmConfig = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        realm = Realm.getInstance(realmConfig)

        val pass = intent?.getIntExtra("pass", -1)
        val schedules = realm.where<Schedule>().findAll()
        listView.adapter = ScheduleAdapter(schedules)

        fab.setOnClickListener { view ->
            startActivity<ScheduleEditActivity>("pass" to pass)
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val schedule = parent.getItemAtPosition(position) as Schedule
            if (schedule.check == 1) {
                val myedit = EditText(this)
                val dialog = AlertDialog.Builder(this)
                val filters = arrayOfNulls<InputFilter>(1)
                filters[0] = InputFilter.LengthFilter(4)
                dialog.setTitle("パスワードを入力してください")
                myedit.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
                myedit.setFilters(filters)
                dialog.setView(myedit)
                dialog.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    // OKボタン押したときの処理
                    val userText = myedit.getText().toString()
                    if (userText == schedule.pass) {
                        startActivity<ScheduleEditActivity> ("schedule_id" to schedule.id)
                    } else {
                        Toast.makeText(this, "パスワードが違います", Toast.LENGTH_SHORT).show()
                    }
                })
                dialog.setNegativeButton("キャンセル", null)
                dialog.show()
            } else {
                startActivity<ScheduleEditActivity> ("schedule_id" to schedule.id)
            }
        }
    }

    override fun onDestroy() { //Activityの終了処理
        super.onDestroy()
        realm.close() //closeメソッドでRealmのインスタンスを破棄し、リソースを解放
    }
}
