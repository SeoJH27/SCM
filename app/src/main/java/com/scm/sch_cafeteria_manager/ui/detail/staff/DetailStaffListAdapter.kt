package com.scm.sch_cafeteria_manager.ui.detail.staff

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.data.D_API_Response
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.emptyMEAL
import com.scm.sch_cafeteria_manager.util.utilAll.nonData

class DetailStaffListAdapter(
    items: D_API_Response,
    private val dayOfWeek: String
) : RecyclerView.Adapter<DetailStaffItemViewHolder>() {

    private var MEAL: List<meals?> = emptyList()

    init {
        items.data.dailyMeal.forEach {
            if (it.dayOfWeek == dayOfWeek) {
                if(it.meals.isEmpty()) MEAL = emptyMEAL
                else MEAL = it.meals
                Log.e("DetailStaffListAdapter", "init - $MEAL")
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailStaffItemViewHolder {
        Log.e("DetailStaffListAdapter", "onCreateViewHolder")
        if (MEAL.isEmpty()) {
            MEAL = emptyMEAL
            Log.e("DetailStaffListAdapter", "onCreateViewHolder: 데이터가 없습니다. 할당: $MEAL")
        }

        return DetailStaffItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: DetailStaffItemViewHolder, position: Int) {
        Log.e("DetailStaffListAdapter", "onBindViewHolder: ${MEAL.get(position)}")
        if (MEAL.isEmpty()) {
            MEAL = emptyMEAL
            Log.e("DetailStaffListAdapter", "onBindViewHolder: 데이터가 없습니다. 할당: $MEAL")
            holder.bind(MEAL.get(position))
        } else {
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

    fun bind(meal: meals?) {
        Log.e("DetailStaffListAdapter", "bind")

        with(binding) {
            if (meal != null) {
                txtTime.text = meal.mealType
            } else txtTime.setText(blank)
            if (meal != null) {
                txtMenu.text = meal.mainMenu?.replaceCommaToLinebreak()
            } else
                txtMenu.setText(nonData)
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