package com.example.androidfi

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class PlaneDetailsDialogFragment: android.app.DialogFragment()
{
    private val exceptionTag = "PharmacyDetailsDialogFragment"

    interface OnInputListenerSortId
    {
        fun sendInputSortId(sortId: Int)
    }

    lateinit var onInputListenerSortId: OnInputListenerSortId

    private lateinit var textViewModelTitle: TextView
    private lateinit var textViewModel: TextView
    private lateinit var textViewColorTitle: TextView
    private lateinit var textViewColor: TextView
    private lateinit var textViewNumTitle: TextView
    private lateinit var textViewNum: TextView
    private lateinit var textViewFactoryTitle: TextView
    private lateinit var textViewFactory: TextView
    private lateinit var textViewProductionDateTitle: TextView
    private lateinit var textViewProductionDate: TextView
    private lateinit var textViewSeatsTitle: TextView
    private lateinit var textViewSeats: TextView
    private lateinit var textViewIsCargoTitle: TextView
    private lateinit var textViewIsCargo: TextView
    private lateinit var textViewCommentTitle: TextView
    private lateinit var textViewComment: TextView
    private lateinit var buttonDel: Button
    private lateinit var buttonEdit: Button
    private lateinit var buttonOk: Button
    private lateinit var textViewCurrSort: TextView

    private var currentIdForSort: Int = -1

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View
    {
        val view: View = inflater!!.inflate(R.layout.plane_details, container, false)
        textViewModelTitle = view.findViewById(R.id.textViewExamNameTitle)
        textViewModel = view.findViewById(R.id.textViewExamName)
        textViewColorTitle = view.findViewById(R.id.textViewTeacherNameTitle)
        textViewColor = view.findViewById(R.id.textViewTeacherName)
        textViewNumTitle = view.findViewById(R.id.textViewAuditoryTitle)
        textViewNum = view.findViewById(R.id.textViewAuditory)
        textViewFactoryTitle = view.findViewById(R.id.textViewDateTitle)
        textViewFactory = view.findViewById(R.id.textViewDate)
        textViewProductionDateTitle = view.findViewById(R.id.textViewTimeTitle)
        textViewProductionDate = view.findViewById(R.id.textViewTime)
        textViewSeatsTitle = view.findViewById(R.id.textViewPeopleTitle)
        textViewSeats = view.findViewById(R.id.textViewPeople)
        textViewIsCargoTitle = view.findViewById(R.id.textViewAbstractTitle)
        textViewIsCargo = view.findViewById(R.id.textViewAbstract)
        textViewCommentTitle = view.findViewById(R.id.textViewCommentTitle)
        textViewComment = view.findViewById(R.id.textViewComment)
        buttonDel = view.findViewById(R.id.button_details_delete)
        buttonEdit = view.findViewById(R.id.button_details_edit)
        buttonOk = view.findViewById(R.id.button_details_ok)
        textViewCurrSort = view.findViewById(R.id.textViewCurrentSort)

        textViewModelTitle.setOnLongClickListener { setSortId(0) }
        textViewModel.setOnLongClickListener { setSortId(0) }
        textViewColorTitle.setOnLongClickListener { setSortId(1) }
        textViewColor.setOnLongClickListener { setSortId(1) }
        textViewNumTitle.setOnLongClickListener { setSortId(2) }
        textViewNum.setOnLongClickListener { setSortId(2) }
        textViewFactoryTitle.setOnLongClickListener { setSortId(3) }
        textViewFactory.setOnLongClickListener { setSortId(3) }
        textViewProductionDateTitle.setOnLongClickListener { setSortId(4) }
        textViewProductionDate.setOnLongClickListener { setSortId(4) }
        textViewSeatsTitle.setOnLongClickListener { setSortId(5) }
        textViewSeats.setOnLongClickListener { setSortId(5) }
        textViewIsCargoTitle.setOnLongClickListener { setSortId(6) }
        textViewIsCargo.setOnLongClickListener { setSortId(6) }
        textViewCommentTitle.setOnLongClickListener { setSortId(7) }
        textViewComment.setOnLongClickListener { setSortId(7) }

        buttonDel.setOnClickListener { returnDel() }
        buttonEdit.setOnClickListener { returnEdit() }
        buttonOk.setOnClickListener { returnIdForSort() }

        val arguments: Bundle = getArguments()
        textViewModel.text = arguments.getString("model")
        textViewColor.text = arguments.getString("color")
        textViewNum.text = arguments.getString("number")
        textViewFactory.text = arguments.getString("factory")
        textViewProductionDate.text = arguments.getString("productionDate")
        textViewSeats.text = arguments.getString("seats")
        if (arguments.getString("isCargo") == "1")
        {
            textViewIsCargo.text = "да"
        }
        else
        {
            textViewIsCargo.text = "нет"
        }
        textViewComment.text = arguments.getString("comment")
        if (arguments.getString("connection") != "1")
        {
            buttonDel.isEnabled = false
            buttonEdit.isEnabled = false
        }

        return view
    }

    override fun onAttach(activity: Activity?)
    {
        super.onAttach(activity)
        try {
            onInputListenerSortId = getActivity() as OnInputListenerSortId
        }
        catch (e: ClassCastException)
        {
            Log.e(exceptionTag, "onAttach: ClassCastException: " + e.message)
        }
    }

    private fun setSortId(id: Int): Boolean
    {
        currentIdForSort = id
        if (currentIdForSort == 0)
        {
            textViewCurrSort.text = "Сортировка по модели"
        }
        else if (currentIdForSort == 1)
        {
            textViewCurrSort.text = "Сортировка по цвету"
        }
        else if (currentIdForSort == 2)
        {
            textViewCurrSort.text = "Сортировка по номеру"
        }
        else if (currentIdForSort == 3)
        {
            textViewCurrSort.text = "Сортировка по заводу"
        }
        else if (currentIdForSort == 4)
        {
            textViewCurrSort.text = "Сортировка по дате производства"
        }
        else if (currentIdForSort == 5)
        {
            textViewCurrSort.text = "Сортировка по вместимости"
        }
        else if (currentIdForSort == 6)
        {
            textViewCurrSort.text = "Сортировка по грузовой ли?"
        }
        else if (currentIdForSort == 7)
        {
            textViewCurrSort.text = "Сортировка по описанию"
        }
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(200
            , VibrationEffect.DEFAULT_AMPLITUDE))
        return true
    }

    private fun returnIdForSort()
    {
        onInputListenerSortId.sendInputSortId(currentIdForSort)
        dialog.dismiss()
    }

    private fun returnDel()
    {
        currentIdForSort = 8
        returnIdForSort()
    }

    private fun returnEdit()
    {
        currentIdForSort = 9
        returnIdForSort()
    }
}