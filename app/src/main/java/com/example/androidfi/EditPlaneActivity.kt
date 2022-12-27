package com.example.androidfi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class EditPlaneActivity : AppCompatActivity()
{
    private lateinit var editModel: EditText
    private lateinit var editColor: EditText
    private lateinit var editNumber: EditText
    private lateinit var editFactory: EditText
    private lateinit var editProductionDate: EditText
    private lateinit var editSeats: EditText
    private lateinit var editIsCargo: EditText
    private lateinit var editComment: EditText

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plane)

        editModel = findViewById(R.id.editTextExamName)
        editColor = findViewById(R.id.editTextTeacherName)
        editNumber = findViewById(R.id.editTextAuditory)
        editFactory = findViewById(R.id.editTextDate)
        editProductionDate = findViewById(R.id.editTextTime)
        editSeats = findViewById(R.id.editTextPeople)
        editIsCargo = findViewById(R.id.editTextAbstract)
        editComment = findViewById(R.id.editTextComment)

        val action = intent.getSerializableExtra("action") as Int

        findViewById<Button>(R.id.button_confirm).setOnClickListener { confirmChanges(action) }

        if (action == 2)
        {
            editModel.setText(intent.getSerializableExtra("model") as String)
            editColor.setText(intent.getSerializableExtra("color") as String)
            editNumber.setText(intent.getSerializableExtra("number") as String)
            editFactory.setText(intent.getSerializableExtra("factory") as String)
            editProductionDate.setText(intent.getSerializableExtra("productionDate") as String)
            editSeats.setText(intent.getSerializableExtra("seats") as String)
            if (intent.getSerializableExtra("isCargo") as String == "1")
            {
                editIsCargo.setText("да")
            }
            else
            {
                editIsCargo.setText("нет")
            }
            editComment.setText(intent.getSerializableExtra("comment") as String)
        }
    }

    private fun confirmChanges(action: Int)
    {
        if (editModel.text.toString() != "" && editColor.text.toString() != ""
            && editNumber.text.toString() != "" && editFactory.text.toString() != ""
            && editProductionDate.text.toString() != "" && editSeats.text.toString() != ""
            && editIsCargo.text.toString() != "")
        {
            if (editIsCargo.text.toString().trim().lowercase(Locale.ROOT) == "да"
                || editIsCargo.text.toString().trim().lowercase(Locale.ROOT) == "нет")
            {
                if (isDateValid(editProductionDate.text.toString().trim()))
                {
                    val intent = Intent(this@EditPlaneActivity,
                        MainActivity::class.java)
                    intent.putExtra("action",    action)
                    intent.putExtra("model",      editModel.text.toString().trim())
                    intent.putExtra("color",   editColor.text.toString().trim())
                    intent.putExtra("number",    editNumber.text.toString().trim().toInt())
                    intent.putExtra("factory",  editFactory.text.toString().trim())
                    intent.putExtra("productionDate", editProductionDate.text.toString().trim())
                    intent.putExtra("seats",   editSeats.text.toString().trim().toInt())
                    if (editIsCargo.text.toString().trim().lowercase(Locale.ROOT) == "да")
                    {
                        intent.putExtra("isCargo", 1)
                    }
                    else
                    {
                        intent.putExtra("isCargo", 0)
                    }
                    intent.putExtra("comment", editComment.text.toString().trim())
                    setResult(RESULT_OK, intent)
                    finish()
                }
                else
                {
                    val toast = Toast.makeText(
                        applicationContext,
                        "Проверьте дату!",
                        Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
            else
            {
                val toast = Toast.makeText(
                    applicationContext,
                    "Поле \"Грузовой:\" поддерживает только " +
                            "значения \"да\" или \"нет\"!",
                    Toast.LENGTH_SHORT)
                toast.show()
            }
        }
        else
        {
            val toast = Toast.makeText(
                applicationContext,
                "Заполните обязательные поля!",
                Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isDateValid(date: String?): Boolean
    {
        val myFormat = SimpleDateFormat("dd.MM.yyyy")
        myFormat.isLenient = false
        return try
        {
            if (date != null)
            {
                myFormat.parse(date)
            }
            true
        }
        catch (e: Exception)
        {
            false
        }
    }
}