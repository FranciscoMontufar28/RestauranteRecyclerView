package com.practice.francisco.restaurante

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var lista:RecyclerView? = null
    var adaptador:AdaptadorCustom? =null
    var layoutManager:RecyclerView.LayoutManager? = null

    var isActionMode = false
    var actionMode:ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val platillos = ArrayList<Platillo>()
        val swipeToRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        platillos.add(Platillo("Alitas miel mostaza", 25.000, 4.0F, R.drawable.alitas))
        platillos.add(Platillo("Hamburguesa", 18.000, 3.5F, R.drawable.hamburguesas))
        platillos.add(Platillo("Pizza", 12.000, 4.5F, R.drawable.pizza))
        platillos.add(Platillo("Perro Caliente", 10.000, 3.0F, R.drawable.perro))
        platillos.add(Platillo("Bandeja Paisa", 15.000, 3.8F, R.drawable.paisa))


        lista = findViewById(R.id.lista)
        lista?.setHasFixedSize(true)

        layoutManager = LinearLayoutManager(this)
        lista?.layoutManager = layoutManager

        val callback = object: ActionMode.Callback{
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when(item?.itemId){
                    R.id.iEliminar ->{
                        //Toast.makeText(applicationContext, "Eliminar", Toast.LENGTH_SHORT).show()
                        adaptador?.eliminarSeleccionados()
                    }
                    else ->{
                        return true
                    }
                }
                adaptador?.terminarActionMode()
                mode?.finish()
                isActionMode = false


                return true
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                //Inicializacion de Action Mode
                adaptador?.iniciarActionMode()
                actionMode = mode
                //inflate menu
                menuInflater.inflate(R.menu.menu_contextual, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.title = "0 seleccionados"
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                adaptador?.destruirActionMode()
                isActionMode = false
            }

        }

        adaptador = AdaptadorCustom(this, platillos, object:ClickListener{
            override fun onClick(vista: View, index: Int) {
                Toast.makeText(applicationContext, platillos.get(index).nombre, Toast.LENGTH_SHORT).show()
            }

        },object: LongClickListener{
            override fun longClick(vista: View, index: Int) {
                if(!isActionMode){
                    startSupportActionMode(callback)
                    isActionMode = true
                    adaptador?.seleccionarItem(index)
                }else{
                    //hacer selecciones o deselecciones
                    adaptador?.seleccionarItem(index)
                }
                actionMode?.title = adaptador?.obtenerNumeroElementosSeleccionados().toString()+" seleccionados"
            }

        })
        lista?.adapter = adaptador

        swipeToRefresh.setOnRefreshListener {
            //Log.d("Refresh", "La informacion se esta actualizando ")
            TimeUnit.SECONDS.sleep(1)
            swipeToRefresh.isRefreshing = false
            platillos.add(Platillo("Nugets", 15.000, 3.8F, R.drawable.paisa))
            adaptador?.notifyDataSetChanged()
        }


    }
}
