package com.scm.sch_cafeteria_manager.ui.todaymenu

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.scm.sch_cafeteria_manager.R
import com.scm.sch_cafeteria_manager.data.meals
import com.scm.sch_cafeteria_manager.data.UserTodayMenuResponse
import com.scm.sch_cafeteria_manager.databinding.ItemDetailMenuBinding
import com.scm.sch_cafeteria_manager.extentions.replaceCommaToLinebreak
import com.scm.sch_cafeteria_manager.util.utilAll.blank
import com.scm.sch_cafeteria_manager.util.utilAll.combinMainAndSub
import com.scm.sch_cafeteria_manager.util.utilAll.dummyMEAL
import com.scm.sch_cafeteria_manager.util.utilAll.emptyMEAL
import com.scm.sch_cafeteria_manager.util.utilAll.mealTypeToKorean
import com.scm.sch_cafeteria_manager.util.utilAll.nonData
import java.util.Objects.isNull

class TodayMenuListAdapter(
    items: UserTodayMenuResponse, private val restaurantName: String,
) : RecyclerView.Adapter<TodayMenuItemViewHolder>() {
    private var MEAL: List<meals?> = emptyList()

    init {
        items.data.forEach {
            if (it.restaurantName == restaurantName) {
                MEAL = it.dailyMeals.meals
                Log.e("TodayMenuListAdapter", "init: $restaurantName - $MEAL")
                return@forEach
            }
        }
    }

    // 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayMenuItemViewHolder {
        Log.e("TodayMenuListAdapter", "onCreateViewHolder")
        return TodayMenuItemViewHolder.from(parent)
    }

    //할당
    override fun onBindViewHolder(holder: TodayMenuItemViewHolder, position: Int) {
        if (MEAL.isEmpty()) {
            Log.e("TodayMenuListAdapter", "onBindViewHolder: 데이터가 없습니다. 할당: $MEAL")
            holder.emptyBind()
        } else {
            holder.bind(MEAL[position])
        }
    }

    override fun getItemCount(): Int {
        Log.e("TodayMenuListAdapter", "getItemCount")
        return if (MEAL.isEmpty() || MEAL == emptyMEAL) 1 else MEAL.size
    }
}

class TodayMenuItemViewHolder(
    private val binding: ItemDetailMenuBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(meal: meals?) {
        Log.e("TodayMenuListAdapter", "bind")
        with(binding) {
            txtTime.text = mealTypeToKorean(meal?.mealType)
            txtMenu.text = combinMainAndSub(meal?.mainMenu, meal?.subMenu) ?: nonData
        }
    }

    fun emptyBind() {
        Log.e("TodayMenuListAdapter", "emptyBind")
        with(binding) {
            txtMenu.text = nonData
            txtMenu.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.grey_300
                )
            )
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