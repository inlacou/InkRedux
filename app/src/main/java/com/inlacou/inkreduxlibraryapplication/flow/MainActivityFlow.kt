package com.inlacou.inkreduxlibraryapplication.flow

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.inlacou.inkreduxlibraryapplication.GlobalStore
import com.inlacou.inkreduxlibraryapplication.R

class MainActivityFlow : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main_flow)
		setSupportActionBar(findViewById(R.id.toolbar))
		
		findViewById<FloatingActionButton>(R.id.fab_plus).setOnClickListener { view ->
			GlobalStore.applyAction(GlobalStore.Actions.Add(1))
		}
		findViewById<FloatingActionButton>(R.id.fab_minus).setOnClickListener { view ->
			GlobalStore.applyAction(GlobalStore.Actions.Decrement)
		}
	}
	
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}
	
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return when (item.itemId) {
			R.id.action_settings -> true
			else -> super.onOptionsItemSelected(item)
		}
	}
}