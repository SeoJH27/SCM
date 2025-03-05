package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.data.Meal
import com.scm.sch_cafeteria_manager.data.TM_API_Response
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.emptyMEAL
import com.scm.sch_cafeteria_manager.util.utilAll.nonData

class TodayMenuListAdapter(
    items: TM_API_Response, private val restaurantName: String,
) : RecyclerView.Adapter<TodayMenuItemViewHolder>() {

    private var MEAL: List<Meal?> = emptyList()

    init {
        items.data.forEach {
            if (it.restaurantName == restaurantName) {
                if (it.meals.isEmpty()) MEAL = emptyMEAL
                else MEAL = it.meals
                Log.e("TodayMenuListAdapter", "init: $restaurantName - $MEAL")
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayMenuItemViewHolder {
        Log.e("TodayMenuListAdapter", "onCreateViewHolder")
        if (MEAL.isEmpty()) {
            MEAL = emptyMEAL
            Log.e("TodayMenuListAdapter", "onCreateViewHolder: 데이터가 없습니다. 할당: $MEAL")
        }
        return TodayMenuItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: TodayMenuItemViewHolder, position: Int) {
        Log.e("TodayMenuListAdapter", "onBindViewHolder: ${MEAL.get(position)}")
        if (MEAL.isEmpty()) {
            MEAL = emptyMEAL
            Log.e("TodayMenuListAdapter", "onBindViewHolder: 데이터가 없습니다. 할당: $MEAL")
            holder.bind(MEAL.get(position))
        } else {
            holder.bind(MEAL.get(position))
        }
    }

    override fun getItemCount(): Int {
        return if (MEAL.isEmpty()) 1 else MEAL.size
    }
}

class TodayMenuItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(meal: Meal?) {
        Log.e("TodayMenuListAdapter", "bind")

        with(binding) {
            if (meal != null) {
                txtTime.text = meal.mealType
            } else {
                txtTime.setText(blank)
            }
            if (meal != null) {
                txtMenu.text = meal.mainMenu?.replaceCommaToLinebreak()
            } else {
                txtMenu.setText(nonData)
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): TodayMenuItemViewHolder {
            Log.e("TodayMenuListAdapter", "from")
            return TodayMenuItemViewHolder(
                ItemDetailMenuBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}