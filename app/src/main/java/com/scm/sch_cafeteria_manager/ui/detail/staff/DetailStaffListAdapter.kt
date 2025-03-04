package com.scm.sch_cafeteria_manager.ui.detail.staff

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.data.D_API_Response
import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak

class DetailStaffListAdapter(
    items: D_API_Response,
    private val dayOfWeek: String
) : RecyclerView.Adapter<DetailStaffItemViewHolder>() {

    private var MEAL: List<Meal?> = emptyList()

    // TODO : 비었을 때 사용. 더 적절한 용어 필요함.
    private val emptyMEAL = listOf(
        Meal(
            "",
            "", "",
            "정보 없음", "정보 없음"
        )
    )

    init {
        items.data.dailyMeals.forEach {
            if (it.dayOfWeek == dayOfWeek) {
                Log.e("DetailStaffListAdapter", "init - $MEAL")
                MEAL = it.meals
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailStaffItemViewHolder {
        Log.e("DetailStaffListAdapter", "onCreateViewHolder")
        if (MEAL.isEmpty()) {
            Log.e("DetailStaffListAdapter", "데이터가 없습니다.")
            MEAL = emptyMEAL
        }

        return DetailStaffItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: DetailStaffItemViewHolder, position: Int) {
        Log.e("DetailStaffListAdapter", "onBindViewHolder: ${MEAL.get(position)}")
        if (MEAL.isEmpty()) {
            MEAL = emptyMEAL
            holder.bind(MEAL.get(position))
        }
        else {
            holder.bind(MEAL.get(position))
        }
    }

    override fun getItemCount(): Int {
        Log.e("DetailStaffListAdapter", "getItemCount")
        return if (MEAL.isEmpty()) 1 else MEAL.size
    }


}

class DetailStaffItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(meal: Meal?) {
        Log.e("DetailStaffListAdapter", "bind")

        with(binding) {
            if (meal != null) {
                txtTime.text = meal.mealType
            } else txtTime.setText("")
            if (meal != null) {
                txtMenu.text = meal.mainMenu?.replaceCommaToLinebreak()
            } else
                txtMenu.setText("정보 없음")
        }
    }

    companion object {
        fun from(parent: ViewGroup): DetailStaffItemViewHolder {
            Log.e("DetailHs1ListAdapter", "from")
            return DetailStaffItemViewHolder(
                ItemDetailMenuBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}