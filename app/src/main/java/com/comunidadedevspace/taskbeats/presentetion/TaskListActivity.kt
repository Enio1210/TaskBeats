package com.comunidadedevspace.taskbeats.presentetion

import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.comunidadedevspace.taskbeats.R
import com.comunidadedevspace.taskbeats.data.AppDataBase
import com.comunidadedevspace.taskbeats.data.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private lateinit var cntContent: LinearLayout


    // isso sinigifica que o codigo so vai ser executado quando for utilizar o data base

    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "taskbeats-database"
        ).build()
    }

    private val dao by lazy {
        database.taskDao()
    }

    private val adapter: TaskListAdpater = TaskListAdpater(::onListItemCliked)


    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val taskAction = data?.getSerializableExtra(TASK_ACTION_RESULT) as TaskAction
            val task: Task = taskAction.task
// room data base completo
            when (taskAction.actionType) {
                ActionType.DELETE.name -> deleteByID(task.id)
                // aqui eu estou inserindo algo na lista
                ActionType.CREATE.name -> insertIntoDataBase(task)
                // atualizou a lista
                ActionType.UPDATE.name -> updateIntoDataBase(task)


            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_taks_listmain)
        // esse e o codigo da toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        // esse e pra nao duplicar a toolbar
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)



        listFromDataBase()
        cntContent = findViewById(R.id.ctn_content)


        // REciclerView
        val rvTask: RecyclerView = findViewById(R.id.rv_task_list)
        rvTask.adapter = adapter

        val fab = findViewById<FloatingActionButton>(R.id.fab_add)
        fab.setOnClickListener {
            openTaskListDetail(null)
        }
    }

    private fun insertIntoDataBase(task: Task) {
        CoroutineScope(IO).launch {
            dao.insert(task)
            listFromDataBase()
        }

    }

    private fun updateIntoDataBase(task: Task) {
        CoroutineScope(IO).launch {
            dao.insert(task)
            // aqui eu atualizo minh alista
            listFromDataBase()
        }

    }

    private fun deleteAll() {
        CoroutineScope(IO).launch {
            dao.deleteAll()
            // aqui eu atualizo minh alista
            listFromDataBase()
        }
    }

    private fun deleteByID(id: Int) {
        CoroutineScope(IO).launch {
            dao.deleteById(id)
            listFromDataBase()
        }

    }

    private fun listFromDataBase() {
        CoroutineScope(IO).launch {
            val myDataBaseList: List<Task> = dao.getAll()
            adapter.submitList(myDataBaseList)

            /*CoroutineScope(Main).launch {
// se tiver vazio ele mostra
                if (myDataBaseList.isEmpty()) {
                    cntContent.visibility = View.VISIBLE
                    // aque ele vai esconder se tiver algo na lista
                } else {

                    cntContent.visibility = View.GONE

                }
            }*/

        }
    }

    private fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null)
            .show()
    }

    private fun onListItemCliked(task: Task) {
        openTaskListDetail(task)
    }

    private fun openTaskListDetail(task: Task?) {
        val intent = TaskDetailActivity.start(this, task)
        startForResult.launch(intent)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.tasklist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_task -> {
                deleteAll()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}

// crud
//create
//read
//update
//delete
enum class ActionType {
    DELETE,
    UPDATE,
    CREATE,

}

data class TaskAction(

    val task: Task,
    val actionType: String
) : Serializable

const val TASK_ACTION_RESULT = "TASK_ACTION_RESULT"
