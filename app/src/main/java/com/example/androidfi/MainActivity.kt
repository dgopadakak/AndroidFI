package com.example.androidfi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfi.databinding.ActivityMainBinding
import com.example.androidfi.forRecyclerView.CustomRecyclerAdapterForExams
import com.example.androidfi.forRecyclerView.RecyclerItemClickListener
import com.example.androidfi.airlines.Airline
import com.example.androidfi.airlines.AirlineOperator
import com.example.androidfi.airlines.Plane
import com.example.androidfi.airlines.dbWithRoom.App
import com.example.androidfi.airlines.dbWithRoom.AppDatabase
import com.example.androidfi.airlines.dbWithRoom.AirlineOperatorDao
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    PlaneDetailsDialogFragment.OnInputListenerSortId
{
    private val gsonBuilder = GsonBuilder()
    private val gson: Gson = gsonBuilder.create()
    private val serverIP = "192.168.1.69"
    private val serverPort = 8989
    private lateinit var connection: Connection
    private var connectionStage: Int = 0
    private var startTime: Long = 0

    private lateinit var db: AppDatabase
    private lateinit var aoDao: AirlineOperatorDao

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nv: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerViewPlanes: RecyclerView
    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts
        .StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            val data: Intent? = result.data
            processOnActivityResult(data)
        }
    }

    private var ao: AirlineOperator = AirlineOperator()
    private var currentAirlineID: Int = -1
    private var currentPlaneID: Int = -1
    private var waitingForUpdate: Boolean = false
    private lateinit var airlineTitle: String

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        nv = binding.navView
        nv.setNavigationItemSelectedListener(this)
        toolbar = findViewById(R.id.toolbar)
        toolbar.apply { setNavigationIcon(R.drawable.ic_my_menu) }
        toolbar.setNavigationOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        progressBar = findViewById(R.id.progressBar)
        recyclerViewPlanes = findViewById(R.id.recyclerViewExams)
        recyclerViewPlanes.visibility = View.INVISIBLE
        recyclerViewPlanes.layoutManager = LinearLayoutManager(this)

        recyclerViewPlanes.addOnItemTouchListener(
            RecyclerItemClickListener(
                recyclerViewPlanes,
                object : RecyclerItemClickListener.OnItemClickListener
                {
                    override fun onItemClick(view: View, position: Int)
                    {
                        currentPlaneID = position
                        val toast = Toast.makeText(
                            applicationContext,
                            "??????????????????????: ${ao.getPlane(currentAirlineID, currentPlaneID)
                                .seats}",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    }
                    override fun onItemLongClick(view: View, position: Int)
                    {
                        currentPlaneID = position
                        val examDetails = PlaneDetailsDialogFragment()
                        val tempExam = ao.getPlane(currentAirlineID, currentPlaneID)
                        val bundle = Bundle()
                        bundle.putString("model", tempExam.model)
                        bundle.putString("color", tempExam.color)
                        bundle.putString("number", tempExam.num.toString())
                        bundle.putString("factory", tempExam.factory)
                        bundle.putString("productionDate", tempExam.productionDate)
                        bundle.putString("seats", tempExam.seats.toString())
                        bundle.putString("isCargo", tempExam.isCargo.toString())
                        bundle.putString("comment", tempExam.comment)
                        bundle.putString("connection", connectionStage.toString())
                        examDetails.arguments = bundle
                        examDetails.show(fragmentManager, "MyCustomDialog")
                    }
                }
            )
        )

        db = App.instance?.database!!
        aoDao = db.groupOperatorDao()
        startTime = System.currentTimeMillis()
        connection = Connection(serverIP, serverPort, "REFRESH", this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        if (currentAirlineID != -1 && connectionStage == 1)
        {
            menu.getItem(0).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        val id = item.itemId
        if (id == R.id.action_add)
        {
            val intent = Intent()
            intent.setClass(this, EditPlaneActivity::class.java)
            intent.putExtra("action", 1)
            resultLauncher.launch(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    internal inner class Connection(
        private val SERVER_IP: String,
        private val SERVER_PORT: Int,
        private val refreshCommand: String,
        private val activity: Activity
    ) {
        private var outputServer: PrintWriter? = null
        private var inputServer: BufferedReader? = null
        var thread1: Thread? = null
        private var threadT: Thread? = null

        internal inner class Thread1Server : Runnable {
            override fun run()
            {
                val socket: Socket
                try {
                    socket = Socket(SERVER_IP, SERVER_PORT)
                    outputServer = PrintWriter(socket.getOutputStream())
                    inputServer = BufferedReader(InputStreamReader(socket.getInputStream()))
                    Thread(Thread2Server()).start()
                    sendDataToServer(refreshCommand)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        internal inner class Thread2Server : Runnable {
            override fun run() {
                while (true) {
                    try {
                        val message = inputServer!!.readLine()
                        if (message != null)
                        {
                            activity.runOnUiThread { processingInputStream(message) }
                        } else {
                            thread1 = Thread(Thread1Server())
                            thread1!!.start()
                            return
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        internal inner class Thread3Server(private val message: String) : Runnable
        {
            override fun run()
            {
                outputServer!!.write(message)
                outputServer!!.flush()
            }
        }

        internal inner class ThreadT : Runnable
        {
            override fun run() {
                while (true)
                {
                    if (System.currentTimeMillis() - startTime > 5000L && connectionStage == 0)
                    {
                        activity.runOnUiThread { val toast = Toast.makeText(
                            applicationContext,
                            "???????????? ??????????????",
                            Toast.LENGTH_SHORT)
                            toast.show() }
                        connectionStage = -1
                        activity.runOnUiThread { progressBar.visibility = View.INVISIBLE }
                        ao = aoDao.getById(1)
                        for (i in 0 until ao.getAirlines().size)
                        {
                            activity.runOnUiThread { nv.menu.add(0, i, 0,
                                ao.getAirlines()[i].name as CharSequence) }
                        }
                    }
                }
            }
        }

        fun sendDataToServer(text: String)
        {
            Thread(Thread3Server(text + "\n")).start()
        }

        private fun processingInputStream(text: String)
        {
            aoDao.delete(AirlineOperator())
            val tempGo: AirlineOperator = gson.fromJson(text, AirlineOperator::class.java)
            aoDao.insert(tempGo)

            if (connectionStage != 1)
            {
                val toast = Toast.makeText(
                    applicationContext,
                    "???????????? ?? ????????????????.",
                    Toast.LENGTH_SHORT)
                toast.show()
            }

            progressBar.visibility = View.INVISIBLE
            for (i in 0 until ao.getAirlines().size)
            {
                nv.menu.removeItem(i)
            }
            val tempArrayListAirlines: ArrayList<Airline> = tempGo.getAirlines()
            ao.setAirlines(tempArrayListAirlines)
            for (i in 0 until tempArrayListAirlines.size)
            {
                nv.menu.add(
                    0, i, 0,
                    tempArrayListAirlines[i].name as CharSequence
                )
            }
            if (waitingForUpdate || connectionStage == -1)
            {
                waitingForUpdate = false
                if (currentAirlineID != -1)
                {
                    recyclerViewPlanes.adapter = CustomRecyclerAdapterForExams(
                        ao.getPlaneModels(currentAirlineID),
                        ao.getPlanesNumbers(currentAirlineID)
                    )
                }
            }
            connectionStage = 1
        }

        init {
            thread1 = Thread(Thread1Server())
            thread1!!.start()
            threadT = Thread(ThreadT())
            threadT!!.start()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawerLayout.closeDrawer(GravityCompat.START)
        airlineTitle = "${item.title}"
        toolbar.title = airlineTitle
        invalidateOptionsMenu()
        currentAirlineID = item.itemId
        recyclerViewPlanes.adapter = CustomRecyclerAdapterForExams(
            ao.getPlaneModels(currentAirlineID),
            ao.getPlanesNumbers(currentAirlineID))
        recyclerViewPlanes.visibility = View.VISIBLE
        return true
    }

    fun delPlane()
    {
        connection.sendDataToServer("d$currentAirlineID,$currentPlaneID")
        waitingForUpdate = true
    }

    override fun sendInputSortId(sortId: Int)
    {
        if (sortId > -1 && sortId < 8)      // ????????????????????
        {
            ao.sortPlanes(currentAirlineID, sortId)
            if (connectionStage == 1)
            {
                connection.sendDataToServer("u" + gson.toJson(ao))
            }
            toolbar.title = when (sortId)
            {
                0 -> "$airlineTitle ????????. ????????????"
                1 -> "$airlineTitle ????????. ????????"
                2 -> "$airlineTitle ????????. ???"
                3 -> "$airlineTitle ????????. ??????????"
                4 -> "$airlineTitle ????????. ???????? ????."
                5 -> "$airlineTitle ????????. ??????????????????????"
                6 -> "$airlineTitle ????????. ????????????????"
                7 -> "$airlineTitle ????????. ????????????????"
                else -> airlineTitle
            }
        }
        if (sortId == 8)        // ????????????????
        {
            val manager: FragmentManager = supportFragmentManager
            val myDialogFragmentDelPlane = MyDialogFragmentDelPlane()
            val bundle = Bundle()
            bundle.putString("name", ao.getPlane(currentAirlineID, currentPlaneID).model)
            myDialogFragmentDelPlane.arguments = bundle
            myDialogFragmentDelPlane.show(manager, "myDialog")
        }
        if (sortId == 9)        // ??????????????????
        {
            val tempTask = ao.getPlane(currentAirlineID, currentPlaneID)
            val intent = Intent()
            intent.setClass(this, EditPlaneActivity::class.java)
            intent.putExtra("action", 2)
            intent.putExtra("model", tempTask.model)
            intent.putExtra("color", tempTask.color)
            intent.putExtra("number", tempTask.num.toString())
            intent.putExtra("factory", tempTask.factory)
            intent.putExtra("productionDate", tempTask.productionDate)
            intent.putExtra("seats", tempTask.seats.toString())
            intent.putExtra("isCargo", tempTask.isCargo.toString())
            intent.putExtra("comment", tempTask.comment)
            resultLauncher.launch(intent)
        }
        recyclerViewPlanes.adapter = CustomRecyclerAdapterForExams(
            ao.getPlaneModels(currentAirlineID),
            ao.getPlanesNumbers(currentAirlineID))
    }

    private fun processOnActivityResult(data: Intent?)
    {
        val action = data!!.getIntExtra("action", -1)
        val model = data.getStringExtra("model")
        val color = data.getStringExtra("color")
        val number = data.getIntExtra("number", -1)
        val factory = data.getStringExtra("factory")
        val productionDate = data.getStringExtra("productionDate")
        val seats = data.getIntExtra("seats", -1)
        val isCargo = data.getIntExtra("isCargo", 0)
        val comment = data.getStringExtra("comment")
        val tempPlane = Plane(model!!, color!!, number, factory!!, productionDate!!, seats
            , isCargo, comment!!)
        val tempPlaneJSON: String = gson.toJson(tempPlane)

        if (action == 1)
        {
            val tempStringToSend = "a${ao.getAirlines()[currentAirlineID].name}##$tempPlaneJSON"
            connection.sendDataToServer(tempStringToSend)
            waitingForUpdate = true
        }
        if (action == 2)
        {
            val tempStringToSend = "e$currentAirlineID,$currentPlaneID##$tempPlaneJSON"
            connection.sendDataToServer(tempStringToSend)
            waitingForUpdate = true
        }
        if (action == -1)
        {
            val toast = Toast.makeText(
                applicationContext,
                "???????????? ????????????????????/??????????????????!",
                Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}